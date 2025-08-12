package io.github.reyx38.neuropulse.presentation.uiCommon.timerUtils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatTimeMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

