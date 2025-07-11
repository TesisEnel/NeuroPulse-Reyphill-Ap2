package io.github.reyx38.neuropulse.presentation.usuarios.auth.login

sealed interface LoginUiEvent {
    data class NombreChange(val nombre: String): LoginUiEvent
    data class PasswordChange(val password: String): LoginUiEvent

    data object Login: LoginUiEvent
    data object New: LoginUiEvent
}