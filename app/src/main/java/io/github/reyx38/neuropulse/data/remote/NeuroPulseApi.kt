package io.github.reyx38.neuropulse.data.remote

import io.github.reyx38.neuropulse.data.remote.dto.PeticionLogin
import io.github.reyx38.neuropulse.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.POST

interface NeuroPulseApi {
    //Usuarios/Registrar
    @POST("api/Usuarios/register")
    suspend fun registrarUsuario(@Body usuarioDto: UsuarioDto)
    //Usuario/Login
    @POST("api/Usuarios/login")
    suspend fun loginUsuario(@Body peticionLogin: PeticionLogin ): UsuarioDto
}