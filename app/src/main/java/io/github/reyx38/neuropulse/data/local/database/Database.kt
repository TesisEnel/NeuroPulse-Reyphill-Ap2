package io.github.reyx38.neuropulse.data.local.database

import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.reyx38.neuropulse.data.local.dao.EjercicioCognitivoDao
import io.github.reyx38.neuropulse.data.local.dao.ProgresionSemanalDao
import io.github.reyx38.neuropulse.data.local.dao.ReflexionDao
import io.github.reyx38.neuropulse.data.local.dao.RespiracionDao
import io.github.reyx38.neuropulse.data.local.dao.SesionJuegoDao
import io.github.reyx38.neuropulse.data.local.dao.SesionRespiracionDao
import io.github.reyx38.neuropulse.data.local.dao.UsuarioDao
import io.github.reyx38.neuropulse.data.local.entities.EjerciciosCognitivoEntity
import io.github.reyx38.neuropulse.data.local.entities.InformacionRespiracionEntity
import io.github.reyx38.neuropulse.data.local.entities.ProgresionSemanalEntity
import io.github.reyx38.neuropulse.data.local.entities.ReflexionEntity
import io.github.reyx38.neuropulse.data.local.entities.RespiracionEntity
import io.github.reyx38.neuropulse.data.local.entities.SesionJuegoEntity
import io.github.reyx38.neuropulse.data.local.entities.SesionRespiracionEntity

@Database(
    entities = [
        UserEntity::class,
        ReflexionEntity::class,
        RespiracionEntity::class,
        InformacionRespiracionEntity::class,
        SesionRespiracionEntity::class,
        EjerciciosCognitivoEntity::class,
        SesionJuegoEntity::class,
        ProgresionSemanalEntity::class
    ],
    version = 13,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class NeuroPulseDb : RoomDatabase() {
    abstract fun usuarioDao() : UsuarioDao
    abstract fun reflexionDao(): ReflexionDao
    abstract fun respiracionDao(): RespiracionDao
    abstract fun sesionRespiracionDao(): SesionRespiracionDao
    abstract fun ejercicioCognitivoDao(): EjercicioCognitivoDao
    abstract fun sesionJuegoDao(): SesionJuegoDao
    abstract fun progresionsemanalDao (): ProgresionSemanalDao
}