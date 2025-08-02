package io.github.reyx38.neuropulse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.reyx38.neuropulse.data.local.entities.EjerciciosCognitivoEntity

@Dao
interface EjercicioCognitivoDao {
    @Query("SELECT * FROM EjerciciosCognitivos")
    suspend fun getAllEjercicioCognitivo(): List<EjerciciosCognitivoEntity>

    @Upsert
    suspend fun  saveEjerciciosCognitivos(listaEjercicios: List<EjerciciosCognitivoEntity>)

    @Query("DELETE FROM EjerciciosCognitivos")
    suspend fun deleteAllEjercicios()
}