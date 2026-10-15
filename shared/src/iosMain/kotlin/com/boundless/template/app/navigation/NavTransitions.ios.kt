package com.boundless.template.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

// Same 200ms/linear slide, same 0.3 parallax factor as Navigation-Compose's own iOS default.
private const val NAV_DURATION_MS = 200

actual val appEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(NAV_DURATION_MS, easing = LinearEasing),
    )
}

actual val appExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(NAV_DURATION_MS, easing = LinearEasing),
        targetOffset = { fullOffset -> (fullOffset * 0.3f).toInt() },
    )
}

actual val appPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(NAV_DURATION_MS, easing = LinearEasing),
        initialOffset = { fullOffset -> (fullOffset * 0.3f).toInt() },
    )
}

actual val appPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(NAV_DURATION_MS, easing = LinearEasing),
    )
}
