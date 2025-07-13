package io.github.reyx38.neuropulse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.reyx38.neuropulse.presentation.ActividadesDiarias.ActividadesDiariasScreen
import io.github.reyx38.neuropulse.presentation.home.Home
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
                goToMenu =   {
                    navHostController.navigate(Screen.Home)
                },
                goToLogout = {
                    navHostController.navigate(Screen.Login) {
                        popUpTo(Screen.UsuarioOptiones) { inclusive = true }
                    }
                }

            )
        }

    }
}

