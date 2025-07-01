package io.github.reyx38.neuropulse.data.remote.dto

data class UsuarioDto (
    val usuarioId : Int?,
    val nombreUsuario: String?,
    val email: String?,
    val token: String?,
    val imagenPerfil: String? = null)