package io.github.reyx38.neuropulse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.reyx38.neuropulse.presentation.Home.Home


@Composable
fun NeuroPulseNavHost (
    navHostController: NavHostController,
){
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home>() {
            Home()
        }
    }

}

