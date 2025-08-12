package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.conflictoColores

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.presentation.uiCommon.dialogs.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConflictoColoresScreen(
    viewModel: ConflictoColoresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    ejercicioCognitivoId: Int = 0,
    onJuegoCompletado: (Int) -> Unit = {}

) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    viewModel.onEvent(ConflictoColoresEvent.EjercicioCognitivoChange(ejercicioCognitivoId))

    LaunchedEffect(uiState.juegoTerminado) {
        if (uiState.juegoTerminado) {
            onJuegoCompletado(uiState.puntuacionTotal)
        }
    }
    var showExitDialog by remember { mutableStateOf(false) }

    fun handleExit() {
        if (!uiState.juegoTerminado) {
            showExitDialog = true
        } else {
            viewModel.reiniciarJuego()
            onNavigateBack()
        }
    }

    BackHandler {
        handleExit()
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
                                    text = "Conflicto de Colores",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Ronda ${uiState.rondaActual}/3 • ${uiState.puntuacionTotal} pts",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {handleExit()},
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Atrás",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        actions = {
                            TimerIndicator(
                                timeRemaining = uiState.tiempoRestante,
                                colorScheme = colorScheme
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = { viewModel.reiniciarJuego() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reiniciar",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )

                    ProgressIndicator(
                        current = uiState.palabrasCompletadas,
                        total = uiState.palabrasPorRonda,
                        colorScheme = colorScheme
                    )
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.juegoTerminado) {
                    GameCompletedScreen(
                        score = uiState.puntuacionTotal,
                        onRestart = {
                            viewModel.onEvent(ConflictoColoresEvent.Save)
                            viewModel.reiniciarJuego()  },
                        onNavigateBack ={
                            viewModel.onEvent(ConflictoColoresEvent.Save)
                            onNavigateBack()
                        },
                        colorScheme = colorScheme
                    )
                } else {
                    GamePlayScreen(
                        state = uiState,
                        viewModel = viewModel,
                        colorScheme = colorScheme
                    )
                }
            }
        }
        if (showExitDialog) {
            ConfirmationDialog(
                onConfirm = {
                    showExitDialog = false
                    viewModel.onEvent(ConflictoColoresEvent.JuegoIncompleto)
                    viewModel.reiniciarJuego()
                    onNavigateBack()
                },
                onDismiss = {
                    showExitDialog = false
                },
                iconoSuperior = Icons.Default.Warning,
                titulo = "¿Salir del juego?",
                subTitulo = "Tu partida está en progreso. Si sales ahora:",
                listaCondiciones = listOf(
                    "• Se perderá todo el progreso actual",
                    "• La partida quedará marcada como incompleta",
                    "• Tendrás que empezar desde el principio",
                    "• Recibiras una penalizacion por abandodar"
                )
            )
        }
    }
}

@Composable
fun TimerIndicator(
    timeRemaining: Int,
    colorScheme: ColorScheme
) {
    val isLowTime = timeRemaining <= 15
    val scale by animateFloatAsState(
        targetValue = if (isLowTime) 1.1f else 1f,
        animationSpec = tween(300),
        label = "timer_scale"
    )

    Card(
        modifier = Modifier
            .scale(scale),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLowTime)
                colorScheme.errorContainer.copy(alpha = 0.8f)
            else
                colorScheme.primaryContainer.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = if (isLowTime) colorScheme.error else colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "${timeRemaining}s",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLowTime) colorScheme.error else colorScheme.primary
            )
        }
    }
}

@Composable
fun ProgressIndicator(
    current: Int,
    total: Int,
    colorScheme: ColorScheme
) {
    val progress = if (total > 0) current.toFloat() / total.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Palabras: $current/$total",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 12.sp,
                color = colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = colorScheme.primary,
            trackColor = colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun GamePlayScreen(
    state: ConflictoColoresUiState,
    viewModel: ConflictoColoresViewModel,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instrucciones dinámicas con indicador de modo
        ModeInstructionCard(
            gameMode = state.modoJuego,
            colorScheme = colorScheme
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Palabra con color destacada
        WordDisplayCard(
            word = state.palabraActual,
            wordColor = state.colorPalabra,
            colorScheme = colorScheme
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Título para opciones
        Text(
            text = "Selecciona tu respuesta",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Opciones de respuesta en grid compacto
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items(state.coloresDisponibles) { colorInfo ->
                ColorOptionCard(
                    colorInfo = colorInfo,
                    onClick = { viewModel.seleccionarRespuesta(colorInfo.nombre) },
                    colorScheme = colorScheme
                )
            }
        }

        // Resultado de la respuesta
        AnimatedVisibility(
            visible = state.mostrandoResultado,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeIn(tween(300))
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard(
                isCorrect = state.esRespuestaCorrecta,
                points = if (state.esRespuestaCorrecta) 100 else 25,
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
fun ModeInstructionCard(
    gameMode: String,
    colorScheme: ColorScheme
) {
    val isColorMode = gameMode == "COLOR"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isColorMode)
                colorScheme.secondaryContainer.copy(alpha = 0.4f)
            else
                colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isColorMode) Icons.Default.Palette else Icons.Default.TextFields,
                    contentDescription = null,
                    tint = if (isColorMode) colorScheme.secondary else colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isColorMode) "MODO COLOR" else "MODO TEXTO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isColorMode) colorScheme.secondary else colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isColorMode)
                    "¡Selecciona el COLOR en que está escrita la palabra!"
                else
                    "¡Selecciona lo que DICE la palabra, no su color!",
                fontSize = 14.sp,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun WordDisplayCard(
    word: String,
    wordColor: Color,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word,
                fontSize = 40.sp,
                color = wordColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ColorOptionCard(
    colorInfo: ColorInfo,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier
            .aspectRatio(1.2f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorInfo.color.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = colorInfo.color.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Círculo de color
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(colorInfo.color, CircleShape)
                        .border(1.dp, colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = colorInfo.nombre,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorInfo.color,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ResultCard(
    isCorrect: Boolean,
    points: Int,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect)
                colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (isCorrect) colorScheme.primary else colorScheme.error,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isCorrect) "¡Correcto! +$points puntos" else "Incorrecto. +$points puntos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) colorScheme.primary else colorScheme.error
            )
        }
    }
}

@Composable
fun GameCompletedScreen(
    score: Int,
    onRestart: () -> Unit,
    onNavigateBack: () -> Unit,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de celebración
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Juego Completado!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Puntuación Final",
            fontSize = 18.sp,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = "$score puntos",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.onSurface
                )
            ) {
                Text("Salir")
            }

            Button(
                onClick = onRestart,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                )
            ) {
                Text("Jugar Otra Vez")
            }
        }
    }
}

