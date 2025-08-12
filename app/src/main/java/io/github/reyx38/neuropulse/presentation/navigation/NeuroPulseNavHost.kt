package io.github.reyx38.neuropulse.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.MenuSessionRespiracion
import io.github.reyx38.neuropulse.presentation.respiracion.menuRespiracion.RespiracionViewModel
import io.github.reyx38.neuropulse.presentation.respiracion.sesionRespiracion.RespiracionScreen
import io.github.reyx38.neuropulse.presentation.respiracion.sesionRespiracion.SesionesRespiracionScreen
import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.catalogoEjercicios.EjerciciosCognitivosScreen
import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.conflictoColores.ConflictoColoresScreen
import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.desvanecimientoGame.DesvanecimientoScreen
import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.historialEjercicios.HistorialEjerciciosScreen
import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.logicaSombra.LogicaSombraScreen
import io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos.secuenciaMental.SecuenciaMentalScreen
import io.github.reyx38.neuropulse.presentation.home.Home
import io.github.reyx38.neuropulse.presentation.progresionSemanal.ProgresionSemanalScreen
import io.github.reyx38.neuropulse.presentation.reflexiones.ReflexionListScreen
import io.github.reyx38.neuropulse.presentation.reflexiones.ReflexionScreen
import io.github.reyx38.neuropulse.presentation.usuarios.auth.login.LoginScreen
import io.github.reyx38.neuropulse.presentation.usuarios.auth.register.RegistarScreen
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.ProfileScreen


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun NeuroPulseNavHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Login
    ) {
        composable<Screen.Home> {
            Home(
                navHostController = navHostController,
                goToActividades = {
                    navHostController.navigate(Screen.HomeActivities)
                },
                goToReflexiones = {
                    navHostController.navigate(Screen.ReflexionListScreen)
                },
                goToRespiracion = {
                    navHostController.navigate(Screen.MenuRespiraciones)
                }
            )
        }
        composable<Screen.HomeActivities> {
            EjerciciosCognitivosScreen(
                onNavigateBack = {
                    navHostController.navigate(Screen.Home)
                },

                onIniciarActividad = { actividad ->
                    when (actividad) {
                        1 -> {
                            navHostController.navigate(Screen.Desvanecimiento(1))
                        }

                        2 -> {
                            navHostController.navigate(Screen.SecuenciaMental(2))
                        }

                        3 -> {
                            navHostController.navigate(Screen.ConflictoColores(3))
                        }

                        4 -> {
                            navHostController.navigate(Screen.LogicaSombra(4))
                        }

                        else -> {
                            navHostController.navigate(Screen.Home)
                        }
                    }
                }

            )
        }
        composable<Screen.Login> {

            LoginScreen(
                goToHome = {
                    navHostController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },

                goToRegister = {
                    navHostController.navigate(Screen.Register)
                }
            )
        }
        composable<Screen.Register> {
            RegistarScreen(
                goToHome = {
                    navHostController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },

                goToLogin = {
                    navHostController.navigate(Screen.Login)
                }
            )
        }
        composable<Screen.UsuarioOptiones> {
            ProfileScreen(
                goToMenu = {
                    navHostController.navigate(Screen.Home)
                },
                goToLogout = {
                    navHostController.navigate(Screen.Login) {
                        popUpTo(Screen.UsuarioOptiones) { inclusive = true }
                    }
                }

            )
        }
        composable<Screen.ReflexionScreen> {
            val reflexionId = it.toRoute<Screen.ReflexionScreen>().reflexionId

            ReflexionScreen(
                goToBack = {
                    navHostController.navigateUp()
                },
                reflexionId = reflexionId
            )
        }
        composable<Screen.ReflexionListScreen> {
            ReflexionListScreen(
                goToCreate = {
                    navHostController.navigate(Screen.ReflexionScreen(0))
                },
                goBack = {
                    navHostController.navigateUp()
                },
                onEdit = {
                    navHostController.navigate(Screen.ReflexionScreen(it))
                }
            )
        }
        composable<Screen.MenuRespiraciones> {
            MenuSessionRespiracion(
                goBack = {
                    navHostController.navigate(Screen.Home)
                },
                goToSesion = {
                    navHostController.navigate(Screen.RespiracionScreen)
                }
            )
        }
        composable<Screen.RespiracionScreen> {
            val parentEntry = remember {
                navHostController.getBackStackEntry(Screen.MenuRespiraciones)
            }

            val viewModel: RespiracionViewModel = hiltViewModel(parentEntry)
            RespiracionScreen(
                viewModel = viewModel,
                goBack = {
                    navHostController.navigate(Screen.Home)
                }
            )
        }
        composable<Screen.Sesiones> {
            SesionesRespiracionScreen(
                onBack = {
                    navHostController.navigateUp()
                }

            )
        }
        composable<Screen.Desvanecimiento> {
            val ejercicioCognitivoId = it.toRoute<Screen.Desvanecimiento>().ejercicioCognitivoId

            DesvanecimientoScreen(
                onNavigateBack = {
                    navHostController.navigateUp()
                },
                ejercicioCognitivoId = ejercicioCognitivoId
            )
        }
        composable<Screen.SecuenciaMental> {
            val ejercicioCognitivoId = it.toRoute<Screen.SecuenciaMental>().ejercicioCognitivoId

            SecuenciaMentalScreen(
                onNavigateBack = {
                    navHostController.navigateUp()
                },
                ejercicioCognitivoId = ejercicioCognitivoId

            )
        }
        composable<Screen.LogicaSombra> {
            val ejercicioCognitivoId = it.toRoute<Screen.LogicaSombra>().ejercicioCognitivoId
            LogicaSombraScreen(
                onNavigateBack = {
                    navHostController.navigateUp()
                },
                ejercicioCognitivoId = ejercicioCognitivoId

            )
        }
        composable<Screen.ConflictoColores> {
            val ejercicioCognitivoId = it.toRoute<Screen.ConflictoColores>().ejercicioCognitivoId

            ConflictoColoresScreen(
                onNavigateBack = {
                    navHostController.navigateUp()
                },
                ejercicioCognitivoId = ejercicioCognitivoId

            )
        }
        composable<Screen.Ejercicios> {
            val usuarioId = it.toRoute<Screen.Ejercicios>().usuarioId
            HistorialEjerciciosScreen(
                usuarioId = usuarioId,
                onBack = {
                    navHostController.navigateUp()
                }
            )
        }
        composable<Screen.ProgresionSemanal> {
            val usuarioId = it.toRoute<Screen.ProgresionSemanal>().usuarioId
            ProgresionSemanalScreen(
                usuarioId = usuarioId,
                goBack = {
                    navHostController.navigateUp()
                }
            )
        }
    }
}

