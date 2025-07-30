package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.OpcionesUsuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritasFrasesScreen(onBack: () -> Unit) {
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
                                    text = "Freses Favoritas",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { onBack() },
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
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Aquí aparecerán tus elementos favoritos.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}