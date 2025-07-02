package io.github.reyx38.neuropulse.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home: Screen()
    @Serializable
    data object HomeActivities: Screen()
    @Serializable
    data object Login: Screen()
    @Serializable
    data object Register: Screen()
    @Serializable
    data object UsuarioOptiones: Screen()
}