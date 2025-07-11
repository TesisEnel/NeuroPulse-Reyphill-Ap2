package io.github.reyx38.neuropulse.data.remote.dto

import java.util.Date

data class ReflexionDto(
    val reflexionId: Int,
    val usuarioId: Int,
    val estadoReflexion: String,
    val descripcion: String,
    val fechaCreacion: Date? = Date()
)