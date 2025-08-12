package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.logicaSombra

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
class ShadowLogicViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sesionJuegosRepository: SesionJuegosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogicaSombraUiState())
    val uiState: StateFlow<LogicaSombraUiState> = _uiState.asStateFlow()

    private val shapes = listOf(
        ShapeType.CIRCLE, ShapeType.SQUARE, ShapeType.TRIANGLE,
        ShapeType.DIAMOND, ShapeType.STAR, ShapeType.HEXAGON
    )

    init {
        startNewGame()
        getUsuario()
    }


    fun onEvent(event: LogicaSombraEvent) {
        when (event) {
            is LogicaSombraEvent.CompletadoChange -> onCompletadoChange(event.estado)
            is LogicaSombraEvent.EjercicioCognitivoChange -> onEjercicioCognitivoChange(event.ejercicio)
            LogicaSombraEvent.New -> restartGame()
            is LogicaSombraEvent.PuntacionChange -> onPuntacionChange(event.puntacion)
            LogicaSombraEvent.Save -> save()
            is LogicaSombraEvent.UsuarioChange -> onUsuarioChange(event.usuarioId)
            LogicaSombraEvent.JuegoIncompleto -> saveIncompleto()
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
                    isGameCompleted = estado
                )
            }
        }
    }

    private fun onEjercicioCognitivoChange(ejercicioId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    ejercicioCognitivoId = ejercicioId
                )
            }
        }
    }

    private fun onPuntacionChange(puntacionTotal: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    score = puntacionTotal
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
                    isGameCompleted = false,
                    score = -400,
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


    fun startNewGame() {
        _uiState.value = LogicaSombraUiState()
        generateNewRound()
    }

    private fun generateNewRound() {
        val currentState = _uiState.value
        if (currentState.currentRound > currentState.totalRounds) {
            _uiState.value = currentState.copy(isGameCompleted = true)
            return
        }

        val originalShape = ShadowShape(
            id = 0,
            shape = shapes.random(),
            rotation = Random.nextFloat() * 360f
        )

        val correctShadow = originalShape.copy(
            id = 1,
            isCorrectShadow = true
        )

        // Generar sombras incorrectas
        val incorrectShadows = mutableListOf<ShadowShape>()
        repeat(3) { index ->
            val incorrectShape = ShadowShape(
                id = index + 2,
                shape = if (Random.nextBoolean()) shapes.random() else originalShape.shape,
                rotation = if (Random.nextBoolean())
                    originalShape.rotation + Random.nextFloat() * 90f + 45f
                else
                    Random.nextFloat() * 360f,
                isCorrectShadow = false
            )
            incorrectShadows.add(incorrectShape)
        }

        val allShadows = (listOf(correctShadow) + incorrectShadows).shuffled()
        val correctAnswerId = allShadows.find { it.isCorrectShadow }?.id ?: 1

        val gameRound = GameRound(
            roundNumber = currentState.currentRound,
            originalShape = originalShape,
            shadowOptions = allShadows,
            correctAnswerId = correctAnswerId
        )

        _uiState.value = currentState.copy(
            currentGameRound = gameRound,
            selectedAnswerId = null,
            showResult = false
        )
    }

    fun selectAnswer(shadowId: Int) {
        val currentState = _uiState.value
        val gameRound = currentState.currentGameRound ?: return

        val isCorrect = shadowId == gameRound.correctAnswerId
        val roundScore = if (isCorrect) 100 else 25

        _uiState.value = currentState.copy(
            selectedAnswerId = shadowId,
            showResult = true,
            isCorrect = isCorrect,
            roundScore = roundScore,
            score = currentState.score + roundScore
        )

        viewModelScope.launch {
            delay(2000) // Mostrar resultado por 2 segundos
            nextRound()
        }
    }

    private fun nextRound() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            currentRound = currentState.currentRound + 1,
            selectedAnswerId = null,
            showResult = false
        )
        generateNewRound()
    }

    fun restartGame() {
        startNewGame()
    }
}