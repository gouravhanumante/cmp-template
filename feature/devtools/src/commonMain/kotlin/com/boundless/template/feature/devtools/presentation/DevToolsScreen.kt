package com.boundless.template.feature.devtools.presentation

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.boundless.template.core.designsystem.component.AppCard
import com.boundless.template.core.designsystem.component.AppDivider
import com.boundless.template.core.designsystem.component.AppIconTile
import com.boundless.template.core.designsystem.component.AppListRow
import com.boundless.template.core.designsystem.component.AppScreen
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.resources.Res
import com.boundless.template.core.resources.devtools_checks_body
import com.boundless.template.core.resources.devtools_checks_title
import com.boundless.template.core.resources.devtools_components_body
import com.boundless.template.core.resources.devtools_components_title
import com.boundless.template.core.resources.devtools_database_body
import com.boundless.template.core.resources.devtools_database_title
import com.boundless.template.core.resources.devtools_subtitle
import com.boundless.template.core.resources.devtools_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The one entry point to every developer page; reached from Home in debug builds only. */
@Composable
fun DevToolsScreen(
    onBack: () -> Unit,
    onOpenDatabase: () -> Unit,
    onOpenComponents: () -> Unit,
    onOpenChecks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScreen(
        title = stringResource(Res.string.devtools_title),
        subtitle = stringResource(Res.string.devtools_subtitle),
        onBack = onBack,
        modifier = modifier,
    ) {
        AppCard {
            ToolRow(Res.string.devtools_database_title, Res.string.devtools_database_body, AppIcons.Ticket, onOpenDatabase)
            AppDivider()
            ToolRow(Res.string.devtools_components_title, Res.string.devtools_components_body, AppIcons.Palette, onOpenComponents)
            AppDivider()
            ToolRow(Res.string.devtools_checks_title, Res.string.devtools_checks_body, AppIcons.Shield, onOpenChecks)
        }
    }
}

@Composable
private fun ToolRow(
    title: StringResource,
    body: StringResource,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    AppListRow(
        title = stringResource(title),
        subtitle = stringResource(body),
        leading = { AppIconTile(icon = icon) },
        trailing = { Icon(AppIcons.Chevron, contentDescription = null) },
        onClick = onClick,
    )
}
