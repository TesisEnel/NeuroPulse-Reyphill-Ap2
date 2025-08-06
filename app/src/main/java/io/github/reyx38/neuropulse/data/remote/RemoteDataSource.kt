package io.github.reyx38.neuropulse.data.remote

import io.github.reyx38.neuropulse.data.remote.dto.PeticionLogin
import io.github.reyx38.neuropulse.data.remote.dto.ReflexionDto
import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto
import io.github.reyx38.neuropulse.data.remote.dto.SesionRespiracionDto
import io.github.reyx38.neuropulse.data.remote.dto.UsuarioDto
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val neuroPulseApi: NeuroPulseApi
) {
    //Usuarios
    suspend fun registerUsuario(usuarioDto: UsuarioDto) = neuroPulseApi.registrarUsuario(usuarioDto)
    suspend fun loginUsuario(peticionLogin: PeticionLogin) = neuroPulseApi.loginUsuario(peticionLogin)
    suspend fun updateUsuario(id: Int, usuarioDto: UsuarioDto) = neuroPulseApi.updateUsuario(id, usuarioDto)

    //Reflexiones
    suspend fun getReflexiones(usuarioId: Int) = neuroPulseApi.getReflexiones(usuarioId)
    suspend fun saveReflexion(reflexionDto: ReflexionDto) = neuroPulseApi.saveReflexion(reflexionDto)
    suspend fun deleteReflexion(reflexionId: Int) = neuroPulseApi.deleteReflexion(reflexionId)

    //Respiraciones
    suspend fun getRespiraciones() = neuroPulseApi.getRespiraciones()

    //Sesiones
    suspend fun getSesiones(usuarioId: Int) = neuroPulseApi.getSesionRespiraciones(usuarioId)
    suspend fun saveSesiones(sesionDto: SesionRespiracionDto) = neuroPulseApi.saveSesionRespiracion(sesionDto)

    //EjercicosCognitivos
    suspend fun getEjerciciosCognitivos() = neuroPulseApi.getEjerciosCognitivos()

    //Sesiones Juegos
    suspend fun getSesionesJuegos(usuarioId: Int) = neuroPulseApi.getSesionesJuegosPorUsuario(usuarioId)
    suspend fun saveSesionesJuegos(sesionJuegosDto: SesionJuegosDto) = neuroPulseApi.saveSesionJuego(sesionJuegosDto)

    //ProgresionSemanal
    suspend fun getHistorialSemanal(usuarioId: Int) = neuroPulseApi.getHistorialProgresionSemanal(usuarioId)
    suspend fun getActualProgresion(usuarioId: Int) = neuroPulseApi.getProgresionActual(usuarioId)
    suspend fun actualizarProgresion(usuarioId: Int) = neuroPulseApi.actualizarProgresion(usuarioId)

}