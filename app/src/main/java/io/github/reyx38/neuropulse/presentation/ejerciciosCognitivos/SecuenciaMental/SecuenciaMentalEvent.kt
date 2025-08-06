package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.SecuenciaMental

import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.DesvanecimientoGame.DesvanecimientoEvent

sealed interface SecuenciaMentalEvent {
    data class EjercicioCognitivoChange (val ejercicio: Int) : SecuenciaMentalEvent
    data class PuntacionChange (val puntacion: Int) : SecuenciaMentalEvent
    data class UsuarioChange (val usuarioId: Int) : SecuenciaMentalEvent
    data class CompletadoChange(val estado: Boolean): SecuenciaMentalEvent

    object Save: SecuenciaMentalEvent
    object JuegoIncompleto: SecuenciaMentalEvent
    object New: SecuenciaMentalEvent
}