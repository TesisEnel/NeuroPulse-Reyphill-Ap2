package io.github.reyx38.neuropulse.presentation.uiCommon.usuarioUtils
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.UsuarioEvent

class ManejarImange(
    private val context: Context,
    private val onEvent: (UsuarioEvent) -> Unit
) {
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun selecionarImagen(
        launcher: ActivityResultLauncher<String>,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        val permission = obtenerPermiso()

        when {
            tienePermiso(permission) -> launcher.launch("image/*")
            else -> requestPermissionLauncher.launch(permission)
        }
    }

    fun resultadoImagen(uri: Uri?) {
        uri?.let {
            selectedImageUri = it
            procesarUri(it)
        }
    }

    fun permisoImagenResultado(
        isGranted: Boolean,
        launcher: ActivityResultLauncher<String>
    ) {
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun procesarUri(uri: Uri) {
        val imageBytes = context.contentResolver.openInputStream(uri)?.readBytes()
        imageBytes?.let { bytes ->
            val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            onEvent(UsuarioEvent.ImagenChange(base64))
        }
    }

    private fun obtenerPermiso(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun tienePermiso(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}


@Composable
fun recordadImagen(
    context: Context,
    onEvent: (UsuarioEvent) -> Unit
): ManejarImange {
    return remember { ManejarImange(context, onEvent) }
}


@Composable
fun recondarImangeBuscador(
    imageHandler: ManejarImange
): Pair<
        ActivityResultLauncher<String>,
        ActivityResultLauncher<String>
        > {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = imageHandler::resultadoImagen
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        imageHandler.permisoImagenResultado(isGranted, launcher)
    }

    return Pair(launcher, requestPermissionLauncher)
}
