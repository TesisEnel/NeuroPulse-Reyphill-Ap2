package io.github.reyx38.neuropulse.presentation.ActividadesDiarias

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.reyx38.neuropulse.R

data class MiniJuego(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val imagen: Int,
    val completado: Boolean
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActividadesDiariasScreen(
) {
    val juegos = listOf(
        MiniJuego(
            id = "desaparicion",
            titulo = "Desaparece la imagen",
            descripcion = "Pon a prueba tu memoria visual",
            imagen = R.drawable.img_3,
            completado = false
        ),
        MiniJuego(
            id = "secuencia_colores",
            titulo = "Secuencia de colores",
            descripcion = "Recuerda y repite la secuencia",
            imagen = R.drawable.img_4,
            completado = false
        ),
        MiniJuego(
            id = "ordenar_numeros",
            titulo = "Ordena los números",
            descripcion = "Ejercita tu lógica numérica",
            imagen = R.drawable.img_5,
            completado = true
        ),
        MiniJuego(
            id = "contar_pulsos",
            titulo = "Cuenta los pulsos",
            descripcion = "Presta atención y cuenta bien",
            imagen = R.drawable.img_6,
            completado = false
        )
    )

    juegos.forEach { juego ->
        MinijuegoCard(
            juego = juego,
            onClick = { }
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinijuegoCard(
    juego: MiniJuego,
    onClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Actividades Diarias",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regreso"
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ){
        Card(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .height(150.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(juego.imagen),
                    contentDescription = juego.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (juego.completado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (juego.completado) "Completado" else "No completado",
                            tint = if (juego.completado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = juego.titulo,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = juego.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }

}
