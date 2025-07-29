package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SesionRespiracion")
data class SesionRespiracionEntity(
    @PrimaryKey
    val sesionId: Int,
    val usuarioId: Int,
    val respiracionId: Int,
    val duracionMinuros: Int,
    val estado: String,
    val fechaRealizacion: String?
)