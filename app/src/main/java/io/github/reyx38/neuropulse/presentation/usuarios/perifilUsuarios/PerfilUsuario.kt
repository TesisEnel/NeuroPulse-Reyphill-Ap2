package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.R
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.OpcionesUsuario.LogoutScreen
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.OpcionesUsuario.ProfileDetailScreen


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
                onBack = { vistaActual.value = "main" })
        }

        "favorite" -> FavoriteScreen(onBack = { vistaActual.value = "main" })
        "settings" -> SettingsScreen(onBack = { vistaActual.value = "main" })
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
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuraciones",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Aquí irán los ajustes del usuario.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Frases favoritas",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Aquí aparecerán tus elementos favoritos.",
                style = MaterialTheme.typography.bodyLarge
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi perfil",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateMenu) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileHeader(
                uiSate.usuario?.nombreUsuario,
                uiSate.usuario?.email
            )

            ProfileMenuItem(
                icon = Icons.Default.Person,
                title = "Profile",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = { onSectionSelect("profile") }
            )

            ProfileMenuItem(
                icon = Icons.Default.Favorite,
                title = "Favorite",
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.secondary,
                onClick = { onSectionSelect("favorite") }
            )
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { onSectionSelect("settings") }
            )

            ProfileMenuItem(
                icon = Icons.Default.ExitToApp,
                title = "Logout",
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { showLogoutDialog = true }
            )

            LogoutScreen(
                isVisible = showLogoutDialog,
                onDismiss = { showLogoutDialog = false },
                onConfirmLogout = {
                    showLogoutDialog = false
                    onEvent(UsuarioEvent.Delete)
                    onNavigateLogin()
                })
        }
    }
}

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
            Image(
                painter = painterResource(R.drawable.brain),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Verified",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
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
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


