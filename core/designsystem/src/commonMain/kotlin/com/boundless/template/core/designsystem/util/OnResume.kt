package com.boundless.template.core.designsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Fires [onResume] whenever this screen becomes the active destination again — both a fresh
 * navigation and a bottom-tab switch restoring a saved back-stack entry (Navigation-Compose sets
 * [LocalLifecycleOwner] to the current `NavBackStackEntry`, whose own lifecycle reaches `RESUMED`
 * in both cases; this works the same on iOS since it's a Compose-Multiplatform concept, not
 * Android-specific).
 *
 * Note: adding a lifecycle observer to an already-resumed lifecycle replays `ON_RESUME`
 * immediately, so [onResume] also fires once on a screen's first appearance — callers that
 * already load in `init` need their own guard (e.g. a "loaded within the last N seconds" check)
 * to avoid a redundant fetch right at startup.
 */
@Composable
fun OnResume(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnResume by rememberUpdatedState(onResume)
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) latestOnResume()
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
