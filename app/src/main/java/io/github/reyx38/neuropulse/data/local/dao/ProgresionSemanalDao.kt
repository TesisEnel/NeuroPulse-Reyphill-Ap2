package io.github.reyx38.neuropulse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.reyx38.neuropulse.data.local.entities.ProgresionSemanalEntity

@Dao
interface ProgresionSemanalDao {
    @Upsert
    suspend fun saveProgresion(progresionSemanal: ProgresionSemanalEntity)

    @Upsert
    suspend fun saveListProgresion(progresionSemanal: List<ProgresionSemanalEntity>)

    @Query(
        """
            Delete from progresionsemanal
        """
    )
    suspend fun deleteAll()

    @Query (
        """
            select * from progresionsemanal Where usuarioId =:id
        """
    )
    suspend fun getHistoriaUsuario(id: Int) : List<ProgresionSemanalEntity>
}