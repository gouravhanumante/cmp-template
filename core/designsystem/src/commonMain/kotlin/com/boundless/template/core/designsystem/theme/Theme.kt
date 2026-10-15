package com.boundless.template.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/** Lets components reach palette extras that Material's ColorScheme has no slot for. */
val LocalAppPalette = staticCompositionLocalOf { AppPalette.Default }

@Composable
fun AppTheme(
    palette: AppPalette = AppPalette.Default,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppPalette provides palette) {
        MaterialTheme(
            colorScheme = palette.colorScheme(),
            typography = appTypography(),
            shapes = AppShapes,
            content = content,
        )
    }
}
