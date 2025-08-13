package io.github.reyx38.neuropulse.data.repository

import io.github.reyx38.neuropulse.data.local.Mappers.toDto
import io.github.reyx38.neuropulse.data.local.Mappers.toEntity
import io.github.reyx38.neuropulse.data.local.dao.ProgresionSemanalDao
import io.github.reyx38.neuropulse.data.remote.NeuroPulseApi
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.remote.dto.ProgresionSemanalDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgresionSemanalRepository @Inject constructor(
    private val api: NeuroPulseApi,
    private val dao: ProgresionSemanalDao
) {
    fun historialProgresionSemanlDesdeApi(usuariId: Int): Flow<Resource<List<ProgresionSemanalDto>>> {
        return flow {
            emit(Resource.Loading())

            try {
                val listaDto = api.getHistorialProgresionSemanal(usuariId)

                if (listaDto.isNotEmpty())
                    dao.deleteAll()

                val progresionSemanal = listaDto.map { it.toEntity() }
                dao.saveListProgresion(progresionSemanal)

            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error desconocido"))
            }

            val lista = dao.getHistoriaUsuario(usuariId)
            var listaDto = lista.map { it.toDto() }
            emit(Resource.Success(listaDto))
        }
    }

    fun obtenerProgesionSemanalActual(usuariId: Int): Flow<Resource<ProgresionSemanalDto>> {
        return flow {
            emit(Resource.Loading())
            try {
                var progresion = api.getProgresionActual(usuariId)
                emit(Resource.Success(progresion))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error desconocido"))
            }
        }
    }

    suspend fun actulizarProgresoSemanal(usuarioId: Int): Resource<Unit> {
        return try {
            api.actualizarProgresion(usuarioId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Huubo un error al guardar los datos: ${e.localizedMessage}")
        }
    }
}