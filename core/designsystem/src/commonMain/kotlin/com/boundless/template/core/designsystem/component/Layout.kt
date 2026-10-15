package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Every screen's frame: optional back affordance, a large title, and a pinned action area.
 * Having one of these stops each screen inventing its own padding and title treatment.
 */
@Composable
fun AppScreen(
    title: String?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    scrollable: Boolean = true,
    scrollState: ScrollState? = null,
    contentSpacing: androidx.compose.ui.unit.Dp = Spacing.md,
    bottomBar: (@Composable () -> Unit)? = null,
    /** Rendered at the end of the title row \u2014 for a screen's one toolbar-level action. */
    actions: (@Composable () -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding(),
    ) {
        if (onBack != null || title != null || actions != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.screen, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.size(AppSize.minTouch)) {
                        Icon(imageVector = AppIcons.Back, contentDescription = stringResource(Res.string.ds_back))
                    }
                }
                if (title != null || subtitle != null) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        if (title != null) {
                            Text(text = title, style = MaterialTheme.typography.titleLarge)
                        }
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else if (onBack != null) {
                    Spacer(modifier = Modifier.weight(1f))
                }
                actions?.invoke()
            }
        }

        val effectiveScrollState =
            if (scrollable) {
                scrollState ?: rememberScrollState()
            } else {
                null
            }

        val body =
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .let { bodyModifier ->
                    effectiveScrollState?.let { bodyModifier.verticalScroll(it) } ?: bodyModifier
                }

        Column(
            modifier = body.padding(horizontal = Spacing.screen, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(contentSpacing),
            content = content,
        )

        if (bottomBar != null) {
            Column(modifier = Modifier.fillMaxWidth().padding(Spacing.screen)) { bottomBar() }
        }
    }
}

/**
 * A titled block of content. The heading sits close to what it labels while sections stay far
 * apart — that difference in gap is what makes the grouping readable without any borders.
 */
@Composable
fun AppSection(
    title: String?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.group),
    ) {
        if (title != null) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        content()
    }
}

/** Explains why a list is empty and what to do next — never a bare "no results". */
@Composable
fun AppEmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (action != null) {
            Box(modifier = Modifier.padding(top = Spacing.sm)) { action() }
        }
    }
}

/** Progress marker for multi-step flows: "Step 2 of 4" plus a segmented bar. */
@Composable
fun AppStepHeader(
    step: Int,
    total: Int,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            repeat(total) { index ->
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                color =
                                    if (index < step) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                shape = MaterialTheme.shapes.extraSmall,
                            ),
                )
            }
        }
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
