package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios

sealed interface UsuarioEvent {
    data class UsuarioIdChange (val usuarioId: Int): UsuarioEvent
    data class NombreChange(val nombre: String ): UsuarioEvent
    data class EmailChange(val email: String) : UsuarioEvent
    data class TelefonoChange(val telefono: String) : UsuarioEvent
    data class ImagenChange(val imagen: String?) : UsuarioEvent


    data object Save: UsuarioEvent
    data object Delete: UsuarioEvent
    data object New: UsuarioEvent
    object DismissMessage : UsuarioEvent

}