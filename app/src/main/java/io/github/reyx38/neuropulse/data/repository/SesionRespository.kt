package io.github.reyx38.neuropulse.data.repository

import io.github.reyx38.neuropulse.data.local.Mappers.toDto
import io.github.reyx38.neuropulse.data.local.Mappers.toEntity
import io.github.reyx38.neuropulse.data.local.dao.SesionRespiracionDao
import io.github.reyx38.neuropulse.data.remote.NeuroPulseApi
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.remote.dto.SesionRespiracionDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SesionRespository @Inject constructor(
    private val sesionDao: SesionRespiracionDao,
    private val neuroPulseApi: NeuroPulseApi
) {
    fun getSesiones(usuarioId: Int): Flow<Resource<List<SesionRespiracionDto>>> {
        return flow {
            emit(Resource.Loading())
            val localFlow = sesionDao.getSesiones(usuarioId)
                .map {
                    it.toDto()
                }
            try {
                val remote = neuroPulseApi.getSesionRespiraciones(usuarioId)
                remote.forEach { sesionDao.saveSesion(it.toEntity()) }
            } catch (e: Exception) {
                emit(Resource.Error("Error: ${e.localizedMessage ?: "Error desconocido"}"))
            }
            Resource.Success(localFlow)
        }

    }

    suspend fun save(sesionDto: SesionRespiracionDto): Resource<Unit> {
        return try {
            neuroPulseApi.saveSesionRespiracion(sesionDto)

            sesionDao.saveSesion(sesionDto.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Huubo un error al guardar los datos: ${e.localizedMessage}")
        }
    }
}