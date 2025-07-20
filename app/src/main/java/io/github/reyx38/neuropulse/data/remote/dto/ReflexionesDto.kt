package io.github.reyx38.neuropulse.data.remote.dto

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class ReflexionDto(
    val reflexionId: Int,
    val usuarioId: Int,
    val estadoReflexion: String,
    val descripcion: String,
    val fechaCreacion: String? = null
)
fun String?.toDateOrNull(): Date? {
    return try {
        if (this == null || this == "0001-01-01T00:00:00") null
        else SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(this)
    } catch (e: Exception) {
        null
    }
}