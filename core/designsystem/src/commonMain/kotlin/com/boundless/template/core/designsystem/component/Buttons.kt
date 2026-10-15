package com.boundless.template.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.theme.AppAccent
import com.boundless.template.core.designsystem.theme.AppRadius
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing

private val ButtonHeight = AppSize.buttonHeight

/**
 * For an action sitting on an accent-filled surface, where the theme's own button colours would
 * fight the fill — a filled one goes white-on-accent, an unfilled one is a white hairline.
 */
@Composable
fun AppOnAccentButton(
    text: String,
    onClick: () -> Unit,
    accent: AppAccent,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
) {
    if (filled) {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().heightIn(min = ButtonHeight),
            shape = AppRadius.button,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = accent.start),
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().heightIn(min = ButtonHeight),
            shape = AppRadius.button,
            border = BorderStroke(1.dp, accent.content.copy(alpha = 0.55f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = accent.content),
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = ButtonHeight),
        enabled = enabled && !loading,
        shape = AppRadius.button,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current,
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * Middle emphasis. This is the default for supporting actions: an outlined button reads as a
 * chip, so a screen full of them has no hierarchy at all.
 */
@Composable
fun AppTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    /** For an action that is consequential but not destructive — reporting a no-show freezes a
     * payout and opens a review, yet undoes nothing. The solid error outline stays reserved for
     * the action that actually destroys something. */
    caution: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = ButtonHeight),
        enabled = enabled,
        shape = AppRadius.button,
        colors =
            if (caution) {
                ButtonDefaults.filledTonalButtonColors(
                    containerColor = colors.errorContainer,
                    contentColor = colors.onErrorContainer,
                )
            } else {
                ButtonDefaults.filledTonalButtonColors()
            },
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Lowest-emphasis full-width action. Reserve for the one case that must not compete. */
@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    /** For the one destructive outline action on a screen (e.g. "Cancel") — a plain hairline
     * in the neutral outline colour reads as barely-there next to filled buttons, so this
     * borrows the error colour instead of adding another emphasis tier. */
    destructive: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = ButtonHeight),
        enabled = enabled && !loading,
        shape = AppRadius.button,
        border =
            BorderStroke(
                width = if (destructive) 1.5.dp else 1.dp,
                color = if (destructive) colors.error else colors.onSurfaceVariant,
            ),
        colors =
            if (destructive) {
                ButtonDefaults.outlinedButtonColors(contentColor = colors.error)
            } else {
                ButtonDefaults.outlinedButtonColors()
            },
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current,
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = AppSize.minTouch),
        enabled = enabled,
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(Spacing.xs))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * A wrap-content pill for one inline action inside a card — "Pay", "Accept" — where a
 * [AppPrimaryButton]'s full-width bar would outweigh the row it sits in.
 */
@Composable
fun AppChipButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 36.dp),
        enabled = enabled,
        shape = AppRadius.pill,
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppSize.iconSm).padding(end = Spacing.xs),
            )
        }
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}

/** Reserves its own height so showing an error never shifts the form below it. */
@Composable
fun AppErrorText(
    message: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().heightIn(min = 20.dp)) {
        AnimatedVisibility(visible = message != null) {
            Text(
                text = message.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun AppScreenMessage(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (action != null) {
            Column(modifier = Modifier.padding(top = Spacing.sm)) { action() }
        }
    }
}
