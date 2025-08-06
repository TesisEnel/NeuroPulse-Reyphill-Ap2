package io.github.reyx38.neuropulse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.reyx38.neuropulse.data.local.entities.SesionRespiracionEntity


@Dao
interface SesionRespiracionDao {
    @Upsert
    suspend fun saveSesion(sesionRespiracionDao: SesionRespiracionEntity)

    @Query(
        """
            Select *
            From SesionRespiracion
            where UsuarioId =:usuarioId
        """
    )
    suspend fun getSesiones(usuarioId: Int) : List<SesionRespiracionEntity>
}