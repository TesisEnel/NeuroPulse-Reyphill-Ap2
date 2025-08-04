package io.github.reyx38.neuropulse.data.remote.dto

data class SesionJuegosDto(
    val sesionJuegoId: Int? = null,
    val usuarioId: Int? = null,
    val ejercicioCognitivoId: Int? = null,
    val fechaRealizacion: String? = null,
    val completado: Boolean = false,
    val puntuacion: Int
)