package com.example.plinkocs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plinkocs.Destinations.Info
import com.example.plinkocs.Destinations.Menu
import com.example.plinkocs.Destinations.Play
import com.example.plinkocs.presentation.info.InfoScreen
import com.example.plinkocs.presentation.menu.screen.MenuScreen
import com.example.plinkocs.presentation.play.PlayScreen
import kotlinx.serialization.Serializable

@Composable
fun PlinkoApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Menu) {
        composable<Menu> { MenuScreen(navController = navController) }
        composable<Play> { PlayScreen(navController = navController) }
        composable<Info> { InfoScreen(navController = navController) }
    }

}

@Serializable
sealed class Destinations {

    @Serializable
    object Menu : Destinations()

    @Serializable
    object Play : Destinations()

    @Serializable
    object Info : Destinations()
}

