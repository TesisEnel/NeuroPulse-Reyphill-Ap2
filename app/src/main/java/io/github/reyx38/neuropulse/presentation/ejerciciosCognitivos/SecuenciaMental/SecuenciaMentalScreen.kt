package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.SecuenciaMental

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
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
import io.github.reyx38.neuropulse.presentation.UiCommon.Dialogs.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuenciaMentalScreen(
    viewModel: SecuenciaMentalViewModel = hiltViewModel(),
    ejercicioCognitivoId: Int = 0,
    onNavigateBack: () -> Unit = {},
    onJuegoCompletado: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    viewModel.onEvent(SecuenciaMentalEvent.EjercicioCognitivoChange(ejercicioCognitivoId))

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
                                    text = "Secuencia Mental",
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
                                onClick = { handleExit() },
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
                            viewModel.onEvent(SecuenciaMentalEvent.Save)
                            viewModel.reiniciarJuego() },
                        onNavigateBack = {
                            viewModel.onEvent(SecuenciaMentalEvent.Save)
                            onNavigateBack() },
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
                    viewModel.onEvent(SecuenciaMentalEvent.JuegoIncompleto)
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
    val isLowTime = timeRemaining <= 10
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
fun GamePlayScreen(
    state: SecuenciaMentalUiState,
    viewModel: SecuenciaMentalViewModel,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instrucciones
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = "Ordena los números de menor a mayor",
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de números desordenados
        Text(
            text = "Números disponibles",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 160.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(state.numerosDesordenados) { numero ->
                    NumberCard(
                        number = numero,
                        isSelected = state.respuestaUsuario.contains(numero),
                        onClick = {
                            if (!state.respuestaUsuario.contains(numero)) {
                                viewModel.agregarNumero(numero)
                            }
                        },
                        colorScheme = colorScheme
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de respuesta del usuario
        Text(
            text = "Tu secuencia ordenada",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.respuestaUsuario.isEmpty()) {
                    Text(
                        text = "Selecciona los números en orden",
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(state.respuestaUsuario.size) { index ->
                            val numero = state.respuestaUsuario[index]
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SequenceNumberCard(
                                    number = numero,
                                    position = index + 1,
                                    colorScheme = colorScheme
                                )
                                if (index < state.respuestaUsuario.size - 1) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (state.respuestaUsuario.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        IconButton(
                            onClick = { viewModel.quitarUltimoNumero() },
                            modifier = Modifier
                                .background(
                                    colorScheme.errorContainer.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Deshacer último",
                                tint = colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Resultado de la ronda
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
fun NumberCard(
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "number_scale"
    )

    Card(
        modifier = Modifier
            .size(55.dp)
            .scale(scale)
            .clickable(enabled = !isSelected) { onClick() },
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                colorScheme.secondary.copy(alpha = 0.3f)
            else
                colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 1.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = if (isSelected) colorScheme.onSurfaceVariant else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SequenceNumberCard(
    number: Int,
    position: Int,
    colorScheme: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "#$position",
            fontSize = 10.sp,
            color = colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier.size(45.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.tertiary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    color = colorScheme.onTertiary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
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

