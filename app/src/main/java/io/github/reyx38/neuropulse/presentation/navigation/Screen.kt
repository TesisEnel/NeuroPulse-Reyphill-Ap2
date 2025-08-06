package io.github.reyx38.neuropulse.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object HomeActivities : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object UsuarioOptiones : Screen()

    @Serializable
    data class ReflexionScreen(val reflexionId: Int?) : Screen()

    @Serializable
    data object ReflexionListScreen : Screen()

    @Serializable
    data object MenuRespiraciones : Screen()

    @Serializable
    data object RespiracionScreen : Screen()

    @Serializable
    data object Sesiones : Screen()

    @Serializable
    data class Ejercicios(val usuarioId: Int) : Screen()

    @Serializable
    data class Desvanecimiento(val ejercicioCognitivoId: Int) : Screen()

    @Serializable
    data class SecuenciaMental(val ejercicioCognitivoId: Int) : Screen()

    @Serializable
    data class ConflictoColores(val ejercicioCognitivoId: Int) : Screen()

    @Serializable
    data class LogicaSombra (val ejercicioCognitivoId: Int) : Screen()

    @Serializable
    data class ProgresionSemanal(val usuarioId: Int) : Screen()



}