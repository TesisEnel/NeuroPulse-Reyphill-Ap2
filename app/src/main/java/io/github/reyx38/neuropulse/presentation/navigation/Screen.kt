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
    @Serializable
    data class ReflexionScreen(val reflexionId: Int): Screen()
    @Serializable
    data object ReflexionListScreen: Screen()
    @Serializable
    data object MenuRespiraciones: Screen()
    @Serializable
    data object RespiracionScreen: Screen()

}