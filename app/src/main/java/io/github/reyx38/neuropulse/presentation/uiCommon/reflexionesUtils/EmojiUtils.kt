package io.github.reyx38.neuropulse.presentation.uiCommon.reflexionesUtils

fun getEmojiByEstado(estado: String): String {
    return when (estado.lowercase()) {
        "feliz" -> "😊"
        "triste" -> "😢"
        "enojado" -> "😠"
        "normal" -> "😐"
        else -> "😐"
    }
}