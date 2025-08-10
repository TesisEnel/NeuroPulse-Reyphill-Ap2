package io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.local.enum.EstadosRespiracion
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.repository.AuthRepository
import io.github.reyx38.neuropulse.data.repository.RespiracionRepository
import io.github.reyx38.neuropulse.data.repository.SesionRespository
import io.github.reyx38.neuropulse.presentation.reflexiones.toDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RespiracionViewModel @Inject constructor(
    private val repository: RespiracionRepository,
    private val authRepository: AuthRepository,
    private val sesionRespository: SesionRespository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RespiracionUiState())
    val uiState = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _currentPhase = MutableStateFlow(EstadosRespiracion.INHALING)
    val currentPhase = _currentPhase.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _remainingTimeMs = MutableStateFlow(0L)
    val remainingTimeMs = _remainingTimeMs.asStateFlow()

    private var totalTimeMs = 0L
    private var breathingJob: Job? = null
    private var currentPhaseStartTime = 0L
    private var totalStartTime = 0L
    private var pausedTimeMs = 0L

    init {
        cargarRespiraciones()
        getUsuario()
    }

    fun getUsuario() {
        viewModelScope.launch {
            val user = authRepository.getUsuario()
            if (user!!.usuarioId != null) {
                _uiState.update {
                    it.copy(
                        usuarioId = user.usuarioId,
                        user = user,
                    )
                }
            }
        }
    }

    fun onEvent(event: RespiracionUiEvent) {
        when (event) {
            is RespiracionUiEvent.DuracionMinutos -> onChangeMinutos(event.minutos)
            is RespiracionUiEvent.RespiracionChange -> onChangeRespiracion(event.respiracionId)
            is RespiracionUiEvent.UsuarioChange -> onChangeUsuario(event.usuarioId)
            is RespiracionUiEvent.EstadoChange -> onChangeEstado(event.estado)
            RespiracionUiEvent.Save -> save()
            RespiracionUiEvent.New -> resetSesion()
        }
    }

    private fun onChangeUsuario(usuarioId: Int) {
        _uiState.update { it.copy(usuarioId = usuarioId) }
    }
    private fun onChangeEstado(estado: String) {
        _uiState.update { it.copy(estado = estado) }
    }
    private fun onChangeRespiracion(respiracionId: Int) {
        _uiState.update { it.copy(respiracionId = respiracionId) }
    }

    private fun onChangeMinutos(minutos: Int) {
        totalTimeMs = minutos * 60 * 1000L
        _remainingTimeMs.value = totalTimeMs
        _uiState.update { it.copy(duracionMinutos = minutos) }
    }

    private fun initializeTotalTime() {
        val minutos = _uiState.value.duracionMinutos
        if (minutos > 0) {
            totalTimeMs = minutos * 60 * 1000L
            _remainingTimeMs.value = totalTimeMs
        }
    }

    fun buscarRespiracion(respiracionId: Int) {
        viewModelScope.launch {
            if (respiracionId > 0) {
                val respiracion = repository.find(respiracionId)
                _uiState.update {
                    it.copy(respiracion = respiracion)
                }
                initializeTotalTime()
            }
        }
    }

    fun cargarRespiraciones() {
        viewModelScope.launch {
            val list = repository.obtenerRespiraciones()
            if (list.isNotEmpty()) {
                _uiState.update { it.copy(respiraciones = list) }
            } else {
                repository.sincronizarRespiracionesDesdeApi().collect { resultado ->
                    when (resultado) {
                        is Resource.Loading -> _uiState.update {
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }

                        is Resource.Success -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                respiraciones = resultado.data ?: emptyList(),
                                error = null
                            )
                        }

                        is Resource.Error -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = resultado.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
        }
    }

    fun togglePlayPause() {
        // Asegurar que el tiempo total esté inicializado
        if (totalTimeMs == 0L) {
            initializeTotalTime()
        }

        val newState = !_isRunning.value
        _isRunning.value = newState

        _uiState.update {
            it.copy(estado = if (newState) "En progreso" else "Pausado")
        }

        if (newState) {
            startBreathingCycle()
        } else {
            breathingJob?.cancel()
        }
    }

    fun resetSesion() {
        breathingJob?.cancel()
        _isRunning.value = false
        _progress.value = 0f
        _currentPhase.value = EstadosRespiracion.INHALING
        pausedTimeMs = 0L
        currentPhaseStartTime = 0L
        totalStartTime = 0L

        // Reinicializar el tiempo total
        initializeTotalTime()

        _uiState.update {
            it.copy(estado = "Detenido", duracionMinutos = (totalTimeMs / 60000).toInt())
        }
    }

    private fun startBreathingCycle() {
        breathingJob?.cancel()

        breathingJob = viewModelScope.launch {
            val respiracion = _uiState.value.respiracion?.respiracion ?: return@launch

            // Crear lista de fases válidas
            val fases = buildList {
                if (respiracion.inhalarSegundos > 0) {
                    add(EstadosRespiracion.INHALING to respiracion.inhalarSegundos * 1000L)
                }
                if (respiracion.mantenerSegundos > 0) {
                    add(EstadosRespiracion.HOLDING to respiracion.mantenerSegundos * 1000L)
                }
                if (respiracion.exhalarSegundos > 0) {
                    add(EstadosRespiracion.EXHALING to respiracion.exhalarSegundos * 1000L)
                }
            }

            if (fases.isEmpty()) {
                _uiState.update { it.copy(estado = "Configuración inválida") }
                _isRunning.value = false
                return@launch
            }

            // Si es la primera vez, inicializar tiempos
            if (totalStartTime == 0L) {
                totalStartTime = System.currentTimeMillis()
                currentPhaseStartTime = totalStartTime
            } else {
                // Si se reanuda, ajustar el tiempo de inicio
                val pauseDuration = System.currentTimeMillis() - pausedTimeMs
                totalStartTime += pauseDuration
                currentPhaseStartTime += pauseDuration
            }

            var currentPhaseIndex = fases.indexOfFirst { it.first == _currentPhase.value }
            if (currentPhaseIndex == -1) currentPhaseIndex = 0

            while (_isRunning.value && _remainingTimeMs.value > 0L) {
                val (fase, duracionMs) = fases[currentPhaseIndex]
                _currentPhase.value = fase

                // Si es una nueva fase, reiniciar el tiempo de inicio de la fase
                if (System.currentTimeMillis() - currentPhaseStartTime >= duracionMs) {
                    currentPhaseStartTime = System.currentTimeMillis()
                }

                var phaseCompleted = false

                while (_isRunning.value && !phaseCompleted && _remainingTimeMs.value > 0L) {
                    val currentTime = System.currentTimeMillis()
                    val elapsedInPhase = currentTime - currentPhaseStartTime
                    val totalElapsed = currentTime - totalStartTime

                    // Actualizar progreso de la fase actual
                    _progress.value = (elapsedInPhase.toFloat() / duracionMs).coerceIn(0f, 1f)

                    // Actualizar tiempo restante total
                    val remaining = (totalTimeMs - totalElapsed).coerceAtLeast(0L)
                    _remainingTimeMs.value = remaining

                    // Completar fase si se alcanza la duración
                    if (elapsedInPhase >= duracionMs) {
                        phaseCompleted = true
                        _progress.value = 1f

                        // Pasar a la siguiente fase
                        currentPhaseIndex = (currentPhaseIndex + 1) % fases.size
                        currentPhaseStartTime = currentTime
                        _progress.value = 0f
                    }

                    delay(16L) // ~60 FPS
                }

                // Si el tiempo total se agotó, salir del ciclo
                if (_remainingTimeMs.value <= 0L) {
                    break
                }
            }

            // Finalizar sesión
            _isRunning.value = false
            if (_remainingTimeMs.value <= 0L) {
                _uiState.update { it.copy(estado = "Finalizado") }
                _progress.value = 0f
                _currentPhase.value = EstadosRespiracion.INHALING
            } else {
                _uiState.update { it.copy(estado = "Pausado") }
                pausedTimeMs = System.currentTimeMillis()
            }
        }
    }

    private fun save(){
        viewModelScope.launch {
            val result = sesionRespository.save(_uiState.value.toDto())
            _uiState.update {
                it.copy(
                    error = when (result) {
                        is Resource.Success -> "Reflexión guardada correctamente"
                        is Resource.Error -> result.message ?: "Error al guardar la reflexión"
                        else -> null
                    }
                )
            }

        }
    }


}