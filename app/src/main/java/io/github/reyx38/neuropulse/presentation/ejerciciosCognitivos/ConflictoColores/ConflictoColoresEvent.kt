package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.ConflictoColores


sealed interface ConflictoColoresEvent {
    data class EjercicioCognitivoChange (val ejercicio: Int) : ConflictoColoresEvent
    data class PuntacionChange (val puntacion: Int) : ConflictoColoresEvent
    data class UsuarioChange (val usuarioId: Int) : ConflictoColoresEvent
    data class CompletadoChange(val estado: Boolean): ConflictoColoresEvent

    object Save: ConflictoColoresEvent
    object JuegoIncompleto: ConflictoColoresEvent
    object New: ConflictoColoresEvent
}