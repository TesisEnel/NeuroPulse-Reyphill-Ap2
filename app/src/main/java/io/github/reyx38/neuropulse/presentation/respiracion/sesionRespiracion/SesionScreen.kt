package io.github.reyx38.neuropulse.presentation.respiracion.sesionRespiracion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.RespiracionUiEvent
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.RespiracionViewModel
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.getVisualForRespiracion

@Composable
fun SesionScreen(
    idrespiracion: Int,
    viewModel: RespiracionViewModel,
    onDismiss: () -> Unit = {},
    onStartSession: () -> Unit = {},
) {
    var showHelpDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(idrespiracion) {
        viewModel.buscarRespiracion(idrespiracion)
        viewModel.onEvent(RespiracionUiEvent.RespiracionChange(idrespiracion))
    }

    if (uiState.respiracion != null) {
        RespiracionSessionDialog(
            pattern = uiState.respiracion!!,
            selectedMinutes = uiState.duracionMinutos,
            onMinutesChanged = { viewModel.onEvent(RespiracionUiEvent.DuracionMinutos(it)) },
            onDismiss = onDismiss,
            onStartSession = onStartSession,
            onShowHelp = { showHelpDialog = true }
        )
    }

    if (showHelpDialog) {
        BreathingHelpDialog(
            pattern = uiState.respiracion,
            onDismiss = { showHelpDialog = false }
        )
    }
}

@Composable
fun RespiracionSessionDialog(
    pattern: RespiracionWithInformacion,
    selectedMinutes: Int,
    onMinutesChanged: (Int) -> Unit,
    onDismiss: () -> Unit,
    onStartSession: () -> Unit,
    onShowHelp: () -> Unit
) {
    val durationOptions = listOf(1, 3, 5, 10, 15, 20, 30)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        ) {

                            IconButton(
                                onClick = onShowHelp,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Help,
                                    contentDescription = "Ayuda",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = pattern.respiracion.nombre,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = pattern.respiracion.descripcion,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PatternStep("Inhalar", "${pattern.respiracion.inhalarSegundos}s")
                            if (pattern.respiracion.mantenerSegundos > 0) {
                                PatternStep("Mantener", "${pattern.respiracion.mantenerSegundos}s")
                            }
                            PatternStep("Exhalar", "${pattern.respiracion.exhalarSegundos}s")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Duración de la sesión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(durationOptions) { minutes ->
                            DurationChip(
                                minutes = minutes,
                                isSelected = minutes == selectedMinutes,
                                onClick = { onMinutesChanged(minutes) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val cycleTime = pattern.respiracion.inhalarSegundos +
                            pattern.respiracion.mantenerSegundos +
                            pattern.respiracion.exhalarSegundos
                    val estimatedCycles = (selectedMinutes * 60) / cycleTime

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sesión de $selectedMinutes ${if (selectedMinutes == 1) "minuto" else "minutos"}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "≈ $estimatedCycles ciclos de respiración",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onStartSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Iniciar Sesión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatternStep(label: String, duration: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            duration,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DurationChip(
    minutes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(200),
        label = "chipScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected)
                    Brush.horizontalGradient(MaterialTheme.colorScheme.primaryContainer.let {
                        listOf(it, it.copy(alpha = 0.8f))
                    })
                else MaterialTheme.colorScheme.surfaceVariant.let {
                    Brush.horizontalGradient(listOf(it, it))
                }
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${minutes}m",
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BreathingHelpDialog(
    pattern: RespiracionWithInformacion?,
    onDismiss: () -> Unit
) {
    val visual = getVisualForRespiracion(pattern?.respiracion!!.nombre)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = visual.color)
            ) {
                Text("Entendido")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(visual.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        visual.icon,
                        contentDescription = null,
                        tint = visual.color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(pattern.respiracion.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pattern.informacionRespiracion) { info ->
                    Column {

                        Text("${info.tipoInformacion}:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        info.descripcion.split(",").forEach { linea ->
                            Text(
                                text = linea.trim(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
