package io.github.reyx38.neuropulse.data.remote.dto


data class ProgresionSemanalDto(
    val progresionSemanalId: Int?,
    val usuarioId: Int?,
    val fechaInicio: String?,
    val fechaFin: String?,
    val puntuacionSemanal: Int?,
    val estadoEmocionalSemanal: String?,
    val reflexionesEscritasSemanal: Int,
    val ejerciciosCognitivosRealizadosSemanal: Int,
    val ejerciciosCognitivosIncompletosSemanal: Int,
    val ejerciciosCognitivosTotalesSemanal: Int,
    val ejerciciosRespiracionRealizadosSemanal: Int,
    val ejerciciosRespiracionIncompletosSemanal: Int,
    val ejerciciosRespiracionTotalesSemanal: Int
)

