package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.SecuenciaMental

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuenciaMentalViewModel@Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SecuenciaMentalState())
    val state: StateFlow<SecuenciaMentalState> = _state.asStateFlow()

    init {
        iniciarNuevaRonda()
    }

    private fun iniciarNuevaRonda() {
        val cantidadNumeros = when (_state.value.rondaActual) {
            1 -> 5
            2 -> 7
            3 -> 9
            else -> 5
        }

        val numerosOrdenados = (1..cantidadNumeros).toList()
        val numerosDesordenados = numerosOrdenados.shuffled()

        _state.value = _state.value.copy(
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
            while (_state.value.tiempoRestante > 0 && !_state.value.mostrandoResultado) {
                delay(1000)
                _state.value = _state.value.copy(
                    tiempoRestante = _state.value.tiempoRestante - 1
                )
            }
            if (!_state.value.mostrandoResultado) {
                verificarRespuesta()
            }
        }
    }

    fun agregarNumero(numero: Int) {
        if (_state.value.mostrandoResultado || _state.value.juegoTerminado) return

        val nuevaRespuesta = _state.value.respuestaUsuario + numero
        _state.value = _state.value.copy(respuestaUsuario = nuevaRespuesta)

        if (nuevaRespuesta.size == _state.value.numerosOrdenados.size) {
            verificarRespuesta()
        }
    }

    fun quitarUltimoNumero() {
        if (_state.value.respuestaUsuario.isNotEmpty() && !_state.value.mostrandoResultado) {
            _state.value = _state.value.copy(
                respuestaUsuario = _state.value.respuestaUsuario.dropLast(1)
            )
        }
    }

    private fun verificarRespuesta() {
        val esCorrecta = _state.value.respuestaUsuario == _state.value.numerosOrdenados
        val puntos = if (esCorrecta) 100 else 25

        _state.value = _state.value.copy(
            mostrandoResultado = true,
            esRespuestaCorrecta = esCorrecta,
            puntuacionTotal = _state.value.puntuacionTotal + puntos
        )

        viewModelScope.launch {
            delay(2000)
            siguienteRonda()
        }
    }

    private fun siguienteRonda() {
        if (_state.value.rondaActual < 3) {
            _state.value = _state.value.copy(
                rondaActual = _state.value.rondaActual + 1
            )
            iniciarNuevaRonda()
        } else {
            _state.value = _state.value.copy(
                juegoTerminado = true
            )
        }
    }

    fun reiniciarJuego() {
        _state.value = SecuenciaMentalState()
        iniciarNuevaRonda()
    }
}