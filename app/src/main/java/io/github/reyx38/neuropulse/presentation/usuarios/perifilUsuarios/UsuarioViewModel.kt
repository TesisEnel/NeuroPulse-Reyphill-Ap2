package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios

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
class UsuarioViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getUsuario()
    }


    fun onEvent(event: UsuarioEvent) {
        when (event) {
            UsuarioEvent.Delete -> cerraSesion()
            UsuarioEvent.New -> new()
            UsuarioEvent.Save -> update()
            UsuarioEvent.DismissMessage -> dismissMessage()
            is UsuarioEvent.EmailChange -> onEmailChange(event.email)
            is UsuarioEvent.NombreChange -> onNombreChange(event.nombre)
            is UsuarioEvent.TelefonoChange -> onTelefonoChange(event.telefono)
            is UsuarioEvent.UsuarioIdChange -> onUsuarioIdChange(event.usuarioId)
        }
    }

    private fun cerraSesion() {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            _uiState.update {
                it.copy(
                    usuario = null
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
                    nombre = user?.nombreUsuario ?: " ",
                    email = user?.email ?: " ",
                    telefono = user?.telefono ?: "Dato no registrado ",
                    usuario = user,
                )
            }
        }
    }

    private fun dismissMessage() {
        _uiState.update {
            it.copy(updateMessage = null, isError = false)
        }
    }

    private fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    private fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre) }
    }

    private fun onTelefonoChange(telefono: String) {
        _uiState.update { it.copy(telefono = telefono) }
    }

    private fun onUsuarioIdChange(usuarioId: Int?) {
        _uiState.update { it.copy(usuarioId = usuarioId) }
    }

    private fun new() {
        _uiState.update {
            it.copy(
                usuarioId = null,
                nombre = "",
                errorNombre = "",
                email = "",
                errorEmail = "",
                telefono = "",
                errorTelefono = "",
                usuario = null,
                isUpdating = false
            )
        }
    }

    private fun update() {
        if (!validar()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, updateMessage = null) }

            val currentState = _uiState.value
            val dto = UsuarioDto(
                usuarioId = currentState.usuarioId,
                nombre = currentState.nombre,
                email = currentState.email,
                telefono = currentState.telefono,
                imagenUrl = null,
                token = null,
                password = null
            )

            authRepository.updateUsuario(dto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isUpdating = true) }
                    }

                    is Resource.Success -> {
                        val updatedUser = authRepository.getUsuario()
                        _uiState.update {
                            it.copy(
                                isUpdating = false,
                                usuario = updatedUser,
                                updateMessage = "Perfil actualizado correctamente",
                                isError = false
                            )
                        }

                        // ⏳ Mostrar mensaje por 3 segundos
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(3000)
                            dismissMessage()
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isUpdating = false,
                                updateMessage = result.message ?: "Error al actualizar",
                                isError = true
                            )
                        }

                        // ⏳ Mostrar mensaje de error por 3 segundos
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(3000)
                            dismissMessage()
                        }
                    }
                }
            }
        }
    }

    private fun validar(): Boolean {
        var isValid = true
        val currentState = _uiState.value

        val nombre = currentState.nombre ?: ""
        val email = currentState.email ?: ""
        val telefono = currentState.telefono ?: ""

        val nombreError = when {
            nombre.isBlank() -> {
                isValid = false
                "El nombre es requerido"
            }

            nombre.length < 2 -> {
                isValid = false
                "El nombre debe tener al menos 2 caracteres"
            }

            else -> ""
        }

        val emailError = when {
            email.isBlank() -> {
                isValid = false
                "El email es requerido"
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                isValid = false
                "Email no válido"
            }

            else -> ""
        }

        val telefonoError = when {
            telefono.isBlank() -> {
                isValid = false
                "El teléfono es requerido"
            }

            telefono.length < 10 -> {
                isValid = false
                "El teléfono debe tener al menos 10 dígitos"
            }

            else -> ""
        }

        _uiState.update {
            it.copy(
                errorNombre = nombreError,
                errorEmail = emailError,
                errorTelefono = telefonoError,
            )
        }

        return isValid
    }

}
