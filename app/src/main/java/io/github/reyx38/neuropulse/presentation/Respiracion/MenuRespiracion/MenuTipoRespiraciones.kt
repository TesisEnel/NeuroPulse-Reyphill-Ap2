package io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LineAxis
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSessionRespiracion(
    viewModel: RespiracionViewModel = hiltViewModel(),
    goBack: () -> Unit,

) {
    var selectedRespiracionId by remember { mutableStateOf<Int?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Técnicas de Respiración",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            BreathingPatternGridMenu(
                uiState,
                goToSesion = { id -> selectedRespiracionId = id }

            )

            selectedRespiracionId?.let { id ->
                SesionScreen(
                    idrespiracion = id,
                    onDismiss = { selectedRespiracionId = null },
                    onStartSession = { minutos ->
                        selectedRespiracionId = null
                    },
                    onShowHelp = { /* Aquí manejas ayuda si quieres */ }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Selecciona la técnica que mejor se adapte a tu momento",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.respiraciones) { pattern ->
                RespiracionGridCard(pattern, onClick = {},goToSesion)
            }
        }
    }
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
    onClick: () -> Unit,
    goToSesion: (Int) -> Unit
) {
    val visual = getVisualForRespiracion(respiracion.respiracion.nombre)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { goToSesion(respiracion.respiracion.idRespiracion)
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
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = visual.gradientColors.map { it.copy(alpha = 0.25f) }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visual.icon,
                        contentDescription = null,
                        tint = visual.color,
                        modifier = Modifier.size(32.dp)
                    )
                }

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
        )
    )

    NeuroPulseTheme {
        BreathingPatternGridMenu(
            uiState = RespiracionUiState(respiraciones = sampleRespiraciones),
            {}
        )
    }

}