package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ProgresionSemanal")
data class ProgresionSemanalEntity(
    @PrimaryKey
    val progresionSemanalId: Int?,
    val usuarioId: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val puntacionSemanal: Int,
    val ejerciciosCognitivosRealizadosSemanal: Int,
    val ejerciciosCognitivosIncompletosSemanal: Int,
    val ejerciciosCognitivosTotalesSemanal: Int,
    val ejerciciosRespiracionRealizadosSemanal: Int,
    val ejerciciosRespiracionIncompletosSemanal: Int,
    val ejerciciosRespiracionTotalesSemanal: Int,
    val reflexionesEscritasSemanal: Int,
    val estadoEmocionalSemanal: String
)


