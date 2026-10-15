package com.boundless.template.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry

/**
 * Navigation-Compose's own Android default is a flat 700ms crossfade with no platform-specific
 * override — unlike iOS, which the library already gives a native 200ms push/pop slide. Each
 * platform wires its own feel here instead of falling through to that mismatched default; the iOS
 * actual reproduces the library's existing behavior exactly (same values, same direction
 * cascade — see its kdoc), so iOS is unaffected by this.
 */
expect val appEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition

expect val appExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition

expect val appPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition

expect val appPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition
