package io.github.reyx38.neuropulse.data.repository

import io.github.reyx38.neuropulse.data.local.Mappers.toDto
import io.github.reyx38.neuropulse.data.local.Mappers.toEntity
import io.github.reyx38.neuropulse.data.local.dao.SesionJuegoDao
import io.github.reyx38.neuropulse.data.remote.RemoteDataSource
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SesionJuegosRepository@Inject constructor(
    private val sesionJuegoDao: SesionJuegoDao,
    private val remoteDataSource: RemoteDataSource
) {
    fun getSesiones(usuarioId: Int): Flow<Resource<List<SesionJuegosDto>>> = flow {
        emit(Resource.Loading())
        var localList = sesionJuegoDao.getAllSesionJuegos(usuarioId)
            .map { it.toDto() }
        try {
            val remote = remoteDataSource.getSesionesJuegos(usuarioId)

            if (remote.isNotEmpty())
                sesionJuegoDao.deleteAllSesiones(usuarioId)
            remote.forEach { sesionJuegoDao.saveSesionJuego(it.toEntity()) }

            localList = sesionJuegoDao.getAllSesionJuegos(usuarioId)
                .map { it.toDto() }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage ?: "Error desconocido"}"))
        }
        emit(Resource.Success(localList))
    }

    suspend fun saveSesionJuegos(sesionDto: SesionJuegosDto): Resource<Unit> {
        return try {
            remoteDataSource.saveSesionesJuegos(sesionDto)

            sesionJuegoDao.saveSesionJuego(sesionDto.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Huubo un error al guardar los datos: ${e.localizedMessage}")
        }
    }
}