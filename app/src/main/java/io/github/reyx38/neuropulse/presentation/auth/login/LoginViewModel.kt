package io.github.reyx38.neuropulse.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    //Validar sesion existente
    init {

    }

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.Login -> login()
            LoginUiEvent.New -> new()
            is LoginUiEvent.NombreChange -> onNombreChange(event.nombre)
            is LoginUiEvent.PasswordChange -> onpasswordChange(event.password)
        }
    }

    private fun onNombreChange(nombre: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    nombre = nombre
                )
            }
        }

    }

    private fun onpasswordChange(password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    password = password
                )
            }
        }
    }

    private fun new() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    nombre = "",
                    password = "",
                    isLoading = false,
                    errorPassword = "",
                    errorNombre = "",
                    user = null
                )
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            authRepository.login(_uiState.value.nombre, _uiState.value.password).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                error = result.message ?: "Error al cargar el usuario",
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                user = result.data,
                                error = null,
                                errorNombre = null,
                                errorPassword = null
                            )
                        }
                    }
                }
            }
        }
    }


}