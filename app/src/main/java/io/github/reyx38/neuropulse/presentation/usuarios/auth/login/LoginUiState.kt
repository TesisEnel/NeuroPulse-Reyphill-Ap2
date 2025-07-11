package io.github.reyx38.neuropulse.presentation.usuarios.auth.login

import io.github.reyx38.neuropulse.data.local.entities.UserEntity

data class LoginUiState (
    val nombre: String = "",
    val errorNombre: String? = null,
    val password: String = "",
    val errorPassword: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: UserEntity? = null
)