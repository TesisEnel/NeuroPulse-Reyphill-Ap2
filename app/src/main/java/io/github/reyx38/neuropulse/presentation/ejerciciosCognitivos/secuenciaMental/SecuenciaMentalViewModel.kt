package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.secuenciaMental

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.repository.AuthRepository
import io.github.reyx38.neuropulse.data.repository.SesionJuegosRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuenciaMentalViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sesionJuegosRepository: SesionJuegosRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SecuenciaMentalUiState())
    val uiState: StateFlow<SecuenciaMentalUiState> = _uiState.asStateFlow()

    init {
        iniciarNuevaRonda()
        getUsuario()
    }

   fun onEvent( event: SecuenciaMentalEvent){
        when(event) {
            is SecuenciaMentalEvent.CompletadoChange -> onCompletadoChange(event.estado)
            is SecuenciaMentalEvent.EjercicioCognitivoChange -> onEjercicioCognitivoChange(event.ejercicio)
            SecuenciaMentalEvent.JuegoIncompleto -> saveIncompleto()
            SecuenciaMentalEvent.New -> reiniciarJuego()
            is SecuenciaMentalEvent.PuntacionChange -> onPuntacionChange(event.puntacion)
            SecuenciaMentalEvent.Save -> save()
            is SecuenciaMentalEvent.UsuarioChange -> onUsuarioChange(event.usuarioId)
        }
   }

    private fun saveIncompleto() {
        penalizacionJuego()
        save()
    }
    private fun penalizacionJuego(){
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    juegoTerminado = false,
                    puntuacionTotal = -400,
                )
            }
        }
    }

    fun getUsuario() {
        viewModelScope.launch {
            val user = authRepository.getUsuario()
            _uiState.update {
                it.copy(
                    usuarioId = user?.usuarioId,
                )
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val result = sesionJuegosRepository.saveSesionJuegos(_uiState.value.toDto())
            _uiState.update {
                it.copy(
                    error = when (result) {
                        is Resource.Success -> "la sesion se ha guardada"
                        is Resource.Error -> result.message ?: "Error al guardar la sesion"
                        else -> null
                    }
                )
            }

        }
    }
    private fun onCompletadoChange(estado: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    juegoTerminado = estado
                )
            }
        }
    }

    private fun onEjercicioCognitivoChange(ejercicioId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    ejerciciosCognitivosId = ejercicioId
                )
            }
        }
    }

    private fun onPuntacionChange(puntacionTotal: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    puntuacionTotal = puntacionTotal
                )
            }
        }
    }

    private fun onUsuarioChange(usuarioId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    usuarioId = usuarioId
                )
            }
        }
    }


    private fun iniciarNuevaRonda() {
        val cantidadNumeros = when (_uiState.value.rondaActual) {
            1 -> 5
            2 -> 7
            3 -> 9
            else -> 5
        }

        val numerosOrdenados = (1..cantidadNumeros).toList()
        val numerosDesordenados = numerosOrdenados.shuffled()

        _uiState.value = _uiState.value.copy(
            numerosDesordenados = numerosDesordenados,
            numerosOrdenados = numerosOrdenados,
            respuestaUsuario = emptyList(),
            tiempoRestante = 30,
            mostrandoResultado = false
        )

        iniciarTemporizador()
    }

    private fun iniciarTemporizador() {
        viewModelScope.launch {
            while (_uiState.value.tiempoRestante > 0 && !_uiState.value.mostrandoResultado) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    tiempoRestante = _uiState.value.tiempoRestante - 1
                )
            }
            if (!_uiState.value.mostrandoResultado) {
                verificarRespuesta()
            }
        }
    }

    fun agregarNumero(numero: Int) {
        if (_uiState.value.mostrandoResultado || _uiState.value.juegoTerminado) return

        val nuevaRespuesta = _uiState.value.respuestaUsuario + numero
        _uiState.value = _uiState.value.copy(respuestaUsuario = nuevaRespuesta)

        if (nuevaRespuesta.size == _uiState.value.numerosOrdenados.size) {
            verificarRespuesta()
        }
    }

    fun quitarUltimoNumero() {
        if (_uiState.value.respuestaUsuario.isNotEmpty() && !_uiState.value.mostrandoResultado) {
            _uiState.value = _uiState.value.copy(
                respuestaUsuario = _uiState.value.respuestaUsuario.dropLast(1)
            )
        }
    }

    private fun verificarRespuesta() {
        val esCorrecta = _uiState.value.respuestaUsuario == _uiState.value.numerosOrdenados
        val puntos = if (esCorrecta) 100 else 25

        _uiState.value = _uiState.value.copy(
            mostrandoResultado = true,
            esRespuestaCorrecta = esCorrecta,
            puntuacionTotal = _uiState.value.puntuacionTotal + puntos
        )

        viewModelScope.launch {
            delay(2000)
            siguienteRonda()
        }
    }

    private fun siguienteRonda() {
        if (_uiState.value.rondaActual < 3) {
            _uiState.value = _uiState.value.copy(
                rondaActual = _uiState.value.rondaActual + 1
            )
            iniciarNuevaRonda()
        } else {
            _uiState.value = _uiState.value.copy(
                juegoTerminado = true
            )
        }
    }

    fun reiniciarJuego() {
        _uiState.value = SecuenciaMentalUiState()
        iniciarNuevaRonda()
    }
}