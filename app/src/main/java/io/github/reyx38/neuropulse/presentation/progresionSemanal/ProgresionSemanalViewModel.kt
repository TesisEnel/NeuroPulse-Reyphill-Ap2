package io.github.reyx38.neuropulse.presentation.progresionSemanal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.repository.AuthRepository
import io.github.reyx38.neuropulse.data.repository.ProgresionSemanalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgresionSemanalViewModel @Inject constructor(
    private val progresionRepository: ProgresionSemanalRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProgresionSemanalUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getUsuario()
    }

    fun getUsuario() {
        viewModelScope.launch {
            val user = authRepository.getUsuario()
            _uiState.update {
                it.copy(
                    usuarioId = user?.usuarioId,
                )
            }
            progresionRepository.actulizarProgresoSemanal(_uiState.value.usuarioId ?: 0)
        }
    }

    fun obtenerProgresionActual(usuarioId: Int?){
        viewModelScope.launch {
            if (usuarioId != null){
                progresionRepository.obtenerProgesionSemanalActual(usuarioId).collect { resources ->
                    when(resources) {
                        is Resource.Error<*> -> _uiState.update {
                        it.copy(
                            error = resources.message ?: "Error desconocido"
                        )
                    }
                        is Resource.Loading -> _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                        is Resource.Success -> _uiState.update {
                            it.copy(
                                progresionActual = resources.data ?: null,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }

    }
    fun refreshProgresion() {
        obtenerProgresionActual(_uiState.value.usuarioId)
    }
}
