package io.github.reyx38.neuropulse.presentation.uiCommon.respiracionUtils

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.data.local.enum.EstadosRespiracion
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.RespiracionUiState
import io.github.reyx38.neuropulse.presentation.respiracion.sesionRespiracion.BreathingPhase

fun construirFases (pattern: RespiracionUiState, colors: ColorScheme): List<BreathingPhase> {
    return listOfNotNull(
        pattern.respiracion?.respiracion?.inhalarSegundos?.takeIf { it > 0 }?.let {
            BreathingPhase("Inhala", it * 1000L, colors.tertiary)
        },
        pattern.respiracion?.respiracion?.mantenerSegundos?.takeIf { it > 0 }?.let {
            BreathingPhase("Mantén", it * 1000L, colors.secondary)
        },
        pattern.respiracion?.respiracion?.exhalarSegundos?.takeIf { it > 0 }?.let {
            BreathingPhase("Exhala", it * 1000L, colors.primary)
        }
    )
}

fun obtenerFasesActuales(fases: List<BreathingPhase>, faseActual: EstadosRespiracion): BreathingPhase {
    return fases.find { phase ->
        when (faseActual) {
            EstadosRespiracion.INHALING -> phase.name == "Inhala"
            EstadosRespiracion.HOLDING -> phase.name == "Mantén"
            EstadosRespiracion.EXHALING -> phase.name == "Exhala"
        }
    } ?: BreathingPhase("Detenido", 0L, Color.Gray)
}

// ✅ Nueva función: Construir fases para el ViewModel (sin colores)
fun buildBreathingPhases(respiracion: RespiracionWithInformacion): List<Pair<EstadosRespiracion, Long>> {
    return buildList {
        if (respiracion.respiracion.inhalarSegundos > 0) {
            add(EstadosRespiracion.INHALING to respiracion.respiracion.inhalarSegundos * 1000L)
        }
        if (respiracion.respiracion.mantenerSegundos > 0) {
            add(EstadosRespiracion.HOLDING to respiracion.respiracion.mantenerSegundos * 1000L)
        }
        if (respiracion.respiracion.exhalarSegundos > 0) {
            add(EstadosRespiracion.EXHALING to respiracion.respiracion.exhalarSegundos * 1000L)
        }
    }
}

// ✅ Nueva función: Validar configuración de respiración
fun isValidBreathingConfiguration(respiracion: RespiracionWithInformacion?): Boolean {
    if (respiracion == null) return false
    val phases = buildBreathingPhases(respiracion)
    return phases.isNotEmpty()
}

// ✅ Nueva función: Determinar fase actual basada en tiempo transcurrido
fun determineCurrentPhase(phases: List<Pair<EstadosRespiracion, Long>>, elapsedInPhase: Long): Int {
    if (elapsedInPhase == 0L) return 0

    for (i in phases.indices) {
        if (elapsedInPhase < phases[i].second) {
            return i
        }
    }
    return 0
}


