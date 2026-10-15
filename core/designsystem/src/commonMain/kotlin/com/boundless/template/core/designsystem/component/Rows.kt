package com.boundless.template.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.theme.AppRadius
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing

/**
 * A row in a list of like-for-like items. Research is consistent that lists beat cards when
 * items are homogeneous: fixed positions make them scannable and comparable, which a card
 * grid actively works against.
 */
@Composable
fun AppListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    // A flat press-tint overlay instead of the platform ripple: Compose Multiplatform's default
    // ripple either doesn't render on iOS or looks foreign there, and a rectangular ripple stacked
    // against a rounded AppCard reads as a glitch at the first/last row. This is one animation that
    // looks and feels the same on both platforms.
    //
    // The fade is driven by a plain Animatable read inside drawBehind rather than
    // `animateFloatAsState` hoisted to a `val ... by`, which would make this whole row recompose
    // on every animation frame. That mattered in practice: the same tap that starts the ~180ms
    // release fade also fires onClick, and when onClick pushes a new screen the fade's per-frame
    // recomposition of this row was landing on top of the ~300ms nav transition and dropping
    // frames — visible as jank on every row-triggered push, never on a AppTile (plain ripple) tap.
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha = remember { Animatable(0f) }
    LaunchedEffect(isPressed) {
        pressAlpha.animateTo(
            targetValue = if (isPressed) 0.06f else 0f,
            animationSpec = tween(durationMillis = if (isPressed) 60 else 180),
        )
    }
    val pressTint = MaterialTheme.colorScheme.onSurface

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .let {
                    if (onClick != null) {
                        it.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                    } else {
                        it
                    }
                }
                .drawBehind {
                    val alpha = pressAlpha.value
                    if (alpha > 0f) drawRect(pressTint.copy(alpha = alpha))
                }
                .heightIn(min = AppSize.rowHeight)
                .padding(vertical = Spacing.smd),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) leading()

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (trailing != null) trailing()
    }
}

/** A setting that toggles in place. The whole row is the target, not just the switch. */
@Composable
fun AppSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
) {
    AppListRow(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onClick = if (enabled) ({ onCheckedChange(!checked) }) else null,
        trailing = {
            Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
        },
    )
}

/** Hairline separator using the card border colour so lists and cards agree. */
@Composable
fun AppDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = MaterialTheme.colorScheme.outlineVariant)
}


/** Rounded icon square used for role tiles, activity tiles and empty-state art. */
@Composable
fun AppIconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    container: Color = tint.copy(alpha = 0.12f),
    size: androidx.compose.ui.unit.Dp = AppSize.tile,
    /** Fills the tile with a gradient and flips the icon to white. */
    brush: Brush? = null,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .let {
                    if (brush != null) it.background(brush, AppRadius.card) else it.background(container, AppRadius.card)
                },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (brush != null) Color.White else tint,
            modifier = Modifier.size(size / 2),
        )
    }
}

/** Which accent an [AppActionCard] carries. There is deliberately no decorative red. */
enum class AppTone {
    NEUTRAL,
    PRIMARY,
    SECONDARY,
    TERTIARY,

    /** Genuine problems only — never decoration. */
    CRITICAL,
}

/**
 * Tinted card carrying one idea and at most one action: "you have a meeting at 8am", "finish
 * verification". Use it instead of rebuilding a title + body + button block by hand.
 */
@Composable
fun AppActionCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tone: AppTone = AppTone.NEUTRAL,
    action: (@Composable () -> Unit)? = null,
    /** Optional slot pinned to the title row's end — a close button for a dismissible card, so a
     *  standalone dismiss doesn't need its own row (and the height that comes with one). */
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val container =
        when (tone) {
            AppTone.NEUTRAL -> colors.surfaceContainerLow
            AppTone.PRIMARY -> colors.primaryContainer
            AppTone.SECONDARY -> colors.secondaryContainer
            AppTone.TERTIARY -> colors.tertiaryContainer
            AppTone.CRITICAL -> colors.errorContainer
        }
    val onContainer =
        when (tone) {
            AppTone.NEUTRAL -> colors.onSurface
            AppTone.PRIMARY -> colors.onPrimaryContainer
            AppTone.SECONDARY -> colors.onSecondaryContainer
            AppTone.TERTIARY -> colors.onTertiaryContainer
            AppTone.CRITICAL -> colors.onErrorContainer
        }

    Column(
        modifier = modifier.fillMaxWidth().background(container, AppRadius.card).padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.group),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = onContainer,
                    modifier = Modifier.size(AppSize.iconMd),
                )
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = onContainer, modifier = Modifier.weight(1f))
            trailing?.invoke()
        }

        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = onContainer.copy(alpha = 0.82f),
        )

        if (action != null) {
            Box(modifier = Modifier.padding(top = Spacing.xs)) { action() }
        }
    }
}
