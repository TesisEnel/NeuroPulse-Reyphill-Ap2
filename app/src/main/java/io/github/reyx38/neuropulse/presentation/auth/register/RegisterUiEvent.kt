package io.github.reyx38.neuropulse.presentation.auth.register


sealed interface RegisterUiEvent {
    data class NombreChange(val nombre: String ): RegisterUiEvent
    data class EmailChange(val email: String) : RegisterUiEvent
    data class TelefonoChange(val telefono: String) : RegisterUiEvent
    data class PasswordChange(val password: String) : RegisterUiEvent
    data class PasswordConfirmChange(val passwordConfirm: String) : RegisterUiEvent


    data object Save: RegisterUiEvent
    data object New: RegisterUiEvent
}