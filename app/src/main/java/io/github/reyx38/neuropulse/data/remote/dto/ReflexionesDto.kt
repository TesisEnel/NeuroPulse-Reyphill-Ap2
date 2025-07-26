package io.github.reyx38.neuropulse.data.remote.dto


data class ReflexionDto(
    val reflexionId: Int,
    val usuarioId: Int,
    val estadoReflexion: String,
    val descripcion: String,
    val fechaCreacion: String? = null
)
