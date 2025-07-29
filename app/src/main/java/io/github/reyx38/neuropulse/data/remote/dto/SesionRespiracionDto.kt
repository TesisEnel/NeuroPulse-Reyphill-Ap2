package io.github.reyx38.neuropulse.data.remote.dto

data class SesionRespiracionDto (
    val idSesionRespiracion: Int,
    val idRespiracion: Int,
    val idUsuario: Int,
    val duracionMinutos: Int,
    val estado: String,
    val fechaRealizada: String?

)