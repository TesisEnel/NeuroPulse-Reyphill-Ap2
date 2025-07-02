package io.github.reyx38.neuropulse.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.R


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    goToHome: () -> Unit,
    goToRegister: () -> Unit

) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.user) {
        if (uiState.user != null) {
            goToHome()
        }
    }
    if (uiState.isLoading || uiState.error != null) {
        AlertDialog(
            onDismissRequest = { /* bloquea cierre mientras carga */ },
            confirmButton = {
                if (uiState.error != null) {
                    TextButton(onClick = { viewModel.onEvent(LoginUiEvent.New) }) {
                        Text("Cerrar")
                    }
                }
            },
            title = { Text("Autenticando") },
            text = {
                if (uiState.isLoading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Por favor espera…")
                    }
                } else {
                    Text(uiState.error ?: "")
                }
            }
        )
    }

    LoginBodyScreen(
        uiState,
        viewModel::onEvent,
        goToHome = goToHome,
        goToRegister = goToRegister

    )

}

@Composable
fun LoginBodyScreen(
    uiState: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    goToHome: () -> Unit,
    goToRegister: () -> Unit

) {

    var showPass by remember { mutableStateOf(false) }

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
    ) {
        Spacer(Modifier.height(100.dp))

        Image(
            painter = painterResource(R.drawable.brain),
            contentDescription = null,
            modifier = Modifier
                .size(160.dp)
                .padding(top = 8.dp)
        )

        Text(
            text = "Iniciar sesion",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = { onEvent(LoginUiEvent.NombreChange(it)) },
            label = { Text("Nombre de usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onEvent(LoginUiEvent.PasswordChange(it)) },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon =
                    if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(icon, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onEvent(LoginUiEvent.Login) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            shape = CircleShape,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Login",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        TextButton(onClick = { goToRegister() }) {
            Text("Create An Account")
        }
        TextButton(onClick = {}) {
            Text("Reset Password")
        }
    }
}
