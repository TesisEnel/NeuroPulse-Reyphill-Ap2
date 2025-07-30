package io.github.reyx38.neuropulse.presentation.Respiracion.SesionRespiracion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.data.remote.dto.SesionRespiracionDto
import io.github.reyx38.neuropulse.presentation.UiCommon.TimerUtils.formatearFecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionesRespiracionScreen(
    viewModel: SesionRespiracionViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSession by remember { mutableStateOf<SesionRespiracionDto?>(null) }

    LaunchedEffect(uiState.usuario?.usuarioId) {
        uiState.usuario?.usuarioId?.let {
            viewModel.getSesiones(it)
        }
    }
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
                                    text = "Mis Reflexiones",
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
            modifier = Modifier.padding(paddingValues)
        ) {
            // Loading indicator animado
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
                            text = "Cargando tus sesiones...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            SesionesRespiracionBodyList(
                uiState = uiState,
                modifier = Modifier.fillMaxSize(),
                onSessionClick = { session -> selectedSession = session }
            )
        }

        // Dialog para mostrar detalles de la sesión
        selectedSession?.let { session ->
            SesionDetailDialog(
                session = session,
                respiraciones = uiState.listRespiracion,
                onDismiss = { selectedSession = null },
            )
        }
    }
}

@Composable
fun SesionesRespiracionBodyList(
    uiState: SesionRespiracionUiState,
    modifier: Modifier = Modifier,
    onSessionClick: (SesionRespiracionDto) -> Unit = {}
) {
    // Solo mostrar el estado vacío si no está cargando y la lista está vacía
    if (uiState.listaSesiones.isEmpty() && !uiState.isLoading) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(
                animationSpec = tween(500, delayMillis = 200)
            ) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(500, delayMillis = 200, easing = EaseOutCubic)
            )
        ) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay sesiones para mostrar",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Comienza tu primera sesión de respiración",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    } else if (uiState.listaSesiones.isNotEmpty()) {
        AnimatedVisibility(
            visible = !uiState.isLoading,
            enter = fadeIn(
                animationSpec = tween(400, delayMillis = 100)
            ) + slideInVertically(
                initialOffsetY = { it / 6 },
                animationSpec = tween(400, delayMillis = 100, easing = EaseOutCubic)
            )
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(uiState.listaSesiones) { sesion ->
                    SesionRespiracionCard(
                        sesion = sesion,
                        respiraciones = uiState.listRespiracion,
                        onClick = { onSessionClick(sesion) }
                    )
                }
            }
        }
    }
}

@Composable
fun SesionRespiracionCard(
    sesion: SesionRespiracionDto,
    respiraciones: List<RespiracionWithInformacion>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val respiracionNombre = respiraciones.find {
        it.respiracion.idRespiracion == sesion.idRespiracion
    }?.respiracion?.nombre ?: "Desconocido"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono circular en la parte superior
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = obtenerIconoTecnica(nombreTecnica = respiracionNombre),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre de la técnica centrado
            Text(
                text = respiracionNombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Duración
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${sesion.duracionMinutos} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Estado
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (sesion.estado == "completo")
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = sesion.estado.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (sesion.estado == "completo")
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SesionDetailDialog(
    session: SesionRespiracionDto,
    respiraciones: List<RespiracionWithInformacion>,
    onDismiss: () -> Unit,
) {
    val respiracionNombre = respiraciones.find {
        it.respiracion.idRespiracion == session.idRespiracion
    }?.respiracion?.nombre ?: "Desconocido"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícono grande en la parte superior
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = obtenerIconoTecnica(nombreTecnica = respiracionNombre),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(
                    text = "Detalles de la Sesión",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Información de la sesión
                SessionDetailRow(
                    icon = Icons.Default.SelfImprovement,
                    label = "Técnica",
                    value = respiracionNombre
                )

                SessionDetailRow(
                    icon = Icons.Default.Schedule,
                    label = "Duración",
                    value = "${session.duracionMinutos} minutos"
                )

                SessionDetailRow(
                    icon = if (session.estado == "completo") Icons.Default.CheckCircle else Icons.Default.Cancel,
                    label = "Estado",
                    value = session.estado.replaceFirstChar { it.uppercase() },
                    valueColor = if (session.estado == "completo")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )

                session.fechaRealizada?.let { fecha ->
                    SessionDetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Fecha",
                        value = formatearFecha(fecha)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar")
                    }

                }
            }
        }
    }
}

@Composable
fun SessionDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

fun obtenerIconoTecnica(nombreTecnica: String): ImageVector {
    return when {
        nombreTecnica.contains("4-4") -> Icons.Default.Spa
        nombreTecnica.contains("4-7-8") -> Icons.Default.SelfImprovement
        nombreTecnica.contains("5-5") -> Icons.Default.FilterDrama
        nombreTecnica.contains("6-2-8") -> Icons.Default.Waves
        else -> Icons.Default.Air
    }
}
