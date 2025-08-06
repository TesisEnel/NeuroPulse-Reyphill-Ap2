package io.github.reyx38.neuropulse.presentation.progresionSemanal

import io.github.reyx38.neuropulse.data.remote.dto.ProgresionSemanalDto

// ProgresionSemanalState.kt
data class ProgresionSemanalUiState(
    val progresionesHistorial: List<ProgresionSemanalDto> = emptyList(),
    val progresionActual: ProgresionSemanalDto? = null,
    val usuarioId: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)