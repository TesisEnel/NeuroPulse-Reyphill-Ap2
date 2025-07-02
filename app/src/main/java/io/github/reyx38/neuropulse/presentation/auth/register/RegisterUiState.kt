package io.github.reyx38.neuropulse.presentation.auth.register

data class RegisterUiState (
    val usuarioId: Int? = null,
    val nombre: String = "",
    val errorNombre: String = "",
    val email: String = "",
    val errorEmail: String = "",
    val telefono:  String = "",
    val errorTelefono: String = "",
    val password: String = "",
    val errorPassword: String = "",
    val passwordConfirm: String = "",
    val passwordConfirmError: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null

)