package io.github.reyx38.neuropulse.presentation.Usuarios

import io.github.reyx38.neuropulse.data.local.entities.UserEntity

data class UsuarioUiState (
    val usuarioId: Int? = null,
    val nombre: String? = null,
    val errorNombre: String? = null,
    val email: String? = null,
    val errorEmail: String? = null,
    val telefono:  String? = null,
    val errorTelefono: String? = null,
    val password: String? = null,
    val errorPassword: String? = null,
    val Usuarios: List<UserEntity> = emptyList()
)