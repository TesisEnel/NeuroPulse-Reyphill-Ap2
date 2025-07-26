package io.github.reyx38.neuropulse.presentation.usuarios.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.remote.dto.UsuarioDto
import io.github.reyx38.neuropulse.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.EmailChange -> onEmailChange(event.email)
            RegisterUiEvent.New -> new()
            is RegisterUiEvent.NombreChange -> onNombreChange(event.nombre)
            is RegisterUiEvent.PasswordChange -> onPasswordChange(event.password)
            RegisterUiEvent.Save -> registrar()
            is RegisterUiEvent.TelefonoChange -> onTelefonoChange(event.telefono)
            is RegisterUiEvent.PasswordConfirmChange -> onPasswordConfirmChange(event.passwordConfirm)
        }
    }

    private fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, error = null) // Limpiar error al cambiar
        }
    }

    private fun onNombreChange(nombre: String) {
        _uiState.update {
            it.copy(nombre = nombre, errorNombre = "", error = null)
        }
    }

    private fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password, errorPassword = "", error = null)
        }
    }

    private fun onTelefonoChange(telefono: String) {
        _uiState.update {
            it.copy(telefono = telefono, errorTelefono = "", error = null)
        }
    }

    private fun onPasswordConfirmChange(password: String) {
        _uiState.update {
            it.copy(passwordConfirm = password, passwordConfirmError = "", error = null)
        }
    }

    private fun registrar() {
        // Primero validar antes de hacer la llamada
        if (!validar()) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isSuccess = false
                )
            }

            try {
                val dto = UsuarioDto(
                    usuarioId = null,
                    nombre = _uiState.value.nombre,
                    email = _uiState.value.email,
                    telefono = _uiState.value.telefono,
                    token = null,
                    password = _uiState.value.password,
                    imagenUrl = ""
                )

                authRepository.registar(dto).collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message ?: "Error desconocido",
                                    isSuccess = false
                                )
                            }
                        }
                        is Resource.Loading -> {
                            // Ya está en loading, no necesitamos actualizar de nuevo
                            // Pero si quieres asegurarte:
                            _uiState.update {
                                it.copy(isLoading = true)
                            }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    error = null
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error inesperado: ${e.message}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    fun new() {
        _uiState.update { RegisterUiState() }
    }

    private fun validar(): Boolean {
        var isValid = true
        val currentState = _uiState.value

        // Validar nombre
        val nombreError = when {
            currentState.nombre.isBlank() -> {
                isValid = false
                "El nombre es requerido"
            }
            currentState.nombre.length < 2 -> {
                isValid = false
                "El nombre debe tener al menos 2 caracteres"
            }
            else -> ""
        }

        // Validar email
        val emailError = when {
            currentState.email.isBlank() -> {
                isValid = false
                "El email es requerido"
            }
            !Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() -> {
                isValid = false
                "Email no válido"
            }
            else -> ""
        }

        // Validar teléfono
        val telefonoError = when {
            currentState.telefono.isBlank() -> {
                isValid = false
                "El teléfono es requerido"
            }
            currentState.telefono.length < 10 -> {
                isValid = false
                "El teléfono debe tener al menos 10 dígitos"
            }
            else -> ""
        }

        // Validar contraseña
        val passwordError = when {
            currentState.password.isBlank() -> {
                isValid = false
                "La contraseña es requerida"
            }
            currentState.password.length < 6 -> {
                isValid = false
                "La contraseña debe tener al menos 6 caracteres"
            }
            else -> ""
        }

        // Validar confirmación de contraseña
        val passwordConfirmError = when {
            currentState.passwordConfirm.isBlank() -> {
                isValid = false
                "Debe confirmar la contraseña"
            }
            currentState.passwordConfirm != currentState.password -> {
                isValid = false
                "Las contraseñas no coinciden"
            }
            else -> ""
        }

        _uiState.update {
            it.copy(
                errorNombre = nombreError,
                errorEmail = emailError,
                errorTelefono = telefonoError,
                errorPassword = passwordError,
                passwordConfirmError = passwordConfirmError
            )
        }

        return isValid
    }
}