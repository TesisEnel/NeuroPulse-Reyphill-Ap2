package io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion

import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import io.github.reyx38.neuropulse.data.remote.dto.SesionRespiracionDto

data class RespiracionUiState(
    val respiracionId: Int = 0,
    var duracionMinutos: Int = 5,
    val usuarioId: Int = 0,
    val user: UserEntity? = null,
    val estado: String = "", // Completado, pausado, interrupido
    val isLoading: Boolean = false,
    val respiraciones: List<RespiracionWithInformacion> = emptyList(),
    val respiracion: RespiracionWithInformacion? = null,
    val error: String? = null
)

fun RespiracionUiState.toDto() = SesionRespiracionDto(
    idRespiracion =  respiracionId,
    idUsuario = usuarioId,
    estado = estado,
    duracionMinutos = duracionMinutos,
    fechaRealizada = "",
    idSesionRespiracion = 0
)