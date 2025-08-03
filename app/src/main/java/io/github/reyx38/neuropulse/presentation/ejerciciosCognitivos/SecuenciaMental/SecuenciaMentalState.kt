package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.SecuenciaMental

data class SecuenciaMentalState(
    val rondaActual: Int = 1,
    val numerosDesordenados: List<Int> = emptyList(),
    val numerosOrdenados: List<Int> = emptyList(),
    val respuestaUsuario: List<Int> = emptyList(),
    val puntuacionTotal: Int = 0,
    val tiempoRestante: Int = 30,
    val juegoTerminado: Boolean = false,
    val mostrandoResultado: Boolean = false,
    val esRespuestaCorrecta: Boolean = false
)