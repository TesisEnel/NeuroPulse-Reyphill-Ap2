package io.github.reyx38.neuropulse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import io.github.reyx38.neuropulse.data.local.entities.InformacionRespiracionEntity
import io.github.reyx38.neuropulse.data.local.entities.RespiracionEntity
import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion

@Dao
interface RespiracionDao {

    @Transaction
    @Query("SELECT * FROM Respiraciones")
    suspend fun getAllRespiraciones(): List<RespiracionWithInformacion>

    @Transaction
    @Query
        ("""
            Select *
            From respiraciones 
            where idRespiracion =:respiracionId
            Limit 1
        """)
    suspend fun find(respiracionId: Int): RespiracionWithInformacion

    @Upsert
    suspend fun saveRespiraciones(listaRespiraciones: List<RespiracionEntity>)

    @Upsert
    suspend fun  saveInformaciones(listaInformaciones: List<InformacionRespiracionEntity>)
}