package io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.opcionesUsuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.reyx38.neuropulse.presentation.uiCommon.usuarioUtils.recordarMapaDeBit
import io.github.reyx38.neuropulse.presentation.uiCommon.usuarioUtils.esValidaImagen

@Composable
fun SeccionImagen(
    imagen: String?,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .fillMaxWidth()
    ) {
        ImagenCard(
            imagen = imagen,
            onImageClick = onImageClick
        )
        EditIconOverlay()
    }
}


@Composable
private fun ImagenCard(
    imagen: String?,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier.size(120.dp),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onImageClick)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            ImagenContenido(imagen = imagen)
        }
    }
}


@Composable
private fun ImagenContenido(imagen: String?) {
    val isValidImage = esValidaImagen(imagen)

    when {
        !isValidImage -> IconoPorDefecto()
        else -> PerfilImagen(imagen = imagen!!)
    }
}


@Composable
private fun IconoPorDefecto() {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Perfil por defecto",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(32.dp)
    )
}


@Composable
private fun PerfilImagen(imagen: String) {
    val bitmap = recordarMapaDeBit(imagen)

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Imagen de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
    } ?: IconoPorDefecto()
}


@Composable
private fun BoxScope.EditIconOverlay() {
    Card(
        modifier = Modifier
            .size(32.dp)
            .align(Alignment.BottomEnd),
        shape = CircleShape,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar imagen",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
