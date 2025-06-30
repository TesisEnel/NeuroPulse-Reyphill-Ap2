package io.github.reyx38.neuropulse.data.local.database

import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NeuroPulseDb : RoomDatabase() {

}