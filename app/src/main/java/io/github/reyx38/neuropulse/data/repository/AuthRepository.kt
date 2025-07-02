package io.github.reyx38.neuropulse.data.repository

import io.github.reyx38.neuropulse.data.local.Mappers.toEntity
import io.github.reyx38.neuropulse.data.local.entities.UserEntity
import io.github.reyx38.neuropulse.data.remote.RemoteDataSource
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.remote.dto.PeticionLogin
import io.github.reyx38.neuropulse.data.remote.dto.UsuarioDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val remote: RemoteDataSource,
    private val local: UsuarioRepository
) {
    fun login(nombre: String, password: String): Flow<Resource<UserEntity>> {
        return flow {
            emit(Resource.Loading())

            try {
                val dto = remote.loginUsuario(PeticionLogin(nombre, password))

                val userEntity = dto.toEntity()
                local.save(userEntity)
                emit(Resource.Success(userEntity))

            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error desconocido"))
            }
        }
    }

    fun registar(usuarioDto: UsuarioDto): Flow<Resource<Unit>> {
        return flow {
            try {
                remote.registerUsuario(usuarioDto)
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error Desconoido"))
            }
        }
    }
}