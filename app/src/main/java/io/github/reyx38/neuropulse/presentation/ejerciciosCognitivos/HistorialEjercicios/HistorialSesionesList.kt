package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.HistorialEjercicios

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.presentation.UiCommon.TimerUtils.formatearFecha
import io.github.reyx38.neuropulse.data.local.entities.EjerciciosCognitivoEntity
import io.github.reyx38.neuropulse.data.remote.dto.SesionJuegosDto
import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.CatalogoEjercicios.EjerciciosCognitivosUiState
import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.CatalogoEjercicios.EjerciciosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialEjerciciosScreen(
    usuarioId: Int,
    viewModel: EjerciciosViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSession by remember { mutableStateOf<SesionJuegosDto?>(null) }

    LaunchedEffect(usuarioId) {
        viewModel.getSesiones(usuarioId)
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
                                    text = "Historial de Ejercicios",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Cognitivos",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
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
            // Loading indicator
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
                            text = "Cargando historial...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Estadísticas rápidas
            if (!uiState.isLoading && uiState.sesiones.isNotEmpty()) {
                EstadisticasRapidas(
                    sesiones = uiState.sesiones,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            HistorialEjerciciosBodyList(
                uiState = uiState,
                modifier = Modifier.fillMaxSize(),
                onSessionClick = { session -> selectedSession = session }
            )
        }

        // Dialog para mostrar detalles de la sesión
        selectedSession?.let { session ->
            SesionEjercicioDetailDialog(
                session = session,
                ejercicios = uiState.ejercicios,
                onDismiss = { selectedSession = null }
            )
        }
    }
}

@Composable
fun EstadisticasRapidas(
    sesiones: List<SesionJuegosDto>,
    modifier: Modifier = Modifier
) {
    val completadas = sesiones.count { it.completado }
    val puntuacionPromedio = if (sesiones.isNotEmpty()) {
        sesiones.map { it.puntuacion }.average().toInt()
    } else 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EstadisticaItem(
                icon = Icons.Default.Psychology,
                label = "Total",
                value = sesiones.size.toString()
            )

            EstadisticaItem(
                icon = Icons.Default.CheckCircle,
                label = "Completadas",
                value = completadas.toString()
            )

            EstadisticaItem(
                icon = Icons.Default.TrendingUp,
                label = "Promedio",
                value = "$puntuacionPromedio pts"
            )
        }
    }
}

@Composable
fun EstadisticaItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HistorialEjerciciosBodyList(
    uiState: EjerciciosCognitivosUiState,
    modifier: Modifier = Modifier,
    onSessionClick: (SesionJuegosDto) -> Unit = {}
) {
    if (uiState.sesiones.isEmpty() && !uiState.isLoading) {
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
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay ejercicios para mostrar",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Comienza tu primer ejercicio cognitivo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    } else if (uiState.sesiones.isNotEmpty()) {
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
                items(uiState.sesiones) { sesion ->
                    SesionEjercicioCard(
                        sesion = sesion,
                        ejercicios = uiState.ejercicios,
                        onClick = { onSessionClick(sesion) }
                    )
                }
            }
        }
    }
}

@Composable
fun SesionEjercicioCard(
    sesion: SesionJuegosDto,
    ejercicios: List<EjerciciosCognitivoEntity>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val ejercicio = ejercicios.find { it.ejercicosCognitivosId == sesion.ejercicioCognitivoId }
    val nombreEjercicio = ejercicio?.titulo ?: "Ejercicio Desconocido"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Ícono circular en la parte superior
            Surface(
                shape = CircleShape,
                color = obtenerColorCategoria(nombreEjercicio),
                shadowElevation = 4.dp,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = obtenerIconoCategoria(nombreEjercicio),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = nombreEjercicio,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Información inferior
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Puntuación
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${sesion.puntuacion} pts",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Estado
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (sesion.completado)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = if (sesion.completado) "Completado" else "Incompleto",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (sesion.completado)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SesionEjercicioDetailDialog(
    session: SesionJuegosDto,
    ejercicios: List<EjerciciosCognitivoEntity>,
    onDismiss: () -> Unit
) {
    val ejercicio = ejercicios.find { it.ejercicosCognitivosId == session.ejercicioCognitivoId }
    val nombreEjercicio = ejercicio?.titulo ?: "Ejercicio Desconocido"
    val descripcion = ejercicio?.descripcion ?: "Sin descripción disponible"

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
                // Ícono grande
                Surface(
                    shape = CircleShape,
                    color = obtenerColorCategoria(nombreEjercicio),
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = obtenerIconoCategoria(nombreEjercicio),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(
                    text = "Detalles del Ejercicio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Información detallada
                EjercicioDetailRow(
                    icon = Icons.Default.Psychology,
                    label = "Ejercicio",
                    value = nombreEjercicio
                )

                EjercicioDetailRow(
                    icon = Icons.Default.Stars,
                    label = "Puntuación",
                    value = "${session.puntuacion} puntos",
                    valueColor = MaterialTheme.colorScheme.tertiary
                )

                EjercicioDetailRow(
                    icon = if (session.completado) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    label = "Estado",
                    value = if (session.completado) "Completado" else "Incompleto",
                    valueColor = if (session.completado)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )

                EjercicioDetailRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Fecha",
                    value = formatearFecha(session.fechaRealizacion.toString())
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón cerrar
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun EjercicioDetailRow(
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

// Funciones de utilidad para iconos y colores por categoría
@Composable
fun obtenerIconoCategoria(categoria: String): ImageVector {
    return when (categoria.lowercase()) {
        "memoria" -> Icons.Default.Psychology
        "atencion" -> Icons.Default.Visibility
        "concentracion" -> Icons.Default.CenterFocusStrong
        "velocidad" -> Icons.Default.Speed
        "logica" -> Icons.Default.Functions
        "calculo" -> Icons.Default.Calculate
        else -> Icons.Default.Psychology
    }
}

@Composable
fun obtenerColorCategoria(categoria: String): Color {
    return when (categoria.lowercase()) {
        "memoria" -> MaterialTheme.colorScheme.primary
        "atencion" -> MaterialTheme.colorScheme.secondary
        "concentracion" -> MaterialTheme.colorScheme.tertiary
        "velocidad" -> MaterialTheme.colorScheme.error
        "logica" -> MaterialTheme.colorScheme.outline
        "calculo" -> MaterialTheme.colorScheme.surfaceTint
        else -> MaterialTheme.colorScheme.primary
    }
}