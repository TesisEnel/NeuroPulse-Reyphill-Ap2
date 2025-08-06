package io.github.reyx38.neuropulse.presentation.usuarios.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.R
import kotlinx.coroutines.delay

@Composable
fun RegistarScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    goToHome: () -> Unit,
    goToLogin: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            delay(1500)
            goToHome()
        }
    }

    RegisterBodyScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onSignInClick = goToLogin
    )
}

@Composable
fun RegisterBodyScreen(
    uiState: RegisterUiState,
    onEvent: (RegisterUiEvent) -> Unit,
    onSignInClick: () -> Unit = {},
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.brain),
            contentDescription = "Register Illustration",
            modifier = Modifier
                .height(160.dp)
                .padding(top = 16.dp)
        )

        Text(
            text = "Registrate",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )

        // Mensaje de error general
        if (!uiState.error.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (uiState.isSuccess) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¡Cuenta creada exitosamente!",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if(!uiState.isLoading) {
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { onEvent(RegisterUiEvent.NombreChange(it)) },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                isError = uiState.errorNombre.isNotEmpty(),
                supportingText = if (uiState.errorNombre.isNotEmpty()) {
                    { Text(text = uiState.errorNombre, color = MaterialTheme.colorScheme.error) }
                } else null
            )

            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = { onEvent(RegisterUiEvent.TelefonoChange(it)) },
                label = { Text("Telefono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                isError = uiState.errorTelefono.isNotEmpty(),
                supportingText = if (uiState.errorTelefono.isNotEmpty()) {
                    { Text(text = uiState.errorTelefono, color = MaterialTheme.colorScheme.error) }
                } else null
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { onEvent(RegisterUiEvent.EmailChange(it)) },
                label = { Text("Correo electronico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                isError = uiState.errorEmail.isNotEmpty(),
                supportingText = if (uiState.errorEmail.isNotEmpty()) {
                    { Text(text = uiState.errorEmail, color = MaterialTheme.colorScheme.error) }
                } else null
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { onEvent(RegisterUiEvent.PasswordChange(it)) },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password"
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                isError = uiState.errorPassword.isNotEmpty(),
                supportingText = if (uiState.errorPassword.isNotEmpty()) {
                    { Text(text = uiState.errorPassword, color = MaterialTheme.colorScheme.error) }
                } else null
            )

            OutlinedTextField(
                value = uiState.passwordConfirm,
                onValueChange = { onEvent(RegisterUiEvent.PasswordConfirmChange(it)) },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Confirm Password"
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                isError = uiState.passwordConfirmError.isNotEmpty(),
                supportingText = if (uiState.passwordConfirmError.isNotEmpty()) {
                    {
                        Text(
                            text = uiState.passwordConfirmError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else null
            )
        }

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = { onEvent(RegisterUiEvent.Save) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = !uiState.isLoading
        ) {
            Text(
                text = if (uiState.isLoading) "Creando cuenta..." else "Crear una cuenta",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        TextButton(
            onClick = onSignInClick,
            enabled = !uiState.isLoading
        ) {
            Text("Iniciar sesión")
        }

    }
}