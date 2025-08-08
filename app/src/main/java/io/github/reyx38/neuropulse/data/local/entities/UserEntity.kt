package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class UserEntity(
    @PrimaryKey
    val usuarioId : Int?,
    val nombreUsuario: String?,
    val email: String?,
    val telefono: String?,
    val token: String? = null,
    val imagenPerfil: String? = null
)