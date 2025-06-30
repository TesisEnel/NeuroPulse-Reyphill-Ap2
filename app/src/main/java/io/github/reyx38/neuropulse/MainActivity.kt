package io.github.reyx38.neuropulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.reyx38.neuropulse.presentation.navigation.NeuroPulseNavHost
import io.github.reyx38.neuropulse.ui.theme.NeuroPulseTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeuroPulseTheme(
                dynamicColor = false
            ) {
                val nav = rememberNavController()
                NeuroPulseNavHost(nav)
            }
        }
    }
}

