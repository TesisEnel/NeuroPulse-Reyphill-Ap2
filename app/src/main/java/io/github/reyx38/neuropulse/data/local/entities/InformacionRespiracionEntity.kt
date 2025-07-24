package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Informacion_Respiraciones")
data class InformacionRespiracionEntity(
    @PrimaryKey val idInformacionRespiracion: Int,
    val descripcion: String,
    val tipoInformacion: String,
    val idRespiracion: Int
)