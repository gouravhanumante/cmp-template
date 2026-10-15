package com.boundless.template.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.boundless.template.feature.home.presentation.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

/** The only place that knows every feature; features never navigate to each other directly. */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = appEnterTransition,
        exitTransition = appExitTransition,
        popEnterTransition = appPopEnterTransition,
        popExitTransition = appPopExitTransition,
    ) {
        composable<HomeRoute> { HomeScreen() }
    }
}
