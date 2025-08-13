package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.R
import kotlin.io.encoding.ExperimentalEncodingApi
import android.util.Base64
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.sp
import io.github.reyx38.neuropulse.presentation.uiCommon.dialogs.ConfirmationDialog
import io.github.reyx38.neuropulse.presentation.usuarios.EditarPerfil.ProfileDetailScreen


@Composable
fun ProfileScreen(
    viewModel: UsuarioViewModel = hiltViewModel(),
    goToMenu: () -> Unit = {},
    goToLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val vistaActual = remember { mutableStateOf("main") }

    when (vistaActual.value) {
        "main" -> {
            MainProfileScreen(
                uiSate = uiState,
                onNavigateMenu = goToMenu,
                onNavigateLogin = goToLogout,
                onSectionSelect = { vistaActual.value = it },
                onEvent = viewModel::onEvent
            )
        }

        "profile" -> {
            ProfileDetailScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBack = { vistaActual.value = "main" }
            )
        }

        "logout" -> {
            MainProfileScreen(
                uiSate = uiState,
                onNavigateMenu = goToMenu,
                onSectionSelect = { vistaActual.value = it },
                isView = true,
                onNavigateLogin = goToLogout,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainProfileScreen(
    uiSate: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit,
    onNavigateMenu: () -> Unit = {},
    onNavigateLogin: () -> Unit = {},
    onSectionSelect: (String) -> Unit,
    isView: Boolean = false,
) {
    var showLogoutDialog by remember { mutableStateOf(isView) }
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
                                onClick = { onNavigateMenu() },
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
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileHeader(
                uiSate.usuario?.nombreUsuario,
                uiSate.usuario?.imagenPerfil
            )

            ProfileMenuItem(
                icon = Icons.Default.Person,
                title = "Perfil",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = { onSectionSelect("profile") }
            )

            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Cerrar sesion",
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { showLogoutDialog = true }
            )

            if (showLogoutDialog) {
                ConfirmationDialog(
                    onConfirm = {
                        showLogoutDialog = false
                        onEvent(UsuarioEvent.Delete)
                        onNavigateLogin()
                    },
                    onDismiss = { showLogoutDialog = false },
                    iconoSuperior = Icons.AutoMirrored.Filled.ExitToApp,
                    titulo = "Cerrar sesion",
                    subTitulo = "¿Estás seguro de que deseas cerrar sesión?",
                    listaCondiciones = listOf(
                        "• No podra volver a iniciar sesion sin internet",
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProfileHeader(
    uiName: String?,
    uiImagen: String?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier.size(120.dp)
        ) {
            if (!uiImagen.isNullOrEmpty()) {
                val imageBytes = Base64.decode(uiImagen, Base64.DEFAULT)
                val bitmap = remember(uiImagen) {
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
                Image(
                    painter = painterResource(R.drawable.brain),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = uiName ?: "",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


