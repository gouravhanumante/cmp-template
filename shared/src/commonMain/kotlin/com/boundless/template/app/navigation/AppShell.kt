package com.boundless.template.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.boundless.template.core.common.AppBuildInfo
import com.boundless.template.core.common.AppPlatform
import com.boundless.template.core.common.currentPlatform
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

enum class NavigationLayout { BOTTOM, RAIL, SIDEBAR }

fun navigationLayout(platform: AppPlatform, width: Int): NavigationLayout = when (platform) {
    AppPlatform.ANDROID, AppPlatform.IOS -> NavigationLayout.BOTTOM
    AppPlatform.DESKTOP, AppPlatform.WEB -> if (width < 840) NavigationLayout.RAIL else NavigationLayout.SIDEBAR
}

private enum class Section(val title: StringResource, val icon: ImageVector) {
    HOME(Res.string.nav_home, AppIcons.Home),
    TEST(Res.string.nav_test, AppIcons.Shield),
    SETTINGS(Res.string.nav_settings, AppIcons.Palette),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppShell(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    buildInfo: AppBuildInfo = koinInject(),
) {
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination
    val selected = when {
        destination?.hasRoute<SettingsRoute>() == true -> Section.SETTINGS
        destination == null || destination.hasRoute<HomeRoute>() -> Section.HOME
        else -> Section.TEST
    }
    val sections = Section.entries.filter { it != Section.TEST || buildInfo.isDebug }
    val select: (Section) -> Unit = { section ->
        if (section != selected) {
            val route: Any = when (section) {
                Section.HOME -> HomeRoute
                Section.TEST -> DevToolsRoute
                Section.SETTINGS -> SettingsRoute
            }
            navController.navigate(route) {
                popUpTo<HomeRoute> { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val layout = navigationLayout(currentPlatform, maxWidth.value.toInt())
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (layout == NavigationLayout.BOTTOM) NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    sections.forEach { section ->
                        NavigationBarItem(
                            selected = selected == section,
                            onClick = { select(section) },
                            icon = { Icon(section.icon, contentDescription = null) },
                            label = { Text(stringResource(section.title)) },
                        )
                    }
                }
            },
        ) { padding ->
            Row(Modifier.fillMaxSize().padding(padding)) {
                if (layout != NavigationLayout.BOTTOM) {
                    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxHeight()) {
                        Column(
                            Modifier.width(if (layout == NavigationLayout.SIDEBAR) 224.dp else 72.dp)
                                .safeDrawingPadding().padding(horizontal = Spacing.sm, vertical = Spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            if (layout == NavigationLayout.SIDEBAR) {
                                Text(stringResource(Res.string.shell_brand), style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.lg))
                            }
                            sections.forEach { section ->
                                val active = selected == section
                                val label = stringResource(section.title)
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = { PlainTooltip { Text(label) } },
                                    state = rememberTooltipState(),
                                ) {
                                    Surface(
                                        color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        contentColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        shape = MaterialTheme.shapes.small,
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                                .selectable(active, role = Role.Tab, onClick = { select(section) })
                                                .padding(horizontal = Spacing.md),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                                        ) {
                                            Icon(section.icon, contentDescription = if (layout == NavigationLayout.RAIL) label else null,
                                                modifier = Modifier.size(24.dp))
                                            if (layout == NavigationLayout.SIDEBAR) Text(label, style = MaterialTheme.typography.labelLarge)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                AppNavHost(navController, Modifier.weight(1f).fillMaxHeight(), buildInfo)
            }
        }
    }
}