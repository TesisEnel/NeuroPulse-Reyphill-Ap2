package io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion

import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import io.github.reyx38.neuropulse.data.remote.dto.SesionRespiracionDto

data class RespiracionUiState(
    val respiracionId: Int? = null,
    var duracionMinutos: Int = 5,
    val usuarioId: Int? = null,
    val user: UserEntity? = null,
    val estado: String? = null, // Completado, pausado, interrupido
    val isLoading: Boolean = false,
    val respiraciones: List<RespiracionWithInformacion> = emptyList(),
    val respiracion: RespiracionWithInformacion? = null,
    val error: String? = null
)

fun RespiracionUiState.toDto() = SesionRespiracionDto(
    respiracionid =  respiracionId,
    usuarioId = usuarioId,
    estado = estado,
    duracionMinutos = duracionMinutos,
    sesionId = 0
)