package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.logicaSombra

import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto

data class ShadowShape(
    val id: Int,
    val shape: ShapeType,
    val rotation: Float = 0f,
    val isCorrectShadow: Boolean = false
)

enum class ShapeType {
    CIRCLE, SQUARE, TRIANGLE, DIAMOND, STAR, HEXAGON
}

data class GameRound(
    val roundNumber: Int,
    val originalShape: ShadowShape,
    val shadowOptions: List<ShadowShape>,
    val correctAnswerId: Int
)

data class LogicaSombraUiState(
    val ejercicioCognitivoId: Int? = null,
    val usuarioId: Int? = null,
    val currentRound: Int = 1,
    val totalRounds: Int = 3,
    val score: Int = 0,
    val isGameCompleted: Boolean = false,
    val currentGameRound: GameRound? = null,
    val selectedAnswerId: Int? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val roundScore: Int = 0,
    val error: String? = null
)

fun LogicaSombraUiState.toDto(): SesionJuegosDto {
    return SesionJuegosDto(
        ejercicioCognitivoId = ejercicioCognitivoId,
        usuarioId = usuarioId,
        puntuacion = score,
        completado = isGameCompleted,
        fechaRealizacion = ""
    )
}