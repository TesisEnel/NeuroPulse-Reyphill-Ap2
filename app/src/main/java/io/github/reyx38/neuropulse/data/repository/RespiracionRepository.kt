package io.github.reyx38.neuropulse.data.repository

import io.github.reyx38.neuropulse.data.local.Mappers.toEntity
import io.github.reyx38.neuropulse.data.local.dao.RespiracionDao
import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.data.remote.NeuroPulseApi
import io.github.reyx38.neuropulse.data.remote.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RespiracionRepository @Inject constructor(
    private val api: NeuroPulseApi,
    private val dao: RespiracionDao
) {

    fun sincronizarRespiracionesDesdeApi(): Flow<Resource<List<RespiracionWithInformacion>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val listaDto = api.getRespiraciones()

                val respiraciones = listaDto.map { it.toEntity() }

                val informaciones = listaDto.flatMap { dto ->
                    dto.informacionRespiracion.map { it.toEntity() }
                }

                dao.saveRespiraciones(respiraciones)
                dao.saveInformaciones(informaciones)


            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error desconocido"))
            }
            val lista = dao.getAllRespiraciones()
            emit(Resource.Success(lista))
        }
    }

    suspend fun obtenerRespiraciones(): List<RespiracionWithInformacion> {
        return dao.getAllRespiraciones()
    }

    suspend fun find(respiracionId: Int) = dao.find(respiracionId)
}