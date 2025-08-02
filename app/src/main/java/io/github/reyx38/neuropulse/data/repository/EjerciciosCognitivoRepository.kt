package io.github.reyx38.neuropulse.data.repository

import io.github.reyx38.neuropulse.data.local.Mappers.toEntity
import io.github.reyx38.neuropulse.data.local.dao.EjercicioCognitivoDao
import io.github.reyx38.neuropulse.data.local.entities.EjerciciosCognitivoEntity
import io.github.reyx38.neuropulse.data.remote.RemoteDataSource
import io.github.reyx38.neuropulse.data.remote.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EjerciciosCognitivoRepository @Inject constructor(
    private val ejercicioCognitivoDao: EjercicioCognitivoDao,
    private  val remoteDataSource: RemoteDataSource
) {
    fun sincronizarEjercicios(): Flow<Resource<List<EjerciciosCognitivoEntity>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val listaDto = remoteDataSource.getEjerciciosCognitivos()

                if (!listaDto.isEmpty()) {
                    ejercicioCognitivoDao.deleteAllEjercicios()
                    val ejerciciosCognitivos = listaDto.map { it.toEntity() }
                    ejercicioCognitivoDao.saveEjerciciosCognitivos(ejerciciosCognitivos)
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error desconocido"))
            }
            val lista = ejercicioCognitivoDao.getAllEjercicioCognitivo()
            emit(Resource.Success(lista))
        }
    }

}