package io.github.reyx38.neuropulse.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.tuapp.ui.screens.EjerciciosCognitivosScreen
import io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion.MenuSessionRespiracion
import io.github.reyx38.neuropulse.presentation.Respiracion.MenuRespiracion.RespiracionViewModel
import io.github.reyx38.neuropulse.presentation.Respiracion.SesionRespiracion.RespiracionScreen
import io.github.reyx38.neuropulse.presentation.Respiracion.SesionRespiracion.SesionesRespiracionScreen
import io.github.reyx38.neuropulse.presentation.experiencia.ReflexionScreen
import io.github.reyx38.neuropulse.presentation.home.Home
import io.github.reyx38.neuropulse.presentation.reflexiones.ReflexionListScreen
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
            EjerciciosCognitivosScreen()
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

    }
}

