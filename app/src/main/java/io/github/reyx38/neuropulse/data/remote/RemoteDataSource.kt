package io.github.reyx38.neuropulse.data.remote

import io.github.reyx38.neuropulse.data.remote.dto.PeticionLogin
import io.github.reyx38.neuropulse.data.remote.dto.ReflexionDto
import io.github.reyx38.neuropulse.data.remote.dto.UsuarioDto
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val neuroPulseApi: NeuroPulseApi
) {
    suspend fun registerUsuario(usuarioDto: UsuarioDto) = neuroPulseApi.registrarUsuario(usuarioDto)
    suspend fun loginUsuario(peticionLogin: PeticionLogin) = neuroPulseApi.loginUsuario(peticionLogin)
    suspend fun updateUsuario(id: Int, usuarioDto: UsuarioDto) = neuroPulseApi.updateUsuario(id, usuarioDto)
    //Reflexiones
    suspend fun getReflexiones(usuarioId: Int) = neuroPulseApi.getReflexiones(usuarioId)
    suspend fun saveReflexion(reflexionDto: ReflexionDto) = neuroPulseApi.saveReflexion(reflexionDto)
}