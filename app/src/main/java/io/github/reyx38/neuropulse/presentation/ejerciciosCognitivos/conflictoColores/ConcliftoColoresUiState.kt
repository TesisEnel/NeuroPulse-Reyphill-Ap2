package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.conflictoColores

import androidx.compose.ui.graphics.Color
import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto

data class ColorInfo(
    val nombre: String,
    val color: Color
)

data class ConflictoColoresUiState(
    val ejerciciosCognitivosId: Int? = null,
    val rondaActual: Int = 1,
    val palabrasCompletadas: Int = 0,
    val palabrasPorRonda: Int = 4,
    val palabraActual: String = "",
    val colorPalabra: Color = Color.Black,
    val coloresDisponibles: List<ColorInfo> = emptyList(),
    val respuestaCorrecta: String = "",
    val puntuacionTotal: Int = 0,
    val tiempoRestante: Int = 20,
    val juegoTerminado: Boolean = false,
    val mostrandoResultado: Boolean = false,
    val esRespuestaCorrecta: Boolean = false,
    val modoJuego: String = "COLOR",
    val usuarioId: Int? = null,
    val error: String? = null
)

fun ConflictoColoresUiState.toDto(): SesionJuegosDto {
    return SesionJuegosDto(
        ejercicioCognitivoId = ejerciciosCognitivosId,
        usuarioId = usuarioId,
        puntuacion = puntuacionTotal,
        completado = juegoTerminado,
        fechaRealizacion = ""
    )
}
