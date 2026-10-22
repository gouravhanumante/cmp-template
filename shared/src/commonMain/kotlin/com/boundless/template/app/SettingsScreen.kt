package com.boundless.template.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boundless.template.core.designsystem.component.AppDivider
import com.boundless.template.core.designsystem.component.AppScreen
import com.boundless.template.core.designsystem.theme.AppPalette
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.designsystem.theme.colorScheme
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val palette by viewModel.palette.collectAsStateWithLifecycle()
    AppScreen(title = stringResource(Res.string.nav_settings), contentSpacing = Spacing.lg) {
        Text(stringResource(Res.string.devtools_cmp_theme), style = MaterialTheme.typography.titleMedium)
        AppPalette.entries.forEach { option ->
            val label = when (option) {
                AppPalette.INDIGO -> Res.string.devtools_cmp_theme_indigo
                AppPalette.TEAL -> Res.string.devtools_cmp_theme_teal
                AppPalette.TERRACOTTA -> Res.string.devtools_cmp_theme_terracotta
                AppPalette.FOREST -> Res.string.devtools_cmp_theme_forest
            }
            Row(
                modifier = Modifier.fillMaxWidth().selectable(
                    selected = palette == option,
                    role = Role.RadioButton,
                    onClick = { viewModel.select(option) },
                ).padding(vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    androidx.compose.foundation.layout.Box(Modifier.size(Spacing.lg).background(option.colorScheme().primary, CircleShape))
                    androidx.compose.foundation.layout.Box(Modifier.size(Spacing.lg).background(option.colorScheme().secondary, CircleShape))
                }
                Column(Modifier.weight(1f)) {
                    Text(stringResource(label), style = MaterialTheme.typography.bodyLarge)
                }
                RadioButton(selected = palette == option, onClick = null)
            }
            AppDivider()
        }
        Text(stringResource(Res.string.settings_font), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(Res.string.settings_font_sample), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(Res.string.settings_font_body), style = MaterialTheme.typography.bodyLarge)
    }
}