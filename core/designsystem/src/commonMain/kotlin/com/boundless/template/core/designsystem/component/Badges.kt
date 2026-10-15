package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.designsystem.theme.AppRadius
import com.boundless.template.core.designsystem.theme.Spacing

/**
 * Small reassurance marker — verified identity, fixed price, public meeting. Deliberately quiet:
 * trust should feel like a fact on the page, not a warning banner.
 */
@Composable
fun AppTrustBadge(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = AppIcons.Check,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    val container = containerFor(tint)
    val content = onContainerFor(tint)
    Row(
        modifier =
            modifier
                .background(container, AppRadius.pill)
                .padding(horizontal = Spacing.sm, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = content, modifier = Modifier.size(14.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = content)
    }
}

/**
 * The palette's real container colours rather than a low-alpha wash of the tint. A 10% tint over
 * white is barely a colour, which made every badge read as a placeholder rather than a fact.
 * A caller-supplied tint outside the theme roles still falls back to a (stronger) alpha blend.
 */
@Composable
private fun containerFor(tint: Color): Color {
    val colors = MaterialTheme.colorScheme
    return when (tint) {
        colors.primary -> colors.primaryContainer
        colors.secondary -> colors.secondaryContainer
        colors.tertiary -> colors.tertiaryContainer
        colors.error -> colors.errorContainer
        colors.onSurfaceVariant -> colors.surfaceContainerHigh
        else -> tint.copy(alpha = 0.16f)
    }
}

@Composable
private fun onContainerFor(tint: Color): Color {
    val colors = MaterialTheme.colorScheme
    return when (tint) {
        colors.primary -> colors.onPrimaryContainer
        colors.secondary -> colors.onSecondaryContainer
        colors.tertiary -> colors.onTertiaryContainer
        colors.error -> colors.onErrorContainer
        colors.onSurfaceVariant -> colors.onSurface
        else -> tint
    }
}

/** A short status label. Colour carries meaning, so the label alone is never the only signal. */
@Composable
fun AppStatusPill(
    text: String,
    tone: StatusTone,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val tint =
        when (tone) {
            // The palette carries one colour, so the affirmative state takes it and everything
            // still-in-progress recedes into the warm neutral. Red stays reserved for failure.
            StatusTone.POSITIVE -> colors.primary
            StatusTone.PENDING -> colors.secondary
            StatusTone.NEUTRAL -> colors.onSurfaceVariant
            StatusTone.NEGATIVE -> colors.error
        }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = onContainerFor(tint),
        modifier =
            modifier
                .background(containerFor(tint), AppRadius.pill)
                .padding(horizontal = Spacing.sm, vertical = 4.dp),
    )
}

enum class StatusTone {
    POSITIVE,
    PENDING,
    NEUTRAL,
    NEGATIVE,
}
