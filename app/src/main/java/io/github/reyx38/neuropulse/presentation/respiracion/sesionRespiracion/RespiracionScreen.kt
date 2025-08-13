package io.github.reyx38.neuropulse.presentation.respiracion.sesionRespiracion

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.RespiracionUiEvent
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.RespiracionUiState
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.RespiracionViewModel
import io.github.reyx38.neuropulse.presentation.uiCommon.dialogs.ConfirmationDialog
import io.github.reyx38.neuropulse.presentation.uiCommon.respiracionUtils.construirFases
import io.github.reyx38.neuropulse.presentation.uiCommon.respiracionUtils.obtenerFasesActuales
import io.github.reyx38.neuropulse.presentation.uiCommon.timerUtils.formatTimeMs

@Composable
fun RespiracionScreen(
    viewModel: RespiracionViewModel,
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.respiracion) {
        uiState.respiracion?.let {
            viewModel.onEvent(RespiracionUiEvent.DuracionMinutos(uiState.duracionMinutos))
        }
    }

    uiState.respiracion?.let {
        BreathingCircle(
            pattern = uiState,
            onEvent = viewModel::onEvent,
            onBackPressed = goBack,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingCircle(
    pattern: RespiracionUiState,
    onEvent: (RespiracionUiEvent) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: RespiracionViewModel,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.progress.collectAsState()
    val currentPhase by viewModel.currentPhase.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val remainingTimeMs by viewModel.remainingTimeMs.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    val phases = construirFases(pattern, MaterialTheme.colorScheme)
    val currentPhaseData = obtenerFasesActuales(phases, currentPhase)

    BackHandler {
        showExitDialog = true
    }

    fun handleExit() {
        if (isRunning || remainingTimeMs > 0) {
            showExitDialog = true
        } else {
            onBackPressed()
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
                                    text = pattern.respiracion?.respiracion?.nombre ?: " ",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
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
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TiempoRestanteCard(remainingTimeMs, pattern.estado)

            Spacer(modifier = Modifier.height(24.dp))

            CiruloProgreso(progress, currentPhaseData)

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick = { viewModel.togglePlayPause() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Control"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        viewModel.resetSesion()
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                ) {
                    Text("↻", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }



    if (remainingTimeMs <= 0L && !isRunning) {
        ConfirmationDialog(
            onConfirm = {
                onEvent(RespiracionUiEvent.EstadoChange("completo"))
                onBackPressed()
                onEvent(RespiracionUiEvent.Save)
            },
            onDismiss = {
                viewModel.resetSesion()
            },
            iconoSuperior = Icons.Default.AddTask,
            titulo = "Felicidades, Sesion Completada",
            subTitulo = "La sesion ha terminado:",
            listaCondiciones = listOf(
                "• Se agregara a tu historial de respiracion",
                "• La sesión quedará marcada como completa",
            ),
        )

    }

    if (showExitDialog) {
        ConfirmationDialog(
            onConfirm = {
                showExitDialog = false
                onEvent(RespiracionUiEvent.EstadoChange("Incompleto"))
                onBackPressed()
                onEvent(RespiracionUiEvent.Save)
            },
            onDismiss = {
                showExitDialog = false
            },
            iconoSuperior = Icons.Default.Warning,
            titulo = "¿Salir de la sesión?",
            subTitulo = "Tu sesión de respiración está en progreso. Si sales ahora:",
            listaCondiciones = listOf(
                "• Se perderá todo el progreso actual",
                "• La sesión quedará marcada como incompleta",
                "• La sesion respiración se detendrá automáticamente"
            ),
        )
    }
}

@Composable
fun CiruloProgreso(progress: Float, currentPhaseData: BreathingPhase) {
    Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f - 40.dp.toPx()
            val strokeWidth = 20.dp.toPx()
            val center = size.center

            drawCircle(
                color = Color(0xFFE0E0E0),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )


            val sweepAngle = 360f * progress.coerceIn(0f, 1f)
            drawArc(
                color = currentPhaseData.color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

        }
        Card(
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = currentPhaseData.color.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    currentPhaseData.name,
                    fontSize = 24.sp,
                    color = currentPhaseData.color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TiempoRestanteCard(remainingTimeMs: Long, estado: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tiempo Restante", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(formatTimeMs(remainingTimeMs), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(estado, fontSize = 12.sp, fontWeight = FontWeight.Light)
        }
    }
}

data class BreathingPhase(
    val name: String,
    val duration: Long,
    val color: Color
)