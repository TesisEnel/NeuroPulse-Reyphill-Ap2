package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.SesionRespiracionEntity
import io.github.reyx38.neuropulse.data.remote.dto.SesionRespiracionDto

fun SesionRespiracionEntity.toDto(): SesionRespiracionDto {
    return SesionRespiracionDto (
        idSesionRespiracion = sesionId,
        idRespiracion = respiracionId,
        idUsuario = usuarioId,
        duracionMinutos = duracionMinuros,
        estado = estado,
        fechaRealizada = fechaRealizacion
    )
}

fun SesionRespiracionDto.toEntity() : SesionRespiracionEntity {
    return SesionRespiracionEntity (
        sesionId = idSesionRespiracion,
        respiracionId = idRespiracion,
        usuarioId = idUsuario,
        duracionMinuros = duracionMinutos,
        estado = estado,
        fechaRealizacion = fechaRealizada
    )
}