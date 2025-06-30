package io.github.reyx38.neuropulse.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home: Screen()
    @Serializable
    data object HomeActivities: Screen()
}