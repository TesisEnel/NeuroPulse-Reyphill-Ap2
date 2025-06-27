package io.github.reyx38.neuropulse.presentation.Home

import io.github.reyx38.neuropulse.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.reyx38.neuropulse.presentation.UiCommon.NeuroDrawerScaffold
import io.github.reyx38.neuropulse.presentation.UiCommon.getFrase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    NeuroDrawerScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GreetingCard()
            ActivityCards()
        }
    }
}

@Composable
private fun GreetingCard() {
    val frase = remember { getFrase() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.brain),
            contentDescription = "Brain icon",
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = "¡Buenas Reyphill!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "¿Listo para tu entrenamiento Diario?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = frase,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
private fun ActivityCards() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ActivityCard(
            icon = R.drawable.brain,
            title = "Actividades Diarias",
            subtitle = "3/4 completadas",
            progress = 0.8f,
            progressColor = MaterialTheme.colorScheme.primary
        )

        ActivityCard(
            icon = R.drawable.img_1,
            title = "Ejercicio de respiración",
            subtitle = "Incompleto",
            progress = 0.5f,
            progressColor = MaterialTheme.colorScheme.secondary
        )

        ActivityCard(
            icon = R.drawable.img,
            title = "Reflexión Escrita",
            subtitle = "2 notas escritas",
            progress = null,
            progressColor = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun ActivityCard(
    icon: Int,
    title: String,
    subtitle: String,
    progress: Float?,
    progressColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = title,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                progress?.let {
                    LinearProgressIndicator(
                        progress = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}
