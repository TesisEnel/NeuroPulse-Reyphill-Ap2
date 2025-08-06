package io.github.reyx38.neuropulse.presentation.Respiracion.SesionRespiracion

import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import io.github.reyx38.neuropulse.data.remote.dto.SesionRespiracionDto

data class SesionRespiracionUiState (
    val usuario: UserEntity? = null,
    val listRespiracion: List<RespiracionWithInformacion> = emptyList(),
    val listaSesiones: List<SesionRespiracionDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)