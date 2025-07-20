package io.github.reyx38.neuropulse.data.local.database

import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.reyx38.neuropulse.data.local.dao.ReflexionDao
import io.github.reyx38.neuropulse.data.local.dao.UsuarioDao
import io.github.reyx38.neuropulse.data.local.entities.ReflexionEntity

@Database(
    entities = [
        UserEntity::class,
        ReflexionEntity::class
    ],
    version = 6,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class NeuroPulseDb : RoomDatabase() {
    abstract fun usuarioDao() : UsuarioDao
    abstract fun reflexionDao(): ReflexionDao
}