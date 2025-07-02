package io.github.reyx38.neuropulse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.reyx38.neuropulse.presentation.ActividadesDiarias.ActividadesDiariasScreen
import io.github.reyx38.neuropulse.presentation.Home.Home
import io.github.reyx38.neuropulse.presentation.auth.register.RegisterScreen
import io.github.reyx38.neuropulse.presentation.auth.login.LoginScreen


@Composable
fun NeuroPulseNavHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Login
    ) {
        composable<Screen.Home>() {
            Home(
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
                    navHostController.navigate(Screen.Home)

                },
                goToRegister = {
                    navHostController.navigate(Screen.Register)
                }

            )
        }
        composable<Screen.Register> {
            RegisterScreen(

            )
        }
    }

}

