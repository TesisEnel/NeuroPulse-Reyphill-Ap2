package io.github.reyx38.neuropulse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.reyx38.neuropulse.presentation.ActividadesDiarias.ActividadesDiariasScreen
import io.github.reyx38.neuropulse.presentation.experiencia.ReflexionScreen
import io.github.reyx38.neuropulse.presentation.reflexiones.ReflexionBodyScreen
import io.github.reyx38.neuropulse.presentation.home.Home
import io.github.reyx38.neuropulse.presentation.reflexiones.ReflexionListScreen
import io.github.reyx38.neuropulse.presentation.usuarios.auth.login.LoginScreen
import io.github.reyx38.neuropulse.presentation.usuarios.auth.register.RegistarScreen
import io.github.reyx38.neuropulse.presentation.usuarios.perifilUsuarios.ProfileScreen


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
                }
            )

        }
        composable<Screen.HomeActivities> {
            ActividadesDiariasScreen()
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
                }
            )
        }

    }
}

