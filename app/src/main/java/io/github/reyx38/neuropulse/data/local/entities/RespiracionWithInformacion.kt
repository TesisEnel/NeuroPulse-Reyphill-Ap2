package io.github.reyx38.neuropulse.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation


data class RespiracionWithInformacion(
    @Embedded val respiracion: RespiracionEntity,

    @Relation(
        parentColumn = "idRespiracion",
        entityColumn = "idRespiracion"
    )
    val informacionRespiracion: List<InformacionRespiracionEntity>
)