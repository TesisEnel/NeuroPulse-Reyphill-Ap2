package io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LineAxis
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.reyx38.neuropulse.data.local.entities.RespiracionEntity
import io.github.reyx38.neuropulse.data.local.entities.RespiracionWithInformacion
import io.github.reyx38.neuropulse.presentation.Respiracion.SesionRespiracion.SesionScreen
import io.github.reyx38.neuropulse.ui.theme.NeuroPulseTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSessionRespiracion(
    viewModel: RespiracionViewModel = hiltViewModel(),
    goBack: () -> Unit,
    goToSesion: () -> Unit
) {
    var selectedRespiracionId by remember { mutableStateOf<Int?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                                    text = "Tecnicas de respiracion",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { goBack() },
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
    ){
        Column(
            modifier = Modifier.padding(it)
        ) {
            BreathingPatternGridMenu(
                uiState,
                goToSesion = { id -> selectedRespiracionId = id }
            )

            selectedRespiracionId?.let { id ->
                SesionScreen(
                    viewModel = viewModel,
                    idrespiracion = id,
                    onDismiss = { selectedRespiracionId = null },
                    onStartSession = goToSesion,

                )
            }
        }
    }
}

@Composable
fun BreathingPatternGridMenu(
    uiState: RespiracionUiState,
    goToSesion: (Int) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedHeaderText(isVisible = isVisible)

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(uiState.respiraciones) { index, pattern ->
                AnimatedRespiracionGridCard(
                    respiracion = pattern,
                    index = index,
                    isVisible = isVisible,
                    goToSesion = goToSesion
                )
            }
        }
    }
}

@Composable
fun AnimatedHeaderText(isVisible: Boolean) {
    val animationSpec = tween<Float>(
        durationMillis = 800,
        easing = FastOutSlowInEasing
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = animationSpec,
        label = "header_alpha"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -30f,
        animationSpec = animationSpec,
        label = "header_offset"
    )

    Text(
        text = "Selecciona la técnica que mejor se adapte a tu momento",
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = offsetY
            }
    )
}

@Composable
fun AnimatedRespiracionGridCard(
    respiracion: RespiracionWithInformacion,
    index: Int,
    isVisible: Boolean,
    goToSesion: (Int) -> Unit
) {
    // Delay escalonado basado en el índice
    val delayMillis = index * 150

    val animationSpec = tween<Float>(
        durationMillis = 600,
        delayMillis = delayMillis,
        easing = FastOutSlowInEasing
    )

    val scaleAnimationSpec = tween<Float>(
        durationMillis = 700,
        delayMillis = delayMillis,
        easing = overshootInterpolator(1.2f).toEasing()
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = animationSpec,
        label = "card_alpha_$index"
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = scaleAnimationSpec,
        label = "card_scale_$index"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = animationSpec,
        label = "card_offset_$index"
    )

    RespiracionGridCard(
        respiracion = respiracion,
        goToSesion = goToSesion,
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
                translationY = offsetY
            }
    )
}

// Función auxiliar para ícono y colores
data class RespiracionVisual(
    val icon: ImageVector,
    val color: Color,
    val gradientColors: List<Color>
)

@Composable
fun getVisualForRespiracion(nombre: String?): RespiracionVisual {
    val scheme = MaterialTheme.colorScheme
    return when (nombre?.lowercase()) {
        "respiración 4-4" -> RespiracionVisual(
            Icons.Filled.SelfImprovement,
            scheme.primary,
            listOf(scheme.primaryContainer, scheme.surfaceContainerHigh)
        )

        "respiración 4-7-8" -> RespiracionVisual(
            Icons.Filled.Spa,
            scheme.tertiary,
            listOf(scheme.tertiaryContainer, scheme.surfaceContainerHigh)
        )

        "respiración 5-5" -> RespiracionVisual(
            Icons.Filled.FitnessCenter,
            scheme.secondary,
            listOf(scheme.secondaryContainer, scheme.surfaceContainerHigh)
        )

        "respiración 6-2-8" -> RespiracionVisual(
            Icons.Filled.LineAxis,
            scheme.secondary,
            listOf(scheme.secondaryContainer, scheme.surfaceContainerHigh)
        )

        else -> RespiracionVisual(
            Icons.Filled.SelfImprovement,
            scheme.outline,
            listOf(scheme.surfaceVariant, scheme.surface)
        )
    }
}

@Composable
fun RespiracionGridCard(
    respiracion: RespiracionWithInformacion,
    goToSesion: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val visual = getVisualForRespiracion(respiracion.respiracion.nombre)

    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "press_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(pressScale)
            .clickable {
                isPressed = true
                goToSesion(respiracion.respiracion.idRespiracion)
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = visual.gradientColors.map { it.copy(alpha = 0.2f) }
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animación del ícono
                AnimatedIcon(visual = visual)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = respiracion.respiracion.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Resetear estado de presión
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
fun AnimatedIcon(visual: RespiracionVisual) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon_animation")

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = visual.gradientColors.map { it.copy(alpha = 0.25f) }
                )
            )
            .scale(iconScale),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.color,
            modifier = Modifier.size(32.dp)
        )
    }
}

fun overshootInterpolator(tension: Float = 2f): android.view.animation.Interpolator {
    return android.view.animation.OvershootInterpolator(tension)
}

fun android.view.animation.Interpolator.toEasing(): Easing {
    return Easing { fraction -> this.getInterpolation(fraction) }
}

@Preview(showBackground = true)
@Composable
fun PreviewBreathingPatternGridMenu() {
    // Datos de prueba
    val sampleRespiraciones = listOf(
        RespiracionWithInformacion(
            respiracion = RespiracionEntity(
                idRespiracion = 1,
                nombre = "Respiración 4-7-8",
                descripcion = "Relajación profunda",
                inhalarSegundos = 4,
                mantenerSegundos = 7,
                exhalarSegundos = 8
            ),
            informacionRespiracion = emptyList()
        ),
        RespiracionWithInformacion(
            respiracion = RespiracionEntity(
                idRespiracion = 2,
                nombre = "Respiración profunda",
                descripcion = "Calma y concentración",
                inhalarSegundos = 5,
                mantenerSegundos = 4,
                exhalarSegundos = 5
            ),
            informacionRespiracion = emptyList()
        ),
        RespiracionWithInformacion(
            respiracion = RespiracionEntity(
                idRespiracion = 3,
                nombre = "Respiración cuadrada",
                descripcion = "Mejora el enfoque",
                inhalarSegundos = 4,
                mantenerSegundos = 4,
                exhalarSegundos = 4
            ),
            informacionRespiracion = emptyList()
        ),
        RespiracionWithInformacion(
            respiracion = RespiracionEntity(
                idRespiracion = 4,
                nombre = "Respiración 4-4",
                descripcion = "Equilibrio y focus",
                inhalarSegundos = 4,
                mantenerSegundos = 0,
                exhalarSegundos = 4
            ),
            informacionRespiracion = emptyList()
        )
    )

    NeuroPulseTheme {
        BreathingPatternGridMenu(
            uiState = RespiracionUiState(respiraciones = sampleRespiraciones),
            {}
        )
    }
}