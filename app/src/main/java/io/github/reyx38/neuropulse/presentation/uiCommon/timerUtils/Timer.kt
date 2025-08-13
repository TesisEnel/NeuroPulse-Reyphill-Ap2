package io.github.reyx38.neuropulse.presentation.uiCommon.timerUtils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatTimeMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun minutesToMs(minutes: Int): Long = minutes * 60 * 1000L
fun msToMinutes(ms: Long): Int = (ms / 60000).toInt()

data class TimerState(
    val elapsedTimeMs: Long = 0L,
    val sessionStartTime: Long = 0L,
    val phaseStartTime: Long = 0L,
    val elapsedInCurrentPhase: Long = 0L
) {
    fun reset() = TimerState()

    fun initSession(currentTime: Long = System.currentTimeMillis()) = copy(
        sessionStartTime = currentTime,
        phaseStartTime = currentTime
    )

    fun resume(currentTime: Long = System.currentTimeMillis()) = copy(
        sessionStartTime = currentTime - elapsedTimeMs,
        phaseStartTime = currentTime - elapsedInCurrentPhase
    )

    fun updateElapsed(currentTime: Long = System.currentTimeMillis()) = copy(
        elapsedTimeMs = currentTime - sessionStartTime,
        elapsedInCurrentPhase = currentTime - phaseStartTime
    )

    fun resetPhase(currentTime: Long = System.currentTimeMillis()) = copy(
        phaseStartTime = currentTime,
        elapsedInCurrentPhase = 0L
    )
}

// ✅ Cálculos de tiempo
fun calculateProgress(elapsedMs: Long, totalMs: Long): Float =
    (elapsedMs.toFloat() / totalMs).coerceIn(0f, 1f)

fun calculateRemainingTime(elapsedMs: Long, totalMs: Long): Long =
    (totalMs - elapsedMs).coerceAtLeast(0L)

fun isTimeComplete(elapsedMs: Long, totalMs: Long): Boolean =
    elapsedMs >= totalMs

fun shouldAdvancePhase(elapsedInPhase: Long, phaseDuration: Long): Boolean =
    elapsedInPhase >= phaseDuration