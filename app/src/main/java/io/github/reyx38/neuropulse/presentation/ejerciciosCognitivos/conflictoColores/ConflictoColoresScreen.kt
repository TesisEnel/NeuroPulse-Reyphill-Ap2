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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.data.local.EstilosData.GameCardConfig
import io.github.reyx38.neuropulse.data.local.EstilosData.GameIconButtonConfig
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
    var showExitDialog by remember { mutableStateOf(false) }

    // Effects
    LaunchedEffect(uiState.usuarioId != null) {
        viewModel.onEvent(ConflictoColoresEvent.EjercicioCognitivoChange(ejercicioCognitivoId))
    }

    LaunchedEffect(uiState.juegoTerminado) {
        if (uiState.juegoTerminado) {
            onJuegoCompletado(uiState.puntuacionTotal)
        }
    }

    // Event handlers
    val handleExit = {
        if (!uiState.juegoTerminado) {
            showExitDialog = true
        } else {
            viewModel.reiniciarJuego()
            onNavigateBack()
        }
    }

    BackHandler { handleExit() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GameTopBar(
                uiState = uiState,
                colorScheme = colorScheme,
                onBackClick = handleExit,
                onRestartClick = { viewModel.reiniciarJuego() }
            )
        }
    ) { paddingValues ->
        GameContent(
            uiState = uiState,
            viewModel = viewModel,
            colorScheme = colorScheme,
            paddingValues = paddingValues,
            onNavigateBack = {
                viewModel.onEvent(ConflictoColoresEvent.Save)
                onNavigateBack()
            }
        )

        if (showExitDialog) {
            GameExitDialog(
                onConfirm = {
                    showExitDialog = false
                    viewModel.onEvent(ConflictoColoresEvent.JuegoIncompleto)
                    viewModel.reiniciarJuego()
                    onNavigateBack()
                },
                onDismiss = { showExitDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameTopBar(
    uiState: ConflictoColoresUiState,
    colorScheme: ColorScheme,
    onBackClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    GameCard(
        config = GameCardConfig(
            backgroundColor = colorScheme.surface,
            contentColor = colorScheme.onSurface,
            elevation = 4.dp,
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            TopAppBar(
                title = {
                    GameTopBarTitle(
                        currentRound = uiState.rondaActual,
                        totalScore = uiState.puntuacionTotal,
                        colorScheme = colorScheme
                    )
                },
                navigationIcon = {
                    GameIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        onClick = onBackClick,
                        config = GameIconButtonConfig(
                            backgroundColor = colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            contentColor = colorScheme.onSurface
                        )
                    )
                },
                actions = {
                    TimerIndicator(
                        timeRemaining = uiState.tiempoRestante,
                        colorScheme = colorScheme
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    GameIconButton(
                        icon = Icons.Default.Refresh,
                        contentDescription = "Reiniciar",
                        onClick = onRestartClick,
                        config = GameIconButtonConfig(
                            backgroundColor = colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            contentColor = colorScheme.onSurface
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            ProgressIndicator(
                current = uiState.palabrasCompletadas,
                total = uiState.palabrasPorRonda,
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun GameTopBarTitle(
    currentRound: Int,
    totalScore: Int,
    colorScheme: ColorScheme
) {
    Column {
        Text(
            text = "Conflicto de Colores",
            color = colorScheme.onSurface,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = "Ronda $currentRound/3 • $totalScore pts",
            color = colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun GameContent(
    uiState: ConflictoColoresUiState,
    viewModel: ConflictoColoresViewModel,
    colorScheme: ColorScheme,
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.juegoTerminado) {
            GameCompletedScreen(
                score = uiState.puntuacionTotal,
                onRestart = {
                    viewModel.onEvent(ConflictoColoresEvent.Save)
                    viewModel.reiniciarJuego()
                },
                onNavigateBack = {
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

@Composable
private fun GameCard(
    config: GameCardConfig,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = config.shape,
        colors = CardDefaults.cardColors(containerColor = config.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = config.elevation),
        content = { content() }
    )
}

@Composable
private fun GameIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    config: GameIconButtonConfig,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(config.size)
            .clip(CircleShape)
            .background(config.backgroundColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = config.contentColor,
            modifier = Modifier.size(config.iconSize)
        )
    }
}

@Composable
private fun TimerIndicator(
    timeRemaining: Int,
    colorScheme: ColorScheme
) {
    val isLowTime = timeRemaining <= 15
    val timerConfig = if (isLowTime) {
        GameCardConfig(
            backgroundColor = colorScheme.errorContainer.copy(alpha = 0.8f),
            contentColor = colorScheme.error,
            shape = RoundedCornerShape(10.dp)
        )
    } else {
        GameCardConfig(
            backgroundColor = colorScheme.primaryContainer.copy(alpha = 0.8f),
            contentColor = colorScheme.primary,
            shape = RoundedCornerShape(10.dp)
        )
    }

    val scale by animateFloatAsState(
        targetValue = if (isLowTime) 1.1f else 1f,
        animationSpec = tween(300),
        label = "timer_scale"
    )

    GameCard(
        config = timerConfig,
        modifier = Modifier.scale(scale)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = timerConfig.contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "${timeRemaining}s",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = timerConfig.contentColor
            )
        }
    }
}

@Composable
private fun ProgressIndicator(
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
        ProgressLabels(
            current = current,
            total = total,
            progress = progress,
            colorScheme = colorScheme
        )

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
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
private fun ProgressLabels(
    current: Int,
    total: Int,
    progress: Float,
    colorScheme: ColorScheme
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
}

@Composable
private fun GamePlayScreen(
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
        ModeInstructionCard(
            gameMode = state.modoJuego,
            colorScheme = colorScheme
        )

        Spacer(modifier = Modifier.height(24.dp))

        WordDisplayCard(
            word = state.palabraActual,
            wordColor = state.colorPalabra,
            colorScheme = colorScheme
        )

        Spacer(modifier = Modifier.height(24.dp))

        GameSectionTitle(
            colorScheme = colorScheme
        )

        Spacer(modifier = Modifier.height(12.dp))

        ColorOptionsGrid(
            coloresDisponibles = state.coloresDisponibles,
            onColorSelected = { viewModel.seleccionarRespuesta(it.nombre) },
            colorScheme = colorScheme
        )

        AnimatedGameResult(
            mostrandoResultado = state.mostrandoResultado,
            esRespuestaCorrecta = state.esRespuestaCorrecta,
            colorScheme = colorScheme
        )
    }
}

@Composable
private fun GameSectionTitle(
    colorScheme: ColorScheme
) {
    Text(
        text = "Selecciona tu respuesta",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = colorScheme.onSurface
    )
}

@Composable
private fun ColorOptionsGrid(
    coloresDisponibles: List<ColorInfo>,
    onColorSelected: (ColorInfo) -> Unit,
    colorScheme: ColorScheme
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.heightIn(max = 200.dp)
    ) {
        items(coloresDisponibles) { colorInfo ->
            ColorOptionCard(
                colorInfo = colorInfo,
                onClick = { onColorSelected(colorInfo) },
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun AnimatedGameResult(
    mostrandoResultado: Boolean,
    esRespuestaCorrecta: Boolean,
    colorScheme: ColorScheme
) {
    AnimatedVisibility(
        visible = mostrandoResultado,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeIn(tween(300))
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard(
                isCorrect = esRespuestaCorrecta,
                points = if (esRespuestaCorrecta) 100 else 25,
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun ModeInstructionCard(
    gameMode: String,
    colorScheme: ColorScheme
) {
    val isColorMode = gameMode == "COLOR"
    val modeConfig = if (isColorMode) {
        GameCardConfig(
            backgroundColor = colorScheme.secondaryContainer.copy(alpha = 0.4f),
            contentColor = colorScheme.secondary
        )
    } else {
        GameCardConfig(
            backgroundColor = colorScheme.tertiaryContainer.copy(alpha = 0.4f),
            contentColor = colorScheme.tertiary
        )
    }

    GameCard(
        config = modeConfig,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ModeHeader(
                isColorMode = isColorMode,
                contentColor = modeConfig.contentColor
            )

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
private fun ModeHeader(
    isColorMode: Boolean,
    contentColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isColorMode) Icons.Default.Palette else Icons.Default.TextFields,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isColorMode) "MODO COLOR" else "MODO TEXTO",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun WordDisplayCard(
    word: String,
    wordColor: Color,
    colorScheme: ColorScheme
) {
    GameCard(
        config = GameCardConfig(
            backgroundColor = colorScheme.surface,
            contentColor = wordColor,
            elevation = 6.dp,
            shape = RoundedCornerShape(20.dp)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
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
private fun ColorOptionCard(
    colorInfo: ColorInfo,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    GameCard(
        config = GameCardConfig(
            backgroundColor = colorInfo.color.copy(alpha = 0.15f),
            contentColor = colorInfo.color,
            elevation = 2.dp
        ),
        modifier = Modifier
            .aspectRatio(1.2f)
            .clickable { onClick() }
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
            ColorOptionContent(colorInfo = colorInfo, colorScheme = colorScheme)
        }
    }
}

@Composable
private fun ColorOptionContent(
    colorInfo: ColorInfo,
    colorScheme: ColorScheme
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@Composable
private fun ResultCard(
    isCorrect: Boolean,
    points: Int,
    colorScheme: ColorScheme
) {
    val resultConfig = if (isCorrect) {
        GameCardConfig(
            backgroundColor = colorScheme.primaryContainer.copy(alpha = 0.3f),
            contentColor = colorScheme.primary
        )
    } else {
        GameCardConfig(
            backgroundColor = colorScheme.errorContainer.copy(alpha = 0.3f),
            contentColor = colorScheme.error
        )
    }

    GameCard(
        config = resultConfig,
        modifier = Modifier.fillMaxWidth()
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
                tint = resultConfig.contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isCorrect) "¡Correcto! +$points puntos" else "Incorrecto. +$points puntos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = resultConfig.contentColor
            )
        }
    }
}

@Composable
private fun GameCompletedScreen(
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
        CompletionIcon(colorScheme = colorScheme)

        Spacer(modifier = Modifier.height(24.dp))

        CompletionTexts(colorScheme = colorScheme)

        Spacer(modifier = Modifier.height(16.dp))

        ScoreCard(score = score, colorScheme = colorScheme)

        Spacer(modifier = Modifier.height(32.dp))

        CompletionButtons(
            onNavigateBack = onNavigateBack,
            onRestart = onRestart,
            colorScheme = colorScheme
        )
    }
}

@Composable
private fun CompletionIcon(colorScheme: ColorScheme) {
    Icon(
        imageVector = Icons.Default.EmojiEvents,
        contentDescription = null,
        tint = colorScheme.primary,
        modifier = Modifier.size(80.dp)
    )
}

@Composable
private fun CompletionTexts(colorScheme: ColorScheme) {
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
}

@Composable
private fun ScoreCard(score: Int, colorScheme: ColorScheme) {
    GameCard(
        config = GameCardConfig(
            backgroundColor = colorScheme.primaryContainer.copy(alpha = 0.3f),
            contentColor = colorScheme.primary
        ),
        modifier = Modifier.fillMaxWidth()
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
}

@Composable
private fun CompletionButtons(
    onNavigateBack: () -> Unit,
    onRestart: () -> Unit,
    colorScheme: ColorScheme
) {
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

@Composable
private fun GameExitDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmationDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
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