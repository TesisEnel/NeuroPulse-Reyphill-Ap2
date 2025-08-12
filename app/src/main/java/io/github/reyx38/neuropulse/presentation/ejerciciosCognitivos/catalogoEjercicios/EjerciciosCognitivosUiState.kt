package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.catalogoEjercicios

import io.github.reyx38.neuropulse.data.local.entities.EjerciciosCognitivoEntity
import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto

data class EjerciciosCognitivosUiState (
    val ejercicios: List<EjerciciosCognitivoEntity> = emptyList(),
    val sesiones: List<SesionJuegosDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)