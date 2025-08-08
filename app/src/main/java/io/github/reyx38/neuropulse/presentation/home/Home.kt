package io.github.reyx38.neuropulse.presentation.home

import io.github.reyx38.neuropulse.R
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.reyx38.neuropulse.presentation.UiCommon.NeuroDrawerScaffold
import io.github.reyx38.neuropulse.presentation.UiCommon.getFrase
import io.github.reyx38.neuropulse.presentation.progresionSemanal.ProgresionSemanalViewModel
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioViewModel
import io.github.reyx38.neuropulse.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navHostController: NavHostController,
    viewModel: UsuarioViewModel = hiltViewModel(),
    goToActividades: () -> Unit,
    goToReflexiones: () -> Unit,
    goToRespiracion: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isVisible by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.getUsuario()
        delay(100)
        isVisible = true
    }

    NeuroDrawerScaffold(
        navHostController = navHostController,
        uiImagen = uiState.usuario?.imagenPerfil,
        usuarioId = uiState.usuarioId ?: 0
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            primaryContainerLight.copy(alpha = 0.95f),
                            surfaceContainerLight,
                            surfaceBrightLight
                        )
                    )
                )
        ) {

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(800, easing = EaseOutCubic)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GreetingCard(nombre = uiState.usuario?.nombreUsuario)

                    Text(
                        text = "Actividades disponible",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = onPrimaryContainerLight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp)
                    )

                    ActivityCard(
                        icon = R.drawable.brain,
                        title = "Ejercicios cognitivos",
                        progressColor = tertiaryContainerLight,
                        goActivity = goToActividades
                    )

                    ActivityCard(
                        icon = R.drawable.img_1,
                        title = "Ejercicios de Respiración",
                        progressColor = secondaryContainerLight,
                        goActivity = goToRespiracion
                    )

                    ActivityCard(
                        icon = R.drawable.img,
                        title = "Reflexiónes Escritas",
                        progressColor = primaryLight,
                        goActivity = goToReflexiones
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun GreetingCard(nombre: String?) {
    val frase = remember { getFrase() }
    var animateIcon by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animateIcon = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryContainerLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val scale by animateFloatAsState(
                targetValue = if (animateIcon) 1f else 0.3f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryLight.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.brain),
                    contentDescription = "Brain icon",
                    modifier = Modifier.size(56.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "¡Hola, $nombre! 👋",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = onPrimaryContainerLight,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "¿Listo para entrenar tu mente hoy?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = onPrimaryContainerLight.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = surfaceContainerHighLight
                ) {
                    Text(
                        text = "💭 \"$frase\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        ),
                        color = onSurfaceLight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(
    icon: Int,
    title: String,
    progressColor: Color,
    goActivity: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                goActivity()
            }
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceContainerLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                progressColor.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = title,
                    modifier = Modifier.size(36.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = onSurfaceLight
                )
            }
        }

        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(150)
                isPressed = false
            }
        }
    }
}
