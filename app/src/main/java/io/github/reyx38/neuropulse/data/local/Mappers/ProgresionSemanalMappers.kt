package io.github.reyx38.neuropulse.data.local.Mappers

import io.github.reyx38.neuropulse.data.local.entities.ProgresionSemanalEntity
import io.github.reyx38.neuropulse.data.remote.dto.ProgresionSemanalDto


fun ProgresionSemanalEntity.toDto(): ProgresionSemanalDto {
    return ProgresionSemanalDto(
        progresionSemanalId = progresionSemanalId,
        usuarioId = usuarioId,
        puntuacionSemanal = puntacionSemanal,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        estadoEmocionalSemanal = estadoEmocionalSemanal,
        reflexionesEscritasSemanal = reflexionesEscritasSemanal,
        ejerciciosCognitivosTotalesSemanal = ejerciciosCognitivosTotalesSemanal,
        ejerciciosCognitivosIncompletosSemanal = ejerciciosCognitivosIncompletosSemanal,
        ejerciciosCognitivosRealizadosSemanal = ejerciciosCognitivosRealizadosSemanal,
        ejerciciosRespiracionTotalesSemanal = ejerciciosRespiracionTotalesSemanal,
        ejerciciosRespiracionIncompletosSemanal = ejerciciosRespiracionTotalesSemanal,
        ejerciciosRespiracionRealizadosSemanal = ejerciciosRespiracionRealizadosSemanal
    )
}

fun ProgresionSemanalDto.toEntity(): ProgresionSemanalEntity{
    return ProgresionSemanalEntity(
        progresionSemanalId = progresionSemanalId,
        usuarioId = usuarioId ?: 0,
        puntacionSemanal = puntuacionSemanal ?: 0,
        fechaInicio = fechaInicio ?: "",
        fechaFin = fechaFin ?: "",
        estadoEmocionalSemanal = estadoEmocionalSemanal ?: "",
        reflexionesEscritasSemanal = reflexionesEscritasSemanal,
        ejerciciosCognitivosTotalesSemanal = ejerciciosCognitivosTotalesSemanal,
        ejerciciosCognitivosIncompletosSemanal = ejerciciosCognitivosIncompletosSemanal,
        ejerciciosCognitivosRealizadosSemanal = ejerciciosCognitivosRealizadosSemanal,
        ejerciciosRespiracionTotalesSemanal = ejerciciosRespiracionTotalesSemanal,
        ejerciciosRespiracionIncompletosSemanal = ejerciciosRespiracionTotalesSemanal,
        ejerciciosRespiracionRealizadosSemanal = ejerciciosRespiracionRealizadosSemanal
    )
}