package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.RespiracionEntity
import io.github.reyx38.neuropulse.data.remote.dto.RespiracionesDto

fun RespiracionEntity.toDto(): RespiracionesDto {
    return RespiracionesDto(
        idRespiracion,
        nombre,
        descripcion,
        inhalarSegundos,
        mantenerSegundos,
        exhalarSegundos,
        emptyList()
    )
}

fun RespiracionesDto.toEntity(): RespiracionEntity{
    return RespiracionEntity(
        idRespiracion,
        nombre,
        descripcion,
        exhalarSegundos,
        mantenerSegundos,
        inhalarSegundos
    )
}