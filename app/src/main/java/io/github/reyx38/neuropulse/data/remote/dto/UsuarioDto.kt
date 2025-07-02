package io.github.reyx38.neuropulse.data.remote.dto

data class UsuarioDto (
    val usuarioId : Int?,
    val nombre: String?,
    val email: String?,
    val telefono: String?,
    val password: String?,
    val token: String?,
    val imagenUrl: String? = null)