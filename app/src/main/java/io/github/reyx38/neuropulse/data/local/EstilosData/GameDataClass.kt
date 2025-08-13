package io.github.reyx38.neuropulse.data.local.EstilosData

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

 data class GameCardConfig(
    val backgroundColor: Color,
    val contentColor: Color,
    val elevation: Dp = 2.dp,
    val shape: RoundedCornerShape = RoundedCornerShape(16.dp)
)

 data class GameIconButtonConfig(
    val size: Dp = 36.dp,
    val iconSize: Dp = 18.dp,
    val backgroundColor: Color,
    val contentColor: Color
)
