package com.boundless.template.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boundless.template.core.designsystem.component.AppCard
import com.boundless.template.core.designsystem.component.AppHeroCard
import com.boundless.template.core.designsystem.component.AppListRow
import com.boundless.template.core.designsystem.component.AppScreen
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.designsystem.component.AppSecondaryButton
import com.boundless.template.core.designsystem.component.AppSection
import com.boundless.template.core.designsystem.component.AppStatusPill
import com.boundless.template.core.designsystem.component.StatusTone
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.resources.Res
import com.boundless.template.core.resources.home_backend_checking
import com.boundless.template.core.resources.home_backend_offline
import com.boundless.template.core.resources.home_backend_online
import com.boundless.template.core.resources.home_backend_subtitle
import com.boundless.template.core.resources.home_backend_version
import com.boundless.template.core.resources.home_check_again
import com.boundless.template.core.resources.home_developer_tools_body
import com.boundless.template.core.resources.home_developer_tools_title
import com.boundless.template.core.resources.home_eyebrow
import com.boundless.template.core.resources.home_item_architecture_body
import com.boundless.template.core.resources.home_item_architecture_title
import com.boundless.template.core.resources.home_item_design_body
import com.boundless.template.core.resources.home_item_design_title
import com.boundless.template.core.resources.home_item_network_body
import com.boundless.template.core.resources.home_item_network_title
import com.boundless.template.core.resources.home_item_platforms_body
import com.boundless.template.core.resources.home_item_platforms_title
import com.boundless.template.core.resources.home_item_storage_body
import com.boundless.template.core.resources.home_item_storage_title
import com.boundless.template.core.resources.home_item_tooling_body
import com.boundless.template.core.resources.home_item_tooling_title
import com.boundless.template.core.resources.home_section_backend
import com.boundless.template.core.resources.home_section_included
import com.boundless.template.core.resources.home_subtitle
import com.boundless.template.core.resources.home_title
import com.boundless.template.core.resources.resolve
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val includedItems: List<Pair<StringResource, StringResource>> =
    listOf(
        Res.string.home_item_platforms_title to Res.string.home_item_platforms_body,
        Res.string.home_item_architecture_title to Res.string.home_item_architecture_body,
        Res.string.home_item_design_title to Res.string.home_item_design_body,
        Res.string.home_item_network_title to Res.string.home_item_network_body,
        Res.string.home_item_storage_title to Res.string.home_item_storage_body,
        Res.string.home_item_tooling_title to Res.string.home_item_tooling_body,
    )

/** [onOpenDeveloperTools] is null in release builds, which hides the entry. */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onOpenDeveloperTools: (() -> Unit)? = null,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(state = state, onEvent = viewModel::onEvent, onOpenDeveloperTools = onOpenDeveloperTools)
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
    onOpenDeveloperTools: (() -> Unit)? = null,
) {
    AppScreen(title = null, modifier = modifier, contentSpacing = Spacing.section) {
        AppHeroCard(
            title = stringResource(Res.string.home_title),
            eyebrow = stringResource(Res.string.home_eyebrow),
            subtitle = stringResource(Res.string.home_subtitle),
        )

        AppSection(
            title = stringResource(Res.string.home_section_backend),
            subtitle = stringResource(Res.string.home_backend_subtitle),
        ) {
            AppCard {
                BackendStatus(state = state, onRetry = { onEvent(HomeEvent.Retry) })
            }
        }

        AppSection(title = stringResource(Res.string.home_section_included)) {
            AppCard {
                includedItems.forEach { (title, body) ->
                    AppListRow(title = stringResource(title), subtitle = stringResource(body))
                }
            }
        }

        if (onOpenDeveloperTools != null) {
            AppCard(onClick = onOpenDeveloperTools) {
                AppListRow(
                    title = stringResource(Res.string.home_developer_tools_title),
                    subtitle = stringResource(Res.string.home_developer_tools_body),
                    trailing = { Icon(AppIcons.Chevron, contentDescription = null) },
                )
            }
        }
    }
}

@Composable
private fun BackendStatus(
    state: HomeUiState,
    onRetry: () -> Unit,
) {
    val error = state.errorMessage
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(AppSize.iconSm))
                Text(stringResource(Res.string.home_backend_checking), style = MaterialTheme.typography.bodyMedium)
            }
            error != null -> AppStatusPill(text = stringResource(Res.string.home_backend_offline), tone = StatusTone.NEGATIVE)
            else -> {
                AppStatusPill(text = stringResource(Res.string.home_backend_online), tone = StatusTone.POSITIVE)
                state.version?.let {
                    Text(stringResource(Res.string.home_backend_version, it), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
    if (error != null) {
        Text(error.resolve(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    if (!state.isLoading) {
        AppSecondaryButton(text = stringResource(Res.string.home_check_again), onClick = onRetry)
    }
}
