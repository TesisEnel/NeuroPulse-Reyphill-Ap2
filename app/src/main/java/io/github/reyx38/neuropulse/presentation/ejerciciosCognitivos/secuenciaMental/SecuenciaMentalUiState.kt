package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.secuenciaMental

import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto

data class SecuenciaMentalUiState(
    val ejerciciosCognitivosId: Int? = null,
    val rondaActual: Int = 1,
    val numerosDesordenados: List<Int> = emptyList(),
    val numerosOrdenados: List<Int> = emptyList(),
    val respuestaUsuario: List<Int> = emptyList(),
    val puntuacionTotal: Int = 0,
    val tiempoRestante: Int = 20,
    val juegoTerminado: Boolean = false,
    val mostrandoResultado: Boolean = false,
    val esRespuestaCorrecta: Boolean = false,
    val usuarioId: Int? = null,
    val error: String? = null
)


fun SecuenciaMentalUiState.toDto(): SesionJuegosDto {
    return SesionJuegosDto(
        ejercicioCognitivoId = ejerciciosCognitivosId,
        usuarioId = usuarioId,
        puntuacion = puntuacionTotal,
        completado = juegoTerminado,
        fechaRealizacion = ""
    )
}