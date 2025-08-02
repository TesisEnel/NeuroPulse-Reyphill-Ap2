package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos

import io.github.reyx38.neuropulse.data.local.entities.EjerciciosCognitivoEntity

data class EjerciciosCognitivosUiState (
    val ejercicios: List<EjerciciosCognitivoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)