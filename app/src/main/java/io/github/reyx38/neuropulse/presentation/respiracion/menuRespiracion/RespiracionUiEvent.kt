package io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion


sealed interface RespiracionUiEvent {
    data class RespiracionChange (val respiracionId: Int) : RespiracionUiEvent
    data class DuracionMinutos (val minutos: Int) : RespiracionUiEvent
    data class UsuarioChange (val usuarioId: Int) : RespiracionUiEvent
    data class EstadoChange(val estado: String): RespiracionUiEvent

    object Save: RespiracionUiEvent
    object New: RespiracionUiEvent
}