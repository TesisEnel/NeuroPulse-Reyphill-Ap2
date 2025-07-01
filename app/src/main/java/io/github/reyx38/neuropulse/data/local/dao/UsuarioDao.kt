package io.github.reyx38.neuropulse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import io.github.reyx38.neuropulse.data.local.entities.UserEntity

@Dao
interface UsuarioDao {
    @Upsert()
    suspend fun save (usuario: UserEntity)

    @Query(
        """
            Select *
            from Usuarios
            where UsuarioId=:id
            Limit 1
        """
    )
    suspend fun find (id: Int): UserEntity?

    @Delete
    suspend fun delete(usuario: UserEntity)
}