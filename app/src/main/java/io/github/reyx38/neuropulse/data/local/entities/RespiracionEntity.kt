package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Respiraciones")
data class RespiracionEntity(
    @PrimaryKey val idRespiracion: Int,
    val nombre: String,
    val descripcion: String,
    val inhalarSegundos: Int,
    val exhalarSegundos: Int,
    val mantenerSegundos: Int
)