package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.conflictoColores

import androidx.compose.ui.graphics.Color
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
import kotlin.random.Random

@HiltViewModel
class ConflictoColoresViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sesionJuegosRepository: SesionJuegosRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ConflictoColoresUiState())
    val uiState: StateFlow<ConflictoColoresUiState> = _uiState.asStateFlow()

    private val colores = listOf(
        ColorInfo("ROJO", Color.Red),
        ColorInfo("AZUL", Color.Blue),
        ColorInfo("VERDE", Color.Green),
        ColorInfo("AMARILLO", Color.Yellow),
        ColorInfo("MORADO", Color.Magenta),
        ColorInfo("NARANJA", Color(0xFFFF8800))
    )

    init {
        iniciarNuevaRonda()
        getUsuario()
    }

    fun onEvent(event: ConflictoColoresEvent) {
        when (event) {
            is ConflictoColoresEvent.CompletadoChange -> onCompletadoChange(event.estado)
            is ConflictoColoresEvent.EjercicioCognitivoChange -> onEjercicioCognitivoChange(event.ejercicio)
            ConflictoColoresEvent.JuegoIncompleto -> saveIncompleto()
            ConflictoColoresEvent.New -> reiniciarJuego()
            is ConflictoColoresEvent.PuntacionChange -> onPuntacionChange(event.puntacion)
            ConflictoColoresEvent.Save -> save()
            is ConflictoColoresEvent.UsuarioChange -> onUsuarioChange(event.usuarioId)
        }
    }

    private fun saveIncompleto() {
        penalizacionJuego()
        save()
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

    private fun iniciarNuevaRonda() {
        val palabrasPorRonda = when (_uiState.value.rondaActual) {
            1 -> 4
            2 -> 6
            3 -> 10
            else -> 10
        }
        val coloresParaRonda = colores.shuffled().take(4)

        _uiState.value = _uiState.value.copy(
            palabrasCompletadas = 0,
            palabrasPorRonda = palabrasPorRonda,
            tiempoRestante = 20,
            mostrandoResultado = false,
            coloresDisponibles = coloresParaRonda
        )
        generarNuevaPalabra()
        iniciarTemporizador()
    }

    private fun generarNuevaPalabra() {
        val coloresDisponibles = _uiState.value.coloresDisponibles

        val modoAleatorio = if (Random.nextBoolean()) "COLOR" else "PALABRA"

        val palabraInfo = coloresDisponibles.random()
        val colorInfo = coloresDisponibles.random()

        val respuestaCorrecta = if (modoAleatorio == "COLOR") {
            colorInfo.nombre
        } else {
            palabraInfo.nombre
        }

        _uiState.value = _uiState.value.copy(
            modoJuego = modoAleatorio,
            palabraActual = palabraInfo.nombre,
            colorPalabra = colorInfo.color,
            respuestaCorrecta = respuestaCorrecta,
            mostrandoResultado = false
        )
    }

    private fun iniciarTemporizador() {
        viewModelScope.launch {
            while (_uiState.value.tiempoRestante > 0 &&
                _uiState.value.palabrasCompletadas < _uiState.value.palabrasPorRonda &&
                !_uiState.value.juegoTerminado) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    tiempoRestante = _uiState.value.tiempoRestante - 1
                )
            }
            if (_uiState.value.palabrasCompletadas < _uiState.value.palabrasPorRonda) {
                finalizarRonda()
            }
        }
    }

    fun seleccionarRespuesta(respuesta: String) {
        if (_uiState.value.mostrandoResultado || _uiState.value.juegoTerminado) return

        val esCorrecta = respuesta == _uiState.value.respuestaCorrecta
        val puntos = if (esCorrecta) 100 else 25

        _uiState.value = _uiState.value.copy(
            mostrandoResultado = true,
            esRespuestaCorrecta = esCorrecta,
            puntuacionTotal = _uiState.value.puntuacionTotal + puntos,
            palabrasCompletadas = _uiState.value.palabrasCompletadas + 1
        )

        viewModelScope.launch {
            delay(1000)
            if (_uiState.value.palabrasCompletadas < _uiState.value.palabrasPorRonda) {
                generarNuevaPalabra()
            } else {
                finalizarRonda()
            }
        }
    }

    private fun finalizarRonda() {
        if (_uiState.value.rondaActual < 3) {
            _uiState.value = _uiState.value.copy(
                rondaActual = _uiState.value.rondaActual + 1
            )
            viewModelScope.launch {
                delay(1000)
                iniciarNuevaRonda()
            }
        } else {
            _uiState.value = _uiState.value.copy(
                juegoTerminado = true
            )        }
    }

    fun reiniciarJuego() {
        _uiState.value = ConflictoColoresUiState()
        iniciarNuevaRonda()
    }
}