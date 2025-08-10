package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.LogicaSombra


sealed interface LogicaSombraEvent {
    data class EjercicioCognitivoChange (val ejercicio: Int) : LogicaSombraEvent
    data class PuntacionChange (val puntacion: Int) : LogicaSombraEvent
    data class UsuarioChange (val usuarioId: Int) : LogicaSombraEvent
    data class CompletadoChange(val estado: Boolean): LogicaSombraEvent

    object Save: LogicaSombraEvent
    object JuegoIncompleto: LogicaSombraEvent
    object New: LogicaSombraEvent
}