package com.boundless.template.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.boundless.template.core.common.AppBuildInfo
import com.boundless.template.feature.devtools.presentation.DevToolsScreen
import com.boundless.template.feature.devtools.presentation.checks.ChecksScreen
import com.boundless.template.feature.devtools.presentation.components.ComponentsScreen
import com.boundless.template.feature.devtools.presentation.database.DatabaseScreen
import com.boundless.template.feature.home.presentation.HomeScreen
import com.boundless.template.app.SettingsScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data object HomeRoute

@Serializable
data object SettingsRoute

@Serializable
data object DevToolsRoute

@Serializable
data object DevDatabaseRoute

@Serializable
data object DevComponentsRoute

@Serializable
data object DevChecksRoute

/** The only place that knows every feature; features never navigate to each other directly. */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    buildInfo: AppBuildInfo = koinInject(),
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
        composable<SettingsRoute> { SettingsScreen() }
        // Release builds don't register these, so no link or back stack can reach them.
        if (buildInfo.isDebug) {
            composable<DevToolsRoute> {
                DevToolsScreen(
                    onBack = navController::popBackStack,
                    onOpenDatabase = { navController.navigate(DevDatabaseRoute) },
                    onOpenComponents = { navController.navigate(DevComponentsRoute) },
                    onOpenChecks = { navController.navigate(DevChecksRoute) },
                )
            }
            composable<DevDatabaseRoute> { DatabaseScreen(onBack = navController::popBackStack) }
            composable<DevComponentsRoute> { ComponentsScreen(onBack = navController::popBackStack) }
            composable<DevChecksRoute> { ChecksScreen(onBack = navController::popBackStack) }
        }
    }
}
