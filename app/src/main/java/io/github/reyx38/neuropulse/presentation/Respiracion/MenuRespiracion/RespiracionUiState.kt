package io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion

import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion

data class RespiracionUiState(

    val isLoading: Boolean = false,
    val respiraciones: List<RespiracionWithInformacion> = emptyList(),
    val respiracion: RespiracionWithInformacion? = null,
    val error: String? = null
)