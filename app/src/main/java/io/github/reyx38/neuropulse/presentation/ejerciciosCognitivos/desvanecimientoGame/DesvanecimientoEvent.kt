package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.desvanecimientoGame


sealed interface DesvanecimientoEvent {
    data class EjercicioCognitivoChange (val ejercicio: Int) : DesvanecimientoEvent
    data class PuntacionChange (val puntacion: Int) : DesvanecimientoEvent
    data class UsuarioChange (val usuarioId: Int) : DesvanecimientoEvent
    data class CompletadoChange(val estado: Boolean): DesvanecimientoEvent

    object Save: DesvanecimientoEvent
    object JuegoIncompleto: DesvanecimientoEvent
    object New: DesvanecimientoEvent
}