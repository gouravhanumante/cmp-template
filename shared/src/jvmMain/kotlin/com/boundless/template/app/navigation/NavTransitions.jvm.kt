package com.boundless.template.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

// Material's shared-axis X pattern used by Google's own apps: a short slide plus a cross-fade,
// with the outgoing screen fading out before the incoming one fades in.
private const val NAV_DURATION_MS = 300
private const val OUTGOING_MS = 90
private const val SLIDE_FRACTION = 0.1f

private fun slideDistance(fullWidth: Int): Int = (fullWidth * SLIDE_FRACTION).toInt()

actual val appEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(tween(NAV_DURATION_MS, easing = FastOutSlowInEasing)) { slideDistance(it) } +
        fadeIn(tween(NAV_DURATION_MS - OUTGOING_MS, delayMillis = OUTGOING_MS, easing = LinearOutSlowInEasing))
}

actual val appExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(tween(NAV_DURATION_MS, easing = FastOutSlowInEasing)) { -slideDistance(it) } +
        fadeOut(tween(OUTGOING_MS, easing = FastOutLinearInEasing))
}

actual val appPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(tween(NAV_DURATION_MS, easing = FastOutSlowInEasing)) { -slideDistance(it) } +
        fadeIn(tween(NAV_DURATION_MS - OUTGOING_MS, delayMillis = OUTGOING_MS, easing = LinearOutSlowInEasing))
}

actual val appPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(tween(NAV_DURATION_MS, easing = FastOutSlowInEasing)) { slideDistance(it) } +
        fadeOut(tween(OUTGOING_MS, easing = FastOutLinearInEasing))
}
