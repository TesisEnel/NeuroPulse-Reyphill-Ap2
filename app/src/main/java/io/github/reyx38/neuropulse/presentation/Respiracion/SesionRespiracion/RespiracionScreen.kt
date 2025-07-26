package io.github.reyx38.neuropulse.presentation.Respiracion.SesionRespiracion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.reyx38.neuropulse.data.local.enum.EstadosRespiracion
import io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion.RespiracionUiEvent
import io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion.RespiracionUiState
import io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion.RespiracionViewModel


@Composable
fun RespiracionScreen(
    viewModel: RespiracionViewModel,
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.respiracion?.let {
        BreathingCircle(
            pattern = uiState,
            onEvent = viewModel::onEvent,
            onBackPressed = goBack,
            viewModel = viewModel
        )
    }
}

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

    // Crear fases solo para las que tienen duración > 0
    val phases = buildList {
        if (pattern.respiracion!!.respiracion.inhalarSegundos > 0) {
            add(BreathingPhase("Inhala", pattern.respiracion.respiracion.inhalarSegundos * 1000L, MaterialTheme.colorScheme.tertiary))
        }
        if (pattern.respiracion.respiracion.mantenerSegundos > 0) {
            add(BreathingPhase("Mantén", pattern.respiracion.respiracion.mantenerSegundos * 1000L, MaterialTheme.colorScheme.secondary))
        }
        if (pattern.respiracion.respiracion.exhalarSegundos > 0) {
            add(BreathingPhase("Exhala", pattern.respiracion.respiracion.exhalarSegundos * 1000L, MaterialTheme.colorScheme.primary))
        }
    }

    // Encontrar la fase actual
    val currentPhaseData = phases.find { phase ->
        when (currentPhase) {
            EstadosRespiracion.INHALING -> phase.name == "Inhala"
            EstadosRespiracion.HOLDING -> phase.name == "Mantén"
            EstadosRespiracion.EXHALING -> phase.name == "Exhala"
        }
    } ?: phases.firstOrNull() ?: BreathingPhase("Detenido", 0L, Color.Gray)

    fun formatTimeMs(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Inicializar la sesión cuando se carga la composable
    LaunchedEffect(pattern.respiracion) {
        pattern.respiracion?.let {
            if (pattern.duracionMinutos > 0) {
                onEvent(RespiracionUiEvent.DuracionMinutos(pattern.duracionMinutos))
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = pattern.respiracion?.respiracion?.nombre ?: " ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(48.dp))
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tiempo Restante",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTimeMs(remainingTimeMs),
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = pattern.estado ?: "Detenido",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Light
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val radius = size.minDimension / 2f - 40.dp.toPx()
                val strokeWidth = 20.dp.toPx()
                val center = size.center

                // Círculo de fondo
                drawCircle(
                    color = Color(0xFFE0E0E0),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Arco de progreso
                if (progress > 0f) {
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
            }

            Card(
                modifier = Modifier.size(200.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = currentPhaseData.color.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentPhaseData.name,
                        fontSize = 24.sp,
                        color = currentPhaseData.color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (phases.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    phases.forEach { phase ->
                        val phaseState = when (phase.name) {
                            "Inhala" -> EstadosRespiracion.INHALING
                            "Mantén" -> EstadosRespiracion.HOLDING
                            else -> EstadosRespiracion.EXHALING
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (currentPhase == phaseState) phase.color
                                        else Color.Gray.copy(alpha = 0.3f)
                                    )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = phase.name,
                                fontSize = 12.sp,
                                color = if (currentPhase == phaseState) phase.color
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = if (currentPhase == phaseState) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FloatingActionButton(
                onClick = { viewModel.togglePlayPause() },
                containerColor = if (isRunning) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pausar" else "Iniciar"
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

data class BreathingPhase(
    val name: String,
    val duration: Long,
    val color: Color
)