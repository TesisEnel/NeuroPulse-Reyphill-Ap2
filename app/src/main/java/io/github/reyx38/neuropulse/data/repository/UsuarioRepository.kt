package io.github.reyx38.neuropulse.data.repository

import io.github.reyx38.neuropulse.data.local.dao.UsuarioDao
import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    suspend fun save(usuario: UserEntity) = usuarioDao.save(usuario)
    suspend fun getUsuario (): UserEntity? = usuarioDao.getUsuario()
    suspend fun deleteAll() = usuarioDao.deleteAll()
}