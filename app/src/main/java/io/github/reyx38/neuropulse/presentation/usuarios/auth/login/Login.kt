package io.github.reyx38.neuropulse.presentation.usuarios.auth.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.R
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    goToHome: () -> Unit,
    goToRegister: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.user != null) {
        if (uiState.user != null) {
            delay(1000)
            goToHome()
        }
    }

    LoginBodyScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        goToRegister = goToRegister,
    )
}

@Composable
fun LoginBodyScreen(
    uiState: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    goToRegister: () -> Unit,
) {
    var showPass by remember { mutableStateOf(false) }
    val gradient = createLoginGradient()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
    ) {
        Spacer(Modifier.height(75.dp))

        LoginLogo()
        LoginTitle()

        if (!uiState.isLoading) {
            LoginFormFields(
                uiState = uiState,
                onEvent = onEvent,
                showPassword = showPass,
                onTogglePasswordVisibility = { showPass = !showPass }
            )
        }

        LoadingIndicator(isVisible = uiState.isLoading)

        LoginButton(
            isLoading = uiState.isLoading,
            gradient = gradient,
            onClick = { onEvent(LoginUiEvent.Login) }
        )

        RegisterButton(
            isEnabled = !uiState.isLoading,
            onClick = goToRegister
        )
    }
}

@Composable
private fun LoginLogo() {
    Image(
        painter = painterResource(R.drawable.brain),
        contentDescription = null,
        modifier = Modifier
            .size(160.dp)
            .padding(top = 8.dp)
    )
}

@Composable
private fun LoginTitle() {
    Text(
        text = "Iniciar sesión",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
}

@Composable
private fun LoginFormFields(
    uiState: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    showPassword: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ValidatedTextField(
            value = uiState.nombre,
            onValueChange = { onEvent(LoginUiEvent.NombreChange(it)) },
            label = "Nombre de usuario",
            errorMessage = uiState.errorNombre,
            isEnabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(4.dp))

        ValidatedTextField(
            value = uiState.password,
            onValueChange = { onEvent(LoginUiEvent.PasswordChange(it)) },
            label = "Contraseña",
            errorMessage = uiState.errorPassword,
            isEnabled = !uiState.isLoading,
            isPassword = true,
            showPassword = showPassword,
            onTogglePasswordVisibility = onTogglePasswordVisibility
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    isEnabled: Boolean,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled,
        isError = !errorMessage.isNullOrEmpty(),
        visualTransformation = if (isPassword && !showPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (isPassword) {
            {
                PasswordVisibilityToggle(
                    showPassword = showPassword,
                    onToggle = onTogglePasswordVisibility
                )
            }
        } else null,
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else {
            KeyboardOptions.Default
        },
        supportingText = {
            ErrorMessage(errorMessage = errorMessage)
        }
    )
}

@Composable
private fun PasswordVisibilityToggle(
    showPassword: Boolean,
    onToggle: () -> Unit
) {
    val icon = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility
    IconButton(onClick = onToggle) {
        Icon(icon, contentDescription = null)
    }
}

@Composable
private fun ErrorMessage(errorMessage: String?) {
    AnimatedVisibility(
        visible = !errorMessage.isNullOrEmpty(),
        enter = slideInVertically(
            initialOffsetY = { -it / 2 },
            animationSpec = tween(200, easing = EaseOutCubic)
        ) + fadeIn(tween(200)),
        exit = slideOutVertically(
            targetOffsetY = { -it / 2 },
            animationSpec = tween(150)
        ) + fadeOut(tween(150))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun LoadingIndicator(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300, easing = EaseOutCubic)
        ) + fadeIn(tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(200)
        ) + fadeOut(tween(200))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Iniciando sesion...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    gradient: Brush,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = CircleShape,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        enabled = !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isLoading) {
                        createDisabledGradient()
                    } else {
                        gradient
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isLoading) "Cargando..." else "Login",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun RegisterButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = isEnabled
    ) {
        Text("Create An Account")
    }
}

@Composable
private fun createLoginGradient(): Brush {
    return Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
private fun createDisabledGradient(): Brush {
    return Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        )
    )
}