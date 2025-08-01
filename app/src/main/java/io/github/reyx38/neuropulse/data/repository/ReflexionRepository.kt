package io.github.reyx38.neuropulse.data.repository

import io.github.reyx38.neuropulse.data.local.Mappers.toDto
import io.github.reyx38.neuropulse.data.local.Mappers.toEntity

import io.github.reyx38.neuropulse.data.local.dao.ReflexionDao
import io.github.reyx38.neuropulse.data.remote.RemoteDataSource
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.remote.dto.ReflexionDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReflexionRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val reflexionDao: ReflexionDao
) {
    fun getReflexiones(usuarioId: Int): Flow<Resource<List<ReflexionDto>>> {
        return flow {
            emit(Resource.Loading())
            val localFlow = reflexionDao.listarReflexion(usuarioId)
                .map { list ->
                    Resource.Success(list.map { it.toDto() })
                }
            try {
                val remote = remoteDataSource.getReflexiones(usuarioId)
                if (remote.isNotEmpty())
                    reflexionDao.deleteAllReflexion(usuarioId)
                remote.forEach { reflexionDao.save(it.toEntity()) }
            } catch (e: Exception) {
                emit(Resource.Error("Error: ${e.localizedMessage ?: "Error desconocido"}"))
            }
            emitAll(localFlow)

        }

    }

    suspend fun save(reflexionDto: ReflexionDto): Resource<Unit> {
        return try {

            remoteDataSource.saveReflexion(reflexionDto)

            reflexionDao.save(reflexionDto.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Huubo un error al guardar los datos: ${e.localizedMessage}")
        }
    }

    suspend fun find(reflexionId : Int) = reflexionDao.find(reflexionId)

    suspend fun deleteReflexion(reflexionId: Int): Resource<Unit> {
        reflexionDao.deleteReflexion(reflexionId)
      return  try {
          reflexionDao.deleteReflexion(reflexionId)
          remoteDataSource.deleteReflexion(reflexionId)
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error("Huubo un error al eliminar los datos: ${e.localizedMessage}")
        }
    }

}