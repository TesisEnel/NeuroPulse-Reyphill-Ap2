package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.DesvanecimientoGame

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
class DesvanecimientoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sesionJuegosRepository: SesionJuegosRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DesvanecimientoUiState())
    val uiState: StateFlow<DesvanecimientoUiState> = _uiState.asStateFlow()

    private val CORRECT_SCORE = 100
    private val INCORRECT_SCORE = 25

    init {
        getUsuario()
    }

    fun onEvent(event: DesvanecimientoEvent) {
        when (event) {
            is DesvanecimientoEvent.CompletadoChange -> onCompletadoChange(event.estado)
            is DesvanecimientoEvent.EjercicioCognitivoChange -> onEjercicioCognitivoChange(event.ejercicio)
            DesvanecimientoEvent.New -> resetGame()
            is DesvanecimientoEvent.PuntacionChange -> onPuntacionChange(event.puntacion)
            DesvanecimientoEvent.Save -> save()
            is DesvanecimientoEvent.UsuarioChange -> onUsuarioChange(event.usuarioId)
            DesvanecimientoEvent.JuegoIncompleto -> saveIncompleto()
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

    fun startGame() {
        _uiState.update {
            it.copy(
                isGameActive = true,
                currentRound = 1,
                puntuacionTotal = 0,
                juegoTerminado = false,
                isStarted = true
            )
        }
        startRound()
    }

    private fun startRound() {
        val difficulty = when (_uiState.value.currentRound) {
            1 -> 3 // 3 posiciones
            2 -> 4 // 4 posiciones
            3 -> 5 // 5 posiciones
            else -> 3
        }

        val positions = (0..15).shuffled().take(difficulty) // Cuadrícula 4x4

        _uiState.value = _uiState.value.copy(
            showImages = true,
            imagePositions = positions,
            selectedPositions = emptyList(),
            isAnswering = false,
            showResult = false,
            timeRemaining = 3
        )

        // Countdown timer
        viewModelScope.launch {
            for (i in 3 downTo 1) {
                _uiState.value = _uiState.value.copy(timeRemaining = i)
                delay(1000)
            }

            // Ocultar imágenes y permitir respuesta
            _uiState.value = _uiState.value.copy(
                showImages = false,
                isAnswering = true,
                timeRemaining = 0
            )
        }
    }

    fun selectPosition(position: Int) {
        val currentState = _uiState.value
        if (!currentState.isAnswering || currentState.showResult) return

        val newSelected = if (position in currentState.selectedPositions) {
            currentState.selectedPositions - position
        } else {
            currentState.selectedPositions + position
        }

        _uiState.value = currentState.copy(selectedPositions = newSelected)
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (!currentState.isAnswering) return

        val isCorrect =
            currentState.selectedPositions.sorted() == currentState.imagePositions.sorted()
        val points = if (isCorrect) CORRECT_SCORE else INCORRECT_SCORE

        _uiState.value = currentState.copy(
            showResult = true,
            isCorrect = isCorrect,
            isAnswering = false,
            puntuacionTotal = currentState.puntuacionTotal + points
        )

        viewModelScope.launch {
            delay(2000) // Mostrar resultado por 2 segundos
            nextRound()
        }
    }

    private fun nextRound() {
        val currentState = _uiState.value
        if (currentState.currentRound < currentState.totalRounds) {
            _uiState.value = currentState.copy(
                currentRound = currentState.currentRound + 1,
                showResult = false
            )
            startRound()
        } else {
            _uiState.value = currentState.copy(
                juegoTerminado = true,
                isGameActive = false,
                showResult = false
            )
        }
    }

    fun resetGame() {
        _uiState.value = DesvanecimientoUiState()
    }
}