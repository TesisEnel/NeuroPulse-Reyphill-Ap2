package io.github.reyx38.neuropulse.presentation.uiCommon.usuarioUtils

import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap


fun base64ToString(base64String: String): ImageBitmap? {
    return try {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}


fun isValidImageString(imageString: String?): Boolean {
    return !imageString.isNullOrEmpty() && imageString.isNotBlank()
}

@Composable
fun recordarMapaDeBit(base64String: String): ImageBitmap? {
    return remember(base64String) {
        base64ToString(base64String)
    }
}

@Composable
fun esValidaImagen(imageString: String?): Boolean {
    return remember(imageString) {
        isValidImageString(imageString)
    }
}
