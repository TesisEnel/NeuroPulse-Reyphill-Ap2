package io.github.reyx38.neuropulse.data.local.database

import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.reyx38.neuropulse.data.local.dao.UsuarioDao

@Database(
    entities = [
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NeuroPulseDb : RoomDatabase() {
    abstract fun usuarioDao() : UsuarioDao
}