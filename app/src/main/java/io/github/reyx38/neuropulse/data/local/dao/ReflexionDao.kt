package io.github.reyx38.neuropulse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import io.github.reyx38.neuropulse.data.local.entities.ReflexionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflexionDao {
    @Upsert()
    suspend fun save(reflexion: ReflexionEntity)

    @Query(
        """
            select
            * from Reflexiones
            where usuarioId=:usuarioId
        """
    )
    fun listarReflexion(usuarioId: Int) : Flow<List<ReflexionEntity>>

    @Delete
    suspend fun deleteReflexion(reflexion: ReflexionEntity)
    @Query("DELETE FROM reflexiones WHERE usuarioId = :usuarioId")
    suspend fun deleteAllReflexion(usuarioId: Int)

}