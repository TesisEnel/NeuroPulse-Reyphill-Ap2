package io.github.reyx38.neuropulse.presentation.progresionSemanal

import io.github.reyx38.neuropulse.data.local.entities.ProgresionSemanalEntity
import io.github.reyx38.neuropulse.data.remote.dto.ProgresionSemanalDto

sealed class ProgresionSemanalEvent {
    data class LoadProgresiones(val usuarioId: Int) : ProgresionSemanalEvent()
    data class LoadProgresionActual(val usuarioId: Int) : ProgresionSemanalEvent()
    data class SaveProgresion(val progresion: ProgresionSemanalDto) : ProgresionSemanalEvent()
    data class DeleteProgresion(val id: Int) : ProgresionSemanalEvent()
}

