package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.OpcionesUsuario

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioEvent
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit,
    onBack: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Imagen
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ProfileInputField("Nombre de usuario", uiState.nombre, { onEvent(UsuarioEvent.NombreChange(it)) })
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInputField("Número de teléfono", uiState.telefono, { onEvent(UsuarioEvent.TelefonoChange(it)) })
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInputField(
                label = "Correo Electrónico",
                value = uiState.email,
                onValueChange = { onEvent(UsuarioEvent.EmailChange(it)) },
                placeholder = "ejemplo@correo.com",
                isError = uiState.errorEmail?.isNotEmpty() == true,
                errorMessage = uiState.errorEmail ?: "",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(24.dp))


            AnimatedVisibility(
                visible = uiState.isUpdating,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(0.2f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Guardando cambios...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onEvent(UsuarioEvent.Save) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(),
                enabled = !uiState.isUpdating
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (uiState.isUpdating)
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                                    )
                                )
                            else gradient,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isUpdating) "Guardando..." else "Actualizar perfil",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            AnimatedVisibility(
                visible = !uiState.updateMessage.isNullOrEmpty(),
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        if (uiState.isError)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.updateMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (uiState.isError)
                                MaterialTheme.colorScheme.onErrorContainer
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        TextButton(onClick = { onEvent(UsuarioEvent.DismissMessage) }) {
                            Text("Cerrar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInputField(
    label: String,
    value: String?,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value ?: "",
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = if (isError && errorMessage.isNotBlank()) {
            { Text(text = errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null
    )
}
