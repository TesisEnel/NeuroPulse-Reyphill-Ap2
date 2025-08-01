package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.OpcionesUsuario

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioEvent
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioUiState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalMaterial3Api::class, ExperimentalEncodingApi::class)
@Composable
fun ProfileDetailScreen(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit,
    onBack: () -> Unit
) {
    if (!uiState.usuario?.imagenPerfil.isNullOrEmpty() && uiState.imagen.isNullOrEmpty()) {
        onEvent(
            UsuarioEvent.ImagenChange(uiState.usuario.imagenPerfil)
        )
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                selectedImageUri = it
                val imageBytes = context.contentResolver.openInputStream(uri)?.readBytes()
                imageBytes?.let { bytes ->
                    val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                    onEvent(
                        UsuarioEvent.ImagenChange(base64)
                    )
                }
            }
        }
    )

    val gradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
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

            // Profile Image
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
                            .clickable(onClick = {
                                val permission =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        android.Manifest.permission.READ_MEDIA_IMAGES
                                    } else {
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                                    }

                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        permission
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        launcher.launch("image/*")
                                    }

                                    else -> {
                                        requestPermissionLauncher.launch(permission)
                                    }
                                }
                            })
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.imagen == null) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            if (!uiState.imagen.isNullOrEmpty()) {
                                val imageBytes =
                                    Base64.decode(uiState.imagen, Base64.DEFAULT)
                                val bitmap = remember(uiState.imagen) {
                                    try {
                                        android.graphics.BitmapFactory.decodeByteArray(
                                            imageBytes,
                                            0,
                                            imageBytes.size
                                        )
                                            ?.asImageBitmap()
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                bitmap?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = "perfil",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            } else {
                                selectedImageUri?.let {
                                    AsyncImage(
                                        model = it,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }

                // Edit icon
                Card(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                ) {
                    Box(
                        Modifier.fillMaxSize(),
                        Alignment.Center
                    ) {
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

            // Input Fields
            ProfileInputField(
                "Nombre de usuario",
                uiState.nombre,
                { onEvent(UsuarioEvent.NombreChange(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileInputField(
                "Número de teléfono",
                uiState.telefono,
                { onEvent(UsuarioEvent.TelefonoChange(it)) }
            )

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

            // Loading indicator
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

            // Update button
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

            Spacer(modifier = Modifier.height(8.dp))

            // Message card
            AnimatedVisibility(
                visible = !uiState.updateMessage.isNullOrEmpty(),
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
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