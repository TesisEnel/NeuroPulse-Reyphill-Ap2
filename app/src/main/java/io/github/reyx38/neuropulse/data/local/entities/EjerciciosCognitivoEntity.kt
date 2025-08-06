package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EjerciciosCognitivos")
data class EjerciciosCognitivoEntity (
    @PrimaryKey
    val ejercicosCognitivosId: Int? = null,
    val titulo: String,
    val descripcion: String,
    val activo: Boolean
)