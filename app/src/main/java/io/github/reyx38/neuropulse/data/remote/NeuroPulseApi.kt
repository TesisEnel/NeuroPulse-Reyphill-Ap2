package io.github.reyx38.neuropulse.data.remote

import io.github.reyx38.neuropulse.data.remote.dto.PeticionLogin
import io.github.reyx38.neuropulse.data.remote.dto.ReflexionDto
import io.github.reyx38.neuropulse.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NeuroPulseApi {
    //Usuarios/Registrar
    @POST("api/Usuarios/register")
    suspend fun registrarUsuario(@Body usuarioDto: UsuarioDto)

    //Usuario/Login
    @POST("api/Usuarios/login")
    suspend fun loginUsuario(@Body peticionLogin: PeticionLogin): UsuarioDto

    //Usario/Edit
    @PUT("api/Usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuarioDto: UsuarioDto) : Response<Unit>

    //Reflexiones/List
    @GET("api/Reflexiones/usuario/{usuarioId}")
    suspend fun getReflexiones(@Path("usuarioId") usuarioId: Int) : List<ReflexionDto>
    //Reflexiones/Agregar
    @POST("api/Reflexiones")
    suspend fun saveReflexion(@Body reflexionDto: ReflexionDto): Response<Unit>
}