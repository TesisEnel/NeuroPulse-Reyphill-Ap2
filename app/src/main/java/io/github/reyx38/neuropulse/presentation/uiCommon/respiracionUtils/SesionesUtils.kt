package io.github.reyx38.neuropulse.presentation.uiCommon.respiracionUtils

import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.data.local.enum.EstadosRespiracion
import io.github.reyx38.neuropulse.presentation.uiCommon.timerUtils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class BreathingSessionManager {

    private var timerState = TimerState()
    private var totalTimeMs = 0L

    data class SessionCallbacks(
        val onProgressUpdate: (Float) -> Unit,
        val onPhaseChange: (EstadosRespiracion) -> Unit,
        val onRemainingTimeUpdate: (Long) -> Unit,
        val onSessionComplete: () -> Unit,
        val onInvalidConfiguration: () -> Unit
    )

    data class SessionState(
        val isRunning: Boolean,
        val currentPhase: EstadosRespiracion,
        val progress: Float,
        val remainingTimeMs: Long
    )

    fun initializeSession(durationMinutes: Int) {
        totalTimeMs = minutesToMs(durationMinutes)
        timerState = timerState.reset()
    }

    fun startSession() {
        timerState = timerState.initSession()
    }

    fun resumeSession() {
        timerState = timerState.resume()
    }

    fun resetSession() {
        timerState = timerState.reset()
        totalTimeMs = 0L
    }

    fun getCurrentSessionState(): SessionState {
        return SessionState(
            isRunning = false, // Se actualiza externamente
            currentPhase = EstadosRespiracion.INHALING, // Se actualiza durante el ciclo
            progress = 0f, // Se calcula durante el ciclo
            remainingTimeMs = calculateRemainingTime(timerState.elapsedTimeMs, totalTimeMs)
        )
    }

    /**
     * Ejecuta un ciclo completo de respiración
     * Devuelve true si debe continuar, false si debe terminar
     */
    suspend fun executeBreathingCycle(
        respiracion: RespiracionWithInformacion,
        isRunning: MutableStateFlow<Boolean>,
        callbacks: SessionCallbacks
    ): Boolean {
        // Validar configuración
        if (!isValidBreathingConfiguration(respiracion)) {
            callbacks.onInvalidConfiguration()
            return false
        }

        // Construir fases
        val phases = buildBreathingPhases(respiracion)
        var currentPhaseIndex = determineCurrentPhase(phases, timerState.elapsedInCurrentPhase)

        // Inicializar fase actual
        callbacks.onPhaseChange(phases[currentPhaseIndex].first)

        // Loop principal de la sesión
        while (isRunning.value && !isSessionComplete()) {
            // Actualizar tiempos
            timerState = timerState.updateElapsed()
            val (_, phaseDurationMs) = phases[currentPhaseIndex]

            // Calcular y reportar progreso
            val progress = calculateProgress(timerState.elapsedInCurrentPhase, phaseDurationMs)
            callbacks.onProgressUpdate(progress)

            // Calcular y reportar tiempo restante
            val remainingTime = calculateRemainingTime(timerState.elapsedTimeMs, totalTimeMs)
            callbacks.onRemainingTimeUpdate(remainingTime)

            // Verificar cambio de fase
            if (shouldAdvancePhase(timerState.elapsedInCurrentPhase, phaseDurationMs)) {
                currentPhaseIndex = advanceToNextPhase(phases, currentPhaseIndex, callbacks)
            }

            delay(16L) // ~60 FPS
        }

        // Sesión terminada
        callbacks.onSessionComplete()
        return true
    }

    private fun advanceToNextPhase(
        phases: List<Pair<EstadosRespiracion, Long>>,
        currentIndex: Int,
        callbacks: SessionCallbacks
    ): Int {
        val nextIndex = (currentIndex + 1) % phases.size

        // Cambiar a la nueva fase
        callbacks.onPhaseChange(phases[nextIndex].first)

        // Resetear timer de fase
        timerState = timerState.resetPhase()
        callbacks.onProgressUpdate(0f)

        return nextIndex
    }

    private fun isSessionComplete(): Boolean {
        return isTimeComplete(timerState.elapsedTimeMs, totalTimeMs)
    }

    fun getElapsedTime(): Long = timerState.elapsedTimeMs
    fun getTotalTime(): Long = totalTimeMs
    fun getRemainingTime(): Long = calculateRemainingTime(timerState.elapsedTimeMs, totalTimeMs)
    fun isComplete(): Boolean = isSessionComplete()
}