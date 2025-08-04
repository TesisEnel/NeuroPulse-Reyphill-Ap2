package io.github.reyx38.neuropulse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.reyx38.neuropulse.data.local.entities.SesionJuegoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SesionJuegoDao {
    @Upsert
    suspend fun saveSesionJuego(sesionJuegoEntity: SesionJuegoEntity)

    @Query(
        """
            Select * 
            From SesionJuegos
            where usuarioId =:usuarioId
        """
    )
    suspend fun getAllSesionJuegos(usuarioId: Int) : List<SesionJuegoEntity>

    @Query(
        """
            Delete from SesionJuegos where UsuarioId =:usuarioId
        """
    )
    suspend fun deleteAllSesiones(usuarioId: Int)

}