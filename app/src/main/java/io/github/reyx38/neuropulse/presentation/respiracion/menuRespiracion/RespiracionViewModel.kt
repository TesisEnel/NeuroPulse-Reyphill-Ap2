package io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.local.enum.EstadosRespiracion
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.repository.AuthRepository
import io.github.reyx38.neuropulse.data.repository.RespiracionRepository
import io.github.reyx38.neuropulse.data.repository.SesionRespository
import io.github.reyx38.neuropulse.presentation.uiCommon.respiracionUtils.BreathingSessionManager
import io.github.reyx38.neuropulse.presentation.uiCommon.timerUtils.msToMinutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RespiracionViewModel @Inject constructor(
    private val repository: RespiracionRepository,
    private val authRepository: AuthRepository,
    private val sesionRespository: SesionRespository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RespiracionUiState())
    val uiState = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _currentPhase = MutableStateFlow(EstadosRespiracion.INHALING)
    val currentPhase = _currentPhase.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _remainingTimeMs = MutableStateFlow(0L)
    val remainingTimeMs = _remainingTimeMs.asStateFlow()

    // ✅ Un solo manager para toda la sesión
    private val sessionManager = BreathingSessionManager()
    private var breathingJob: Job? = null

    // ✅ Callbacks para el SessionManager
    private val sessionCallbacks = BreathingSessionManager.SessionCallbacks(
        onProgressUpdate = { _progress.value = it },
        onPhaseChange = { _currentPhase.value = it },
        onRemainingTimeUpdate = { _remainingTimeMs.value = it },
        onSessionComplete = { finalizarSesion() },
        onInvalidConfiguration = { handleInvalidConfiguration() }
    )

    init {
        cargarRespiraciones()
        getUsuario()
    }

    fun getUsuario() = viewModelScope.launch {
        authRepository.getUsuario()?.usuarioId?.let { userId ->
            _uiState.update { it.copy(usuarioId = userId, user = authRepository.getUsuario()) }
        }
    }

    fun onEvent(event: RespiracionUiEvent) {
        when (event) {
            is RespiracionUiEvent.DuracionMinutos -> setDuration(event.minutos)
            is RespiracionUiEvent.RespiracionChange -> setRespiracion(event.respiracionId)
            is RespiracionUiEvent.UsuarioChange -> setUsuario(event.usuarioId)
            is RespiracionUiEvent.EstadoChange -> setEstado(event.estado)
            RespiracionUiEvent.Save -> save()
            RespiracionUiEvent.New -> resetSesion()
        }
    }

    private fun setUsuario(usuarioId: Int) = _uiState.update { it.copy(usuarioId = usuarioId) }
    private fun setEstado(estado: String) = _uiState.update { it.copy(estado = estado) }
    private fun setRespiracion(respiracionId: Int) = _uiState.update { it.copy(respiracionId = respiracionId) }

    private fun setDuration(minutos: Int) {
        sessionManager.initializeSession(minutos) // ✅ SessionManager maneja la inicialización
        _remainingTimeMs.value = sessionManager.getRemainingTime()
        _uiState.update { it.copy(duracionMinutos = minutos) }
    }

    private fun initializeTotalTime() {
        val minutos = _uiState.value.duracionMinutos
        if (minutos > 0) {
            setDuration(minutos)
        }
    }

    fun buscarRespiracion(respiracionId: Int) = viewModelScope.launch {
        val respiracion = repository.find(respiracionId)
        _uiState.update { it.copy(respiracion = respiracion) }
        initializeTotalTime()
    }

    fun cargarRespiraciones() = viewModelScope.launch {
        loadRespiracionesFromRepositoryOrSync()
    }

    private suspend fun loadRespiracionesFromRepositoryOrSync() {
        val list = repository.obtenerRespiraciones()
        if (list.isNotEmpty()) {
            _uiState.update { it.copy(respiraciones = list) }
        } else {
            syncRespiracionesFromApi()
        }
    }

    private suspend fun syncRespiracionesFromApi() {
        repository.sincronizarRespiracionesDesdeApi().collect { resultado ->
            val currentState = _uiState.value
            val newState = when (resultado) {
                is Resource.Loading -> currentState.copy(isLoading = true, error = null)
                is Resource.Success -> currentState.copy(
                    isLoading = false,
                    respiraciones = resultado.data ?: emptyList(),
                    error = null
                )
                is Resource.Error -> currentState.copy(
                    isLoading = false,
                    error = resultado.message ?: "Error desconocido"
                )
            }
            _uiState.value = newState
        }
    }

    // Ya no necesitamos esta función
    // private fun updateStateFromResource...

    fun togglePlayPause() {
        if (sessionManager.getTotalTime() == 0L) initializeTotalTime()

        _isRunning.value = !_isRunning.value

        if (_isRunning.value) {
            startOrResumeSession()
        } else {
            pauseSession()
        }
    }

    private fun startOrResumeSession() {
        if (sessionManager.getElapsedTime() == 0L) {
            sessionManager.startSession()
        } else {
            sessionManager.resumeSession()
        }

        _uiState.update { it.copy(estado = "En progreso") }
        executeBreathingSession()
    }

    private fun pauseSession() {
        breathingJob?.cancel()
        _uiState.update { it.copy(estado = "Pausado") }
    }

    fun resetSesion() {
        breathingJob?.cancel()
        sessionManager.resetSession() // ✅ SessionManager maneja el reset
        resetUIStates()
    }

    private fun resetUIStates() {
        _isRunning.value = false
        _progress.value = 0f
        _currentPhase.value = EstadosRespiracion.INHALING
        initializeTotalTime()
        _uiState.update {
            it.copy(estado = "Detenido", duracionMinutos = msToMinutes(sessionManager.getTotalTime()))
        }
    }

    private fun executeBreathingSession() {
        breathingJob?.cancel()

        val respiracion = _uiState.value.respiracion ?: return

        breathingJob = viewModelScope.launch {
            // ✅ SessionManager maneja toda la lógica compleja
            sessionManager.executeBreathingCycle(
                respiracion = respiracion,
                isRunning = _isRunning,
                callbacks = sessionCallbacks
            )
        }
    }

    private fun handleInvalidConfiguration() {
        _uiState.update { it.copy(estado = "Configuración inválida") }
        _isRunning.value = false
    }

    private fun finalizarSesion() {
        _isRunning.value = false

        val estado = if (sessionManager.isComplete()) {
            _progress.value = 0f
            _currentPhase.value = EstadosRespiracion.INHALING
            "Finalizado"
        } else {
            "Pausado"
        }

        _uiState.update { it.copy(estado = estado) }
    }

    private fun save() = viewModelScope.launch {
        val result = sesionRespository.save(_uiState.value.toDto())
        val message = when (result) {
            is Resource.Success -> "Reflexión guardada correctamente"
            is Resource.Error -> result.message ?: "Error al guardar la reflexión"
            else -> null
        }
        _uiState.update { it.copy(error = message) }
    }
}