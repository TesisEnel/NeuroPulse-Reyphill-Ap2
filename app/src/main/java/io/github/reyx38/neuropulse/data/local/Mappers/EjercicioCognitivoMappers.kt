package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.EjerciciosCognitivoEntity
import io.github.reyx38.neuropulse.data.remote.dto.EjerciciosCognitivosDto

fun EjerciciosCognitivosDto.toEntity(): EjerciciosCognitivoEntity{
    return EjerciciosCognitivoEntity (
        ejercicosCognitivosId = ejercicosCognitivosId,
        titulo = titulo,
        descripcion = descripcion,
        activo = activo,

    )
}