package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource

enum class NoticeTone {
    WARNING,
    INFO,
}

/**
 * Full-bleed app notice — a maintenance window or an announcement. Deliberately a solid, edge-to-
 * edge strip rather than a tinted card: this is a system message riding above the whole app, not
 * another dismissible content nudge, and needs to read as unmistakably different from one.
 */
@Composable
fun AppNoticeBanner(
    title: String,
    message: String,
    tone: NoticeTone,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val fill = if (tone == NoticeTone.WARNING) colors.error else colors.tertiary
    val onFill = if (tone == NoticeTone.WARNING) colors.onError else colors.onTertiary

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(fill)
                .padding(horizontal = Spacing.md, vertical = Spacing.smd),
        horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = if (tone == NoticeTone.WARNING) AppIcons.Shield else AppIcons.Sparkle,
            contentDescription = null,
            tint = onFill,
            modifier = Modifier.size(AppSize.iconSm),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = onFill)
            Text(text = message, style = MaterialTheme.typography.bodySmall, color = onFill)
        }
        if (onDismiss != null) {
            IconButton(onClick = onDismiss, modifier = Modifier.size(AppSize.iconMd)) {
                Icon(imageVector = AppIcons.Close, contentDescription = stringResource(Res.string.ds_dismiss), tint = onFill)
            }
        }
    }
}
