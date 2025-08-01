package io.github.reyx38.neuropulse.presentation.reflexiones

import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import io.github.reyx38.neuropulse.data.remote.dto.ReflexionDto

data class ReflexionesUiState(
    val reflexionId: Int? = null,
    val descripcion: String? = null,
    val errorDescripcion: String? = null,
    val usuarioId: Int? = null,
    val errorUsuario: String? = null,
    val estadoReflexion: String? = "feliz",
    val errorEstado: String? = null,
    var usuario: UserEntity? = null,
    val reflexiones: List<ReflexionDto?> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)
