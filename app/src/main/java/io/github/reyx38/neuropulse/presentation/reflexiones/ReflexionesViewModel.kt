package io.github.reyx38.neuropulse.presentation.reflexiones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.remote.dto.ReflexionDto
import io.github.reyx38.neuropulse.data.repository.AuthRepository
import io.github.reyx38.neuropulse.data.repository.ReflexionRepository
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReflexionesViewModel @Inject constructor(
    private val reflexionRepository: ReflexionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReflexionesUiState())
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
                    usuario = user,
                )
            }
        }
    }

    fun getReflexiones(usuarioId: Int?) {
        viewModelScope.launch {
            if (usuarioId != null && usuarioId > 0 ) {
                reflexionRepository.getReflexiones(usuarioId).collect { resource ->
                    when (resource) {
                        is Resource.Error -> _uiState.update {
                            it.copy(
                                error = resource.message ?: "Error desconocido"
                            )
                        }

                        is Resource.Loading -> _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }

                        is Resource.Success -> _uiState.update {
                            it.copy(
                                reflexiones = resource.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: ReflexionesEvent) {
        when (event) {
            ReflexionesEvent.New -> TODO()
            ReflexionesEvent.Save -> saveReflexion()
            is ReflexionesEvent.descripcionChange -> onDescripcionChange(event.descripcion)
            is ReflexionesEvent.estadoReflexion -> onEstadoChange(event.estadoReflexion)
            is ReflexionesEvent.usuarioChange -> onUsuarioChange(event.usuarioId)
            is ReflexionesEvent.Delete -> deleteReflexion(event.reflexionid)
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

    private fun onEstadoChange(estado: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    estadoReflexion = estado
                )
            }
        }
    }

    private fun onDescripcionChange(descripcion: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    descripcion = descripcion
                )
            }
        }
    }

    private fun saveReflexion() {
        viewModelScope.launch {
            val result = reflexionRepository.save(_uiState.value.toDto())
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

    fun findReflexion(refelxionId : Int?){
        viewModelScope.launch {
            if(refelxionId != null && refelxionId > 0){
                 val reflexion = reflexionRepository.find(refelxionId)
                _uiState.update {
                    it.copy(
                        reflexionId = reflexion.reflexionId,
                        estadoReflexion = reflexion.descripcion,
                        descripcion =  reflexion.estadoReflexion,
                        usuarioId = reflexion.usuarioId
                    )
                }
            }
        }
    }

    fun deleteReflexion(reflexionId: Int?){
        viewModelScope.launch {
            if (reflexionId != null) {
                reflexionRepository.deleteReflexion(reflexionId)
            }
        }
    }
}


fun ReflexionesUiState.toDto() = ReflexionDto(
    reflexionId = reflexionId ?: 0,
    usuarioId = usuarioId ?: 0,
    descripcion = descripcion ?: " ",
    estadoReflexion = estadoReflexion ?: " ",
    fechaCreacion = ""
)
