@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.reyx38.neuropulse.presentation.experiencia

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.presentation.reflexiones.ReflexionesEvent
import io.github.reyx38.neuropulse.presentation.reflexiones.ReflexionesUiState
import io.github.reyx38.neuropulse.presentation.reflexiones.ReflexionesViewModel
import kotlin.math.round

@Composable
fun ReflexionScreen(
    viewmodel: ReflexionesViewModel = hiltViewModel(),
    reflexionId: Int? = 0,
    goToBack: () -> Unit
) {

    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(reflexionId) {
        reflexionId.let {
            if (it != 0){
                viewmodel.findReflexion(reflexionId)
            }
        }
    }

    ReflexionBodyScreen(
        uiState,
        viewmodel::onEvent,
        goToBack
    )

}


@Composable
fun ReflexionBodyScreen(
    uiState: ReflexionesUiState,
    onEvent: (ReflexionesEvent) -> Unit,
    goToBack: () -> Unit
) {

    var showNoteField by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        TopAppBar(
            title = { Text("Reflexión diaria") },
            navigationIcon = {
                IconButton(onClick = { goToBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Handle info */ }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Cuéntanos tus experiencias de hoy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = when (uiState.estadoReflexion) {
                    "triste" -> "😢"
                    "normal" -> "😐"
                    "feliz" -> "😊"
                    "enojado" -> "😠"
                    else -> "😊"
                },
                fontSize = 100.sp,
                textAlign = TextAlign.Center
            )

            if (!showNoteField) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = uiState.estadoReflexion?.uppercase() ?: "FELIZ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                CustomEmojiSlider(
                    value = uiState.estadoReflexion ?: "feliz",
                    onValueChange = { nuevoEstado ->
                        onEvent(ReflexionesEvent.estadoReflexion(nuevoEstado))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                TextButton(
                    onClick = { showNoteField = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Describe tu experiencia", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.height(40.dp))

                NoteTextField(
                    value = uiState.descripcion ?: "",
                    onValueChange = { onEvent(ReflexionesEvent.descripcionChange(it)) },
                    onDismiss = { showNoteField = false },
                    onSubmit = { onEvent(ReflexionesEvent.Save) },
                    error = uiState.error,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun NoteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        error?.let {
            Text(
                text = it,
                color = if (it.contains("correctamente", ignoreCase = true)) Color(0xFF4CAF50) else Color.Red,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tu reflexión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Visibility,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = "Cuéntanos cómo te sientes hoy, qué experiencias tuviste, qué te hizo sentir así...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de guardar mejorado
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp
            )
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Guardar",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Guardar reflexión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CustomEmojiSlider(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        "triste",
        "normal",
        "feliz",
        "enojado"
    )

    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier.padding(horizontal = 32.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                val trackY = size.height / 2
                val stepWidth = size.width / 3f

                drawLine(
                    color = colors.secondary.copy(alpha = 0.3f),
                    start = Offset(0f, trackY),
                    end = Offset(size.width, trackY),
                    strokeWidth = 4.dp.toPx()
                )

                for (i in 0..3) {
                    val x = i * stepWidth
                    val isSelected = steps.getOrNull(i) == value

                    drawCircle(
                        color = if (isSelected) colors.secondary
                        else colors.secondary.copy(alpha = 0.5f),
                        radius = if (isSelected) 12.dp.toPx() else 8.dp.toPx(),
                        center = Offset(x, trackY)
                    )

                    if (isSelected) {
                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = Offset(x, trackY)
                        )
                    }
                }
            }

            val index = steps.indexOf(value).coerceIn(0, 3)

            Slider(
                value = index.toFloat(),
                onValueChange = {
                    val newIndex = round(it).toInt().coerceIn(0, 3)
                    onValueChange(steps[newIndex])
                },
                valueRange = 0f..3f,
                steps = 2,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, step ->
                Text(
                    text = step.uppercase(),
                    fontSize = 12.sp,
                    color = if (steps[index] == value)
                        colors.primary
                    else
                        colors.onSurfaceVariant,
                    fontWeight = if (steps[index] == value)
                        FontWeight.Bold
                    else
                        FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1.5f)
                )
            }
        }
    }
}