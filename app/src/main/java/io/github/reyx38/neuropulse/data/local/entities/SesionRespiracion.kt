package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SesionRespiracion")
data class SesionRespiracion(
    @PrimaryKey
    val sesionId: Int?,
    val usuarioId: Int?,
    val duracionMinuros: Int?,
    val estado: String?,
    val fecha: String
)