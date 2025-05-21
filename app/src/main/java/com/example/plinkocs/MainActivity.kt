package com.example.plinkocs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Ви вже використовуєте це
import androidx.core.view.WindowInsetsCompat // Потрібен для WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat // Потрібен для контролера
import com.example.plinkocs.ui.theme.PlinkoCSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)

        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            PlinkoCSTheme {
                PlinkoApp()
            }
        }
    }
}