package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.desvanecimientoGame

import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto


data class DesvanecimientoUiState(
    val error: String? = null,
    val ejerciciosCognitivosId: Int? = null,
    val currentRound: Int = 1,
    val totalRounds: Int = 3,
    val puntuacionTotal: Int = 0,
    val isGameActive: Boolean = false,
    val juegoTerminado: Boolean = false,
    val showImages: Boolean = false,
    val imagePositions: List<Int> = emptyList(),
    val selectedPositions: List<Int> = emptyList(),
    val isAnswering: Boolean = false,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val timeRemaining: Int = 0,
    val isStarted: Boolean = false,
    val usuarioId: Int?= null,
)

fun DesvanecimientoUiState.toDto(): SesionJuegosDto {
    return SesionJuegosDto(
        ejercicioCognitivoId = ejerciciosCognitivosId,
        usuarioId = usuarioId,
        puntuacion = puntuacionTotal,
        completado = juegoTerminado,
        fechaRealizacion = ""
    )
}
