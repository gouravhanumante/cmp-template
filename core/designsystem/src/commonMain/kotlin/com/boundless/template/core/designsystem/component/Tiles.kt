package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.boundless.template.core.designsystem.theme.AppAccent
import com.boundless.template.core.designsystem.theme.AppRadius
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.designsystem.theme.appAccents

/**
 * A shortcut to somewhere else in the app. Tinted rather than filled: these are navigation, not
 * news, so they carry the palette without competing with whatever the screen is actually about.
 */
@Composable
fun AppTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: AppAccent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .height(AppSize.tileHeight)
                .clip(AppRadius.card)
                .background(accent.brush)
                .clickable(onClick = onClick)
                .padding(Spacing.smd),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent.content,
            modifier = Modifier.size(AppSize.iconMd),
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = accent.content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = accent.content.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Lays tiles out two per row. Fixed at two columns because a tile any narrower stops fitting a
 * readable title on a phone.
 */
@Composable
fun AppTileGrid(
    modifier: Modifier = Modifier,
    tiles: List<@Composable (Modifier) -> Unit>,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.smd),
    ) {
        tiles.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.smd)) {
                row.forEach { tile -> tile(Modifier.weight(1f)) }
                // Keeps a lone trailing tile at half width instead of stretching it.
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * Full-width panel for the one thing a screen most wants to say. [filled] spends the gradient;
 * leave it off for a second panel so only one region on screen is loud.
 */
@Composable
fun AppHeroCard(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    accent: AppAccent = appAccents()[0],
    filled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val foreground = if (filled) accent.content else accent.onSoft

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(AppRadius.card)
                .background(if (filled) accent.brush else SolidColor(accent.soft))
                .let { if (onClick != null) it.clickable(onClick = onClick) else it }
                .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (eyebrow != null) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.bodySmall,
                color = if (filled) foreground.copy(alpha = 0.8f) else accent.mark,
            )
        }

        Text(text = title, style = MaterialTheme.typography.headlineSmall, color = foreground)

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = foreground.copy(alpha = 0.88f),
            )
        }

        content()
    }
}

/** A single fact inside a [AppHeroCard], e.g. "₹1,200" over "Payment held". */
@Composable
fun AppHeroStat(
    value: String,
    label: String,
    accent: AppAccent,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = accent.content)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = accent.content.copy(alpha = 0.8f),
        )
    }
}

/** A headline number with its label. Use in a [AppTileGrid] to turn a wall of amounts into a board. */
@Composable
fun AppStatTile(
    value: String,
    label: String,
    icon: ImageVector,
    accent: AppAccent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(AppRadius.card)
                .background(accent.brush)
                .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent.content,
                modifier = Modifier.size(AppSize.iconSm),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = accent.content.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = accent.content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
