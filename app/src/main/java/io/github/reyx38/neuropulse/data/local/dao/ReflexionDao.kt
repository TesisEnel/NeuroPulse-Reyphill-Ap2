package io.github.reyx38.neuropulse.data.local.dao

import androidx.room.Dao
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
    fun listarReflexion(usuarioId: Int): Flow<List<ReflexionEntity>>

    @Query(
        """
            select
            * from Reflexiones
            where reflexionId=:reflexionId
            limit 1
        """
    )
    suspend fun find(reflexionId: Int): ReflexionEntity

    @Query("""Delete from reflexiones where reflexionId=:reflexionId""")
    suspend fun deleteReflexion(reflexionId: Int)

    @Query("DELETE FROM reflexiones WHERE usuarioId = :usuarioId")
    suspend fun deleteAllReflexion(usuarioId: Int)

}