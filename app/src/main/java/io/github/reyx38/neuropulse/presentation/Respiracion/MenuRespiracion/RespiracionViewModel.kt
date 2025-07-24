package io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.repository.RespiracionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RespiracionViewModel @Inject constructor(
    private val repository: RespiracionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RespiracionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarRespiraciones()
    }

    fun buscarRespiracion(respiracionId: Int){
        viewModelScope.launch {
            if(respiracionId > 0) {
                val respiracion = repository.find(respiracionId)
                _uiState.update {
                    it.copy(
                        respiracion = respiracion
                    )
                }
            }
        }
    }

    fun cargarRespiraciones() {
        viewModelScope.launch {
            val list = repository.obtenerRespiraciones()
            if (!list.isEmpty()){
                _uiState.update{
                    it.copy(
                        respiraciones = list
                    )
                }
            }else {
                repository.sincronizarRespiracionesDesdeApi().collect { resultado ->
                    when (resultado) {
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                        }

                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                respiraciones = resultado.data ?: emptyList(),
                                error = null
                            )
                        }

                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = resultado.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
        }
    }
}
