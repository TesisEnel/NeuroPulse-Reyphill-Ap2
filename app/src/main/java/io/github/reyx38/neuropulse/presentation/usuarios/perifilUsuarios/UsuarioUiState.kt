package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios

import io.github.reyx38.neuropulse.data.local.entities.UserEntity

data class UsuarioUiState (
    val usuarioId: Int? = null,
    val nombre: String? = null,
    val errorNombre: String? = null,
    val email: String? = null,
    val errorEmail: String? = null,
    val telefono:  String? = null,
    val errorTelefono: String? = null,
    val imagen: String? = null,
    val isUpdating: Boolean = false,
    val updateMessage: String? = null,
    val isError: Boolean = false,
    val usuario: UserEntity? = null
)