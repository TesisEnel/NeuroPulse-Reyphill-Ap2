package io.github.reyx38.neuropulse.presentation.UiCommon.TimerUtils

fun formatearFecha(fecha: String): String {
    return try {
        val partes = fecha.split("-")
        "${partes[2]}/${partes[1]}/${partes[0]}"
    } catch (e: Exception) {
        fecha
    }
}