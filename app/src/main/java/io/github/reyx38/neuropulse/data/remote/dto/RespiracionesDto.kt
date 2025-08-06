package io.github.reyx38.neuropulse.data.remote.dto

data class RespiracionesDto(
    val idRespiracion: Int,

    val nombre: String,

    val descripcion: String,

    val inhalarSegundos: Int,

    val exhalarSegundos: Int,

    val mantenerSegundos: Int,

    val informacionRespiracion: List<InformacionRespiracionesDto>
)