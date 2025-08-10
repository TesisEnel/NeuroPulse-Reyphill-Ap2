package io.github.reyx38.neuropulse.presentation.usuarios.auth.login

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

    init {
        getUsuario()
    }

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.Login -> login()
            LoginUiEvent.New -> new()
            is LoginUiEvent.NombreChange -> onNombreChange(event.nombre)
            is LoginUiEvent.PasswordChange -> onPasswordChange(event.password)
        }
    }

    private fun onNombreChange(nombre: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    nombre = nombre,
                    errorNombre = ""
                )
            }
        }

    }

    private fun onPasswordChange(password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    password = password,
                    errorPassword = ""
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
                    user = null,
                    error = null
                )
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            if (validar()) {
                authRepository.login(_uiState.value.nombre, _uiState.value.password)
                    .collect { result ->
                        when (result) {
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
                                        isLoading = true,
                                        user = result.data,
                                        error = null,
                                        errorNombre = null,
                                        errorPassword = null
                                    )
                                }
                            }
                        }
                    }
            } else {
                return@launch
            }
        }
    }

    private fun getUsuario() {
        viewModelScope.launch {
            val user = authRepository.getUsuario()
            _uiState.update {
                it.copy(
                    user = user,
                    isLoading = user != null
                )
            }
        }
    }

    private fun validar(): Boolean {
        if (_uiState.value.nombre.isEmpty()) {
            _uiState.update {
                it.copy(
                    errorNombre = "El nombre esta vacio"
                )
            }
            return false
        }

        if (_uiState.value.nombre.length > 30) {
            _uiState.update {
                it.copy(
                    errorNombre = "El nombre es muy largo"
                )
            }
            return false
        }

        if (_uiState.value.password.isEmpty()) {
            _uiState.update {
                it.copy(
                    errorPassword = "La contraseña esta vacia"
                )
            }
            return false
        }

        if (_uiState.value.password.length > 12 || _uiState.value.password.length < 5) {
            _uiState.update {
                it.copy(
                    errorPassword = "la contraseña debe estar entre 5 y 12 letras"
                )
            }
            return false
        }

        return true
    }

}