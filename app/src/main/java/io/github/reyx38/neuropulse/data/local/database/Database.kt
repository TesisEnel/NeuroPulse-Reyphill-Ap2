package io.github.reyx38.neuropulse.data.local.database

import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.reyx38.neuropulse.data.local.dao.ReflexionDao
import io.github.reyx38.neuropulse.data.local.dao.RespiracionDao
import io.github.reyx38.neuropulse.data.local.dao.UsuarioDao
import io.github.reyx38.neuropulse.data.local.entities.InformacionRespiracionEntity
import io.github.reyx38.neuropulse.data.local.entities.ReflexionEntity
import io.github.reyx38.neuropulse.data.local.entities.RespiracionEntity

@Database(
    entities = [
        UserEntity::class,
        ReflexionEntity::class,
        RespiracionEntity::class,
        InformacionRespiracionEntity::class
    ],
    version = 8,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class NeuroPulseDb : RoomDatabase() {
    abstract fun usuarioDao() : UsuarioDao
    abstract fun reflexionDao(): ReflexionDao
    abstract fun respiracionDao(): RespiracionDao
}