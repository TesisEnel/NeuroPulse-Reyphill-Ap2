package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import io.github.reyx38.neuropulse.data.remote.dto.UsuarioDto

fun UsuarioDto.toEntity(): UserEntity {
    return UserEntity(
        usuarioId = usuarioId,
        nombreUsuario = nombre,
        telefono = telefono,
        email = email,
        token = token,
        imagenPerfil = imagenUrl
    )
}