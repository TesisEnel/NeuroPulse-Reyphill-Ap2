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
    fun getSesiones(usuarioId: Int): Flow<Resource<List<SesionRespiracionDto>>> = flow {
        emit(Resource.Loading())
        var localList = sesionDao.getSesiones(usuarioId)
            .map { it.toDto() }
        try {
            // Sincroniza la data remota con la local
            val remote = neuroPulseApi.getSesionRespiraciones(usuarioId)
            remote.forEach { sesionDao.saveSesion(it.toEntity()) }

            // Luego consulta local y emite
             localList = sesionDao.getSesiones(usuarioId)
               .map { it.toDto() }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage ?: "Error desconocido"}"))
        }
        emit(Resource.Success(localList))


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