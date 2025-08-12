package io.github.reyx38.neuropulse.presentation.uiCommon.respiracionUtils

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
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


