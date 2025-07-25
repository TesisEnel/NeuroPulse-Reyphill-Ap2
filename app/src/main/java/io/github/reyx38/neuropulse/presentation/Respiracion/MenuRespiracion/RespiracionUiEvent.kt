package io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion

import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioEvent

sealed interface RespiracionUiEvent {
    data class RespiracionChange (val respiracionId: Int) : RespiracionUiEvent
    data class DuracionMinutos (val minutos: Int) : RespiracionUiEvent
    data class UsuarioChange (val usuarioId: Int) : RespiracionUiEvent

    object Save: RespiracionUiEvent
    object New: RespiracionUiEvent
}