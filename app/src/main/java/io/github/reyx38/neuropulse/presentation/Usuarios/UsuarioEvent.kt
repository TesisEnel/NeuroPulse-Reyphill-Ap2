package io.github.reyx38.neuropulse.presentation.Usuarios

sealed interface UsuarioEvent {
    data class UsuarioIdChange (val usuarioId: Int): UsuarioEvent
    data class NombreChange(val nombre: String ): UsuarioEvent
    data class EmailChange(val email: String) : UsuarioEvent
    data class TelefonoChange(val telefono: String) : UsuarioEvent
    data class PasswordChange(val password: String) : UsuarioEvent

    data object Save: UsuarioEvent
    data object Delete: UsuarioEvent
    data object New: UsuarioEvent

}