package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.InformacionRespiracionEntity
import io.github.reyx38.neuropulse.data.remote.dto.InformacionRespiracionesDto


fun InformacionRespiracionesDto.toEntity(): InformacionRespiracionEntity{
    return InformacionRespiracionEntity(
        idInformacionRespiracion,
        descripcion,
        tipoInformacion,
        idRespiracion
    )
}
fun InformacionRespiracionEntity.toDto(): InformacionRespiracionesDto{
    return InformacionRespiracionesDto(
        idRespiracion,
        idInformacionRespiracion,
        descripcion,
        tipoInformacion,
    )
}