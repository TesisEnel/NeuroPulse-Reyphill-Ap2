package io.github.reyx38.neuropulse.presentation.usuarios.EditarPerfil
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.reyx38.neuropulse.presentation.uiCommon.usuarioUtils.recordadImagen
import io.github.reyx38.neuropulse.presentation.uiCommon.usuarioUtils.recondarImangeBuscador
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioEvent
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioUiState
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.opcionesUsuario.SeccionImagen
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalEncodingApi::class)
@Composable
fun ProfileDetailScreen(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val imageHandler = recordadImagen(context, onEvent)
    val (launcher, permissionLauncher) = recondarImangeBuscador(imageHandler)

    InitializeProfileImage(uiState = uiState, onEvent = onEvent)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ProfileContent(
            uiState = uiState,
            onEvent = onEvent,
            onBack = onBack,
            onImageClick = {
                imageHandler.selecionarImagen(launcher, permissionLauncher)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit,
    onBack: () -> Unit,
    onImageClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "Mi Perfil",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { onBack() },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Atrás",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )


                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            SeccionImagen(
                imagen = uiState.imagen,
                onImageClick = onImageClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            PerfilInputs(
                uiState = uiState,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(24.dp))

            LoadingIndicator(isVisible = uiState.isUpdating)

            Spacer(modifier = Modifier.height(8.dp))

            UpdateButton(
                uiState = uiState,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(8.dp))

            MessageCard(
                uiState = uiState,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun InitializeProfileImage(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
    val shouldInitialize = !uiState.usuario?.imagenPerfil.isNullOrEmpty() &&
            uiState.imagen.isNullOrEmpty()

    if (shouldInitialize) {
        onEvent(UsuarioEvent.ImagenChange(uiState.usuario.imagenPerfil))
    }
}

@Composable
fun PerfilInputs(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
    PerfilInput(
        label = "Nombre de usuario",
        value = uiState.nombre,
        onValueChange = { onEvent(UsuarioEvent.NombreChange(it)) }
    )

    Spacer(modifier = Modifier.height(16.dp))

    PerfilInput(
        label = "Número de teléfono",
        value = uiState.telefono,
        onValueChange = { onEvent(UsuarioEvent.TelefonoChange(it)) },
    )

    Spacer(modifier = Modifier.height(16.dp))

    PerfilInput(
        label = "Correo Electrónico",
        value = uiState.email,
        onValueChange = { onEvent(UsuarioEvent.EmailChange(it)) },
        placeholder = "ejemplo@correo.com",
        isError = uiState.errorEmail?.isNotEmpty() == true,
        errorMessage = uiState.errorEmail ?: "",
    )
}

@Composable
fun PerfilInput(
    label: String,
    value: String?,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        supportingText = if (isError && errorMessage.isNotBlank()) {
            { Text(text = errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null
    )
}

@Composable
fun LoadingIndicator(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
}

@Composable
fun UpdateButton(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
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
                    brush = if (uiState.isUpdating) {
                        createDisabledGradient()
                    } else {
                        createGradientBrush()
                    },
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
}


@Composable
fun MessageCard(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
    AnimatedVisibility(
        visible = !uiState.updateMessage.isNullOrEmpty(),
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isError) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
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
                    color = if (uiState.isError) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { onEvent(UsuarioEvent.DismissMessage) }
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
private fun createGradientBrush(): Brush {
    return Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
private fun createDisabledGradient(): Brush {
    return Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        )
    )
}