package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.ReflexionEntity
import io.github.reyx38.neuropulse.data.remote.dto.ReflexionDto

fun ReflexionEntity.toDto(): ReflexionDto {
    return ReflexionDto(
        reflexionId,
        usuarioId,
        descripcion,
        estadoReflexion,
        fechaCreacion
    )
}

fun ReflexionDto.toEntity(): ReflexionEntity {
    return ReflexionEntity(
        reflexionId,
        usuarioId,
        descripcion,
        estadoReflexion,
        fechaCreacion
    )
}

