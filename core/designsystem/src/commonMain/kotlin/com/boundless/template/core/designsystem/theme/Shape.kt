package com.boundless.template.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Softly rounded, never pill-shaped. Full-round corners read as a dating product; these keep
 * surfaces calm and let the verified badge be the only fully-rounded element on screen.
 */
val AppShapes =
    Shapes(
        extraSmall = RoundedCornerShape(6.dp),
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(14.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(28.dp),
    )

object AppRadius {
    val card = RoundedCornerShape(16.dp)
    val button = RoundedCornerShape(8.dp)
    val field = RoundedCornerShape(12.dp)
    val chip = RoundedCornerShape(10.dp)
    val sheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val pill = RoundedCornerShape(percent = 50)
}
