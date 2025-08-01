package io.github.reyx38.neuropulse.presentation.Respiracion.SesionRespiracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.repository.AuthRepository
import io.github.reyx38.neuropulse.data.repository.RespiracionRepository
import io.github.reyx38.neuropulse.data.repository.SesionRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SesionRespiracionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val respiracionRepository: RespiracionRepository,
    private val sesionRepository: SesionRespository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SesionRespiracionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getUsuario()
        cargarRespiraciones()
    }
    fun getUsuario() {
        viewModelScope.launch {
            val user = authRepository.getUsuario()
            _uiState.update {
                it.copy(
                    usuario = user,
                )
            }
        }
    }
    fun cargarRespiraciones() {
        viewModelScope.launch {
            val list = respiracionRepository.obtenerRespiraciones()
            if (list.isNotEmpty()) {
                _uiState.update { it.copy(listRespiracion =  list) }
            } else {
                respiracionRepository.sincronizarRespiracionesDesdeApi().collect { resultado ->
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
                                listRespiracion = resultado.data ?: emptyList(),
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
    fun getSesiones(usuarioId: Int?){
        viewModelScope.launch {
            if (usuarioId != null && usuarioId > 0){
                sesionRepository.getSesiones(usuarioId).collect { resource ->
                    when(resource){
                        is Resource.Error -> _uiState.update { it.copy(error = resource.message ?: "Error desconocido" ) }
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success-> _uiState.update { it.copy(
                            listaSesiones = resource.data ?: emptyList(),
                            isLoading = false
                        ) }
                    }

                }
            }

        }

    }
}