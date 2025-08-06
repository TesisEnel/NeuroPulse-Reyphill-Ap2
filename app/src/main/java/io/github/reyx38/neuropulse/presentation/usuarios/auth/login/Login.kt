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
        uiState,
        viewModel::onEvent,
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
        Spacer(Modifier.height(75.dp))

        Image(
            painter = painterResource(R.drawable.brain),
            contentDescription = null,
            modifier = Modifier
                .size(160.dp)
                .padding(top = 8.dp)
        )

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(!uiState.isLoading) {

                Column {
                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = { onEvent(LoginUiEvent.NombreChange(it)) },
                        label = { Text("Nombre de usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        isError = !uiState.errorNombre.isNullOrEmpty(),
                        supportingText = {
                            AnimatedVisibility(
                                visible = !uiState.errorNombre.isNullOrEmpty(),
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
                                        text = uiState.errorNombre ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Column {
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
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        isError = !uiState.errorPassword.isNullOrEmpty(),
                        supportingText = {
                            AnimatedVisibility(
                                visible = !uiState.errorPassword.isNullOrEmpty(),
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
                                        text = uiState.errorPassword ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    )

                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        AnimatedVisibility(
            visible = uiState.isLoading,
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

        Button(
            onClick = { onEvent(LoginUiEvent.Login) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            shape = CircleShape,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            enabled = !uiState.isLoading
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (uiState.isLoading)
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                                )
                            )
                        else gradient,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.isLoading) "Cargando..." else "Login",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        TextButton(
            onClick = { goToRegister() },
            enabled = !uiState.isLoading
        ) {
            Text("Create An Account")
        }
    }
}