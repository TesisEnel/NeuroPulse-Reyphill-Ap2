package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SesionJuegos")
data class SesionJuegoEntity(
    @PrimaryKey
    val sesionJuegoId: Int? = null,
    val usuarioId: Int?,
    val ejercicioId: Int?,
    val fechaRealizacion: String?,
    val completado: Boolean,
    val puntuacion: Int
)