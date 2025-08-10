package io.github.reyx38.neuropulse.presentation.UiCommon.RespiracionUtils

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import io.github.reyx38.neuropulse.data.local.enum.EstadosRespiracion
import io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion.RespiracionUiState
import io.github.reyx38.neuropulse.presentation.Respiracion.SesionRespiracion.BreathingPhase

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

fun obtenerFasesActuales(phases: List<BreathingPhase>, currentPhase: EstadosRespiracion): BreathingPhase {
    return phases.find { phase ->
        when (currentPhase) {
            EstadosRespiracion.INHALING -> phase.name == "Inhala"
            EstadosRespiracion.HOLDING -> phase.name == "Mantén"
            EstadosRespiracion.EXHALING -> phase.name == "Exhala"
        }
    } ?: BreathingPhase("Detenido", 0L, Color.Gray)
}