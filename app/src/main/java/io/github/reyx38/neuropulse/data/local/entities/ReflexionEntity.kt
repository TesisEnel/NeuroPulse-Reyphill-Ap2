package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Reflexiones")
data class ReflexionEntity(
    @PrimaryKey
    val reflexionId: Int,
    val usuarioId: Int,
    val estadoReflexion: String,
    val descripcion: String,
    val fechaCreacion: String?
)

