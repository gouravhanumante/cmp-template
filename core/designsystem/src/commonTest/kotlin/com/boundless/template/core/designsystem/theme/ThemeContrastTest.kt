package com.boundless.template.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.test.Test
import kotlin.test.assertTrue

private fun contrast(
    a: Color,
    b: Color,
): Double {
    val (light, dark) = listOf(a.luminance(), b.luminance()).sortedDescending()
    return (light + 0.05) / (dark + 0.05)
}

// WCAG AA: normal-size text needs 4.5:1. A new theme must not ship buttons whose label can't be read.
class ThemeContrastTest {
    @Test
    fun whiteTextOnEveryThemesButtonsIsReadable() {
        AppPalette.entries.forEach { palette ->
            val scheme = palette.colorScheme()
            assertTrue(contrast(scheme.primary, scheme.onPrimary) >= 4.5, "$palette primary")
            assertTrue(contrast(scheme.secondary, scheme.onSecondary) >= 4.5, "$palette secondary")
        }
    }

    @Test
    fun bodyTextOnEveryThemesBackgroundIsReadable() {
        AppPalette.entries.forEach { palette ->
            val scheme = palette.colorScheme()
            assertTrue(contrast(scheme.onBackground, scheme.background) >= 4.5, "$palette body")
            assertTrue(contrast(scheme.onSurfaceVariant, scheme.surface) >= 4.5, "$palette secondary text")
        }
    }

    // Accent cards carry white text on their gradient; the darker end must still read at 3:1 (large text).
    @Test
    fun whiteTextOnEveryAccentGradientIsReadable() {
        AppPalette.entries.forEach { palette ->
            palette.accents().forEach { accent ->
                assertTrue(contrast(accent.end, accent.content) >= 3.0, "$palette accent ${accent.end}")
            }
        }
    }
}
