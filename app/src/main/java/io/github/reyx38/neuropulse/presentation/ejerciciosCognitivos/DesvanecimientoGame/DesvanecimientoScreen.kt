package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.DesvanecimientoGame

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.presentation.UiCommon.Dialogs.ConfirmationDialog
import io.github.reyx38.neuropulse.presentation.UiCommon.getFrase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesvanecimientoScreen(
    onNavigateBack: () -> Unit,
    ejercicioCognitivoId : Int = 0,
    viewModel: DesvanecimientoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    // Estado para controlar el diálogo de confirmación de salida
    var showExitDialog by remember { mutableStateOf(false) }

    // Función para manejar la salida
    fun handleExit() {
        if (uiState.isStarted && !uiState.juegoTerminado) {
            // Si el juego está activo, mostrar diálogo de confirmación
            showExitDialog = true
        } else {
            // Si no está jugando o ya terminó, salir directamente
            viewModel.resetGame()
            onNavigateBack()
        }
    }

    // Interceptar el botón de retroceso del sistema
    BackHandler {
        handleExit()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Desvanecimiento",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.isStarted) {
                                Text(
                                    text = "Ronda ${uiState.currentRound}/${uiState.totalRounds} • Puntos: ${uiState.puntuacionTotal}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when {
                !uiState.isStarted -> {
                    viewModel.onEvent(DesvanecimientoEvent.EjercicioCognitivoChange(ejercicioCognitivoId))
                    StartScreen(
                        onStartGame = { viewModel.startGame() },
                        colorScheme = colorScheme
                    )
                }

                uiState.juegoTerminado -> {
                    ResultScreen(
                        score = uiState.puntuacionTotal,
                        onPlayAgain = {
                            viewModel.onEvent(DesvanecimientoEvent.Save)
                            viewModel.startGame() },
                        onBackToMenu = {
                            viewModel.onEvent(DesvanecimientoEvent.Save)
                            onNavigateBack()
                        },
                        colorScheme = colorScheme
                    )
                }

                else -> {
                    // Juego activo
                    GameContent(
                        gameState = uiState,
                        onPositionClick = { viewModel.selectPosition(it) },
                        onSubmitAnswer = { viewModel.submitAnswer() },
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para salir del juego
    if (showExitDialog) {
        ConfirmationDialog(
            onConfirm = {
                showExitDialog = false
                // Marcar el juego como incompleto (juegoTerminado = false)
                viewModel.onEvent(DesvanecimientoEvent.JuegoIncompleto)
                viewModel.resetGame()
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

@Composable
private fun StartScreen(
    onStartGame: () -> Unit,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Observa rápido!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Las imágenes desaparecen en segundos. Memoriza sus posiciones y pon a prueba tu memoria visual.",
            fontSize = 16.sp,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            Text(
                text = "Comenzar Juego",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun GameContent(
    gameState: DesvanecimientoUiState,
    onPositionClick: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    colorScheme: ColorScheme
) {
    // Instrucciones y timer
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (gameState.timeRemaining > 0)
                colorScheme.primaryContainer else colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                gameState.timeRemaining > 0 -> {
                    Text(
                        text = "¡Memoriza las posiciones!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${gameState.timeRemaining}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }

                gameState.isAnswering -> {
                    Text(
                        text = "Selecciona las posiciones que recuerdes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${gameState.selectedPositions.size} de ${gameState.imagePositions.size} seleccionadas",
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                gameState.showResult -> {
                    Icon(
                        imageVector = if (gameState.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (gameState.isCorrect) Color.Green else Color.Red
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (gameState.isCorrect) "¡Correcto! +100 puntos" else "Incorrecto +25 puntos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (gameState.isCorrect) Color.Green else Color.Red
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Cuadrícula 4x4
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        items(16) { position ->
            val showImage = gameState.showImages && position in gameState.imagePositions
            val isSelected = position in gameState.selectedPositions
            val showCorrect = gameState.showResult && position in gameState.imagePositions

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            showImage -> colorScheme.primary
                            showCorrect -> if (position in gameState.selectedPositions) Color.Green else Color.Red.copy(alpha = 0.3f)
                            isSelected -> colorScheme.primary.copy(alpha = 0.7f)
                            else -> colorScheme.surfaceVariant
                        }
                    )
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) colorScheme.primary else colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = gameState.isAnswering) {
                        onPositionClick(position)
                    },
                contentAlignment = Alignment.Center
            ) {
                when {
                    showImage -> {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    showCorrect && position in gameState.imagePositions -> {
                        Icon(
                            imageVector = if (position in gameState.selectedPositions)
                                Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    isSelected -> {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Botón de enviar respuesta
    if (gameState.isAnswering) {
        Button(
            onClick = onSubmitAnswer,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = gameState.selectedPositions.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            Text(
                text = "Confirmar Respuesta",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ResultScreen(
    score: Int,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Juego Completado!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Puntuación Final",
            fontSize = 16.sp,
            color = colorScheme.onSurfaceVariant
        )

        Text(
            text = "$score puntos",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBackToMenu,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Menú Principal")
            }

            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                Text("Jugar Otra Vez")
            }
        }
    }
}