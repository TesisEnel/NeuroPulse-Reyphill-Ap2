package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.LogicaSombra

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.presentation.UiCommon.Dialogs.ConfirmationDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogicaSombraScreen(
    viewModel: ShadowLogicViewModel = hiltViewModel(),
    ejercicioCognitivoId: Int = 0,
    onNavigateBack: () -> Unit = {}
) {
    viewModel.onEvent(LogicaSombraEvent.EjercicioCognitivoChange(ejercicioCognitivoId))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    var showExitDialog by remember { mutableStateOf(false) }

    fun handleExit() {
        if ( !uiState.isGameCompleted) {
            showExitDialog = true
        } else {
            viewModel.restartGame()
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
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Lógica de Sombras",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ronda ${uiState.currentRound}/${uiState.totalRounds} • Puntos: ${uiState.score}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { handleExit() },
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
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues)
        ) {
            if (uiState.isGameCompleted) {
                GameCompletedScreen(
                    score = uiState.score,
                    onRestart = { viewModel.onEvent(LogicaSombraEvent.Save)
                        viewModel.startNewGame() },
                    onNavigateBack = {
                        viewModel.onEvent(LogicaSombraEvent.Save)
                        onNavigateBack() },
                    colorScheme = colorScheme
                )
            } else {
                GamePlayScreen(
                    uiState = uiState,
                    onAnswerSelected = { viewModel.selectAnswer(it) },
                    colorScheme = colorScheme
                )
            }
        }
        if (showExitDialog) {
            ConfirmationDialog(
                onConfirm = {
                    showExitDialog = false
                    viewModel.onEvent(LogicaSombraEvent.JuegoIncompleto)
                    viewModel.restartGame()
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
                ),
                textoInferior = "¿Seguro que deseas salir?",
                textoBotonConfirmacion = "Sí, salir",
                textoBotonDenegar = "Continuar jugando"
            )
        }
    }
}

@Composable
fun GamePlayScreen(
    uiState: LogicaSombraUiState,
    onAnswerSelected: (Int) -> Unit,
    colorScheme: ColorScheme
) {
    val gameRound = uiState.currentGameRound ?: return

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
                text = "Encuentra la sombra correcta de la figura mostrada",
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Figura original
        Text(
            text = "Figura Original",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ShapeCanvas(
                    shadowShape = gameRound.originalShape,
                    isOriginal = true,
                    colorScheme = colorScheme
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Opciones de sombras
        Text(
            text = "Selecciona la sombra correcta",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(gameRound.shadowOptions) { shadow ->
                ShadowOptionCard(
                    shadowShape = shadow,
                    isSelected = uiState.selectedAnswerId == shadow.id,
                    isCorrect = uiState.showResult && shadow.isCorrectShadow,
                    isWrong = uiState.showResult && uiState.selectedAnswerId == shadow.id && !shadow.isCorrectShadow,
                    onClick = {
                        if (!uiState.showResult) {
                            onAnswerSelected(shadow.id)
                        }
                    },
                    colorScheme = colorScheme
                )
            }
        }

        // Resultado de la ronda
        AnimatedVisibility(
            visible = uiState.showResult,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeIn(tween(300))
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard(
                isCorrect = uiState.isCorrect,
                points = uiState.roundScore,
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
fun ShadowOptionCard(
    shadowShape: ShadowShape,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    val borderColor = when {
        isCorrect -> colorScheme.primary
        isWrong -> colorScheme.error
        isSelected -> colorScheme.primary.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    val backgroundColor = when {
        isCorrect -> colorScheme.primaryContainer.copy(alpha = 0.3f)
        isWrong -> colorScheme.errorContainer.copy(alpha = 0.3f)
        else -> colorScheme.surface
    }

    Card(
        modifier = Modifier
            .size(100.dp)
            .border(
                width = if (borderColor != Color.Transparent) 3.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ShapeCanvas(
                shadowShape = shadowShape,
                isOriginal = false,
                colorScheme = colorScheme
            )

            // Iconos de resultado
            if (isCorrect) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Correcto",
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp)
                )
            } else if (isWrong) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Incorrecto",
                    tint = colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ShapeCanvas(
    shadowShape: ShadowShape,
    isOriginal: Boolean,
    colorScheme: ColorScheme
) {
    Canvas(
        modifier = Modifier
            .size(60.dp)
            .rotate(shadowShape.rotation)
    ) {
        val color = if (isOriginal) colorScheme.primary else Color.Black.copy(alpha = 0.7f)
        drawShape(shadowShape.shape, color)
    }
}

fun DrawScope.drawShape(shapeType: ShapeType, color: Color) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = minOf(size.width, size.height) / 3

    when (shapeType) {
        ShapeType.CIRCLE -> {
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(centerX, centerY)
            )
        }
        ShapeType.SQUARE -> {
            drawRect(
                color = color,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2)
            )
        }
        ShapeType.TRIANGLE -> {
            val path = Path().apply {
                moveTo(centerX, centerY - radius)
                lineTo(centerX - radius, centerY + radius)
                lineTo(centerX + radius, centerY + radius)
                close()
            }
            drawPath(path = path, color = color)
        }
        ShapeType.DIAMOND -> {
            val path = Path().apply {
                moveTo(centerX, centerY - radius)
                lineTo(centerX + radius, centerY)
                lineTo(centerX, centerY + radius)
                lineTo(centerX - radius, centerY)
                close()
            }
            drawPath(path = path, color = color)
        }
        ShapeType.STAR -> {
            val path = Path()
            val outerRadius = radius
            val innerRadius = radius * 0.5f
            val angleStep = Math.PI / 5

            for (i in 0 until 10) {
                val angle = i * angleStep - Math.PI / 2
                val r = if (i % 2 == 0) outerRadius else innerRadius
                val x = centerX + (r * kotlin.math.cos(angle)).toFloat()
                val y = centerY + (r * kotlin.math.sin(angle)).toFloat()

                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path = path, color = color)
        }
        ShapeType.HEXAGON -> {
            val path = Path()
            val angleStep = Math.PI / 3

            for (i in 0 until 6) {
                val angle = i * angleStep
                val x = centerX + (radius * kotlin.math.cos(angle)).toFloat()
                val y = centerY + (radius * kotlin.math.sin(angle)).toFloat()

                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path = path, color = color)
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