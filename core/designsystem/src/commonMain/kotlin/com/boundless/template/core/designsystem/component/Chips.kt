package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.theme.AppRadius
import com.boundless.template.core.designsystem.theme.Spacing

data class SelectableOption(
    val id: String,
    val label: String,
)

/**
 * A selected chip carries the lead brand colour, not Material's default secondary — with amber as
 * the counterpoint hue, the stock selection state was pulling the eye away from the green.
 * Unselected chips sit on [surface] so they read as controls against the warm page ground.
 */
@Composable
fun appFilterChipColors(): SelectableChipColors =
    FilterChipDefaults.filterChipColors(
        containerColor = MaterialTheme.colorScheme.surface,
        labelColor = MaterialTheme.colorScheme.onSurface,
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )

/**
 * Multi-select preset picker. Structured presets are used instead of free text so
 * nothing leaks contact details and discovery stays filterable.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppChipGroup(
    title: String,
    options: List<SelectableOption>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxSelections: Int? = null,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        AppSectionHeader(
            title = title,
            subtitle = maxSelections?.let { "${selectedIds.size} of ${options.size} selected" },
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            options.forEach { option ->
                val selected = option.id in selectedIds
                val atLimit = maxSelections != null && !selected && selectedIds.size >= maxSelections
                FilterChip(
                    selected = selected,
                    onClick = { onToggle(option.id) },
                    enabled = enabled && !atLimit,
                    label = { Text(option.label) },
                    colors = appFilterChipColors(),
                )
            }
        }
    }
}

@Composable
fun AppSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Two or three mutually-exclusive views of the same screen (Upcoming/Past) — a switch, not a
 * filter, so it is one pill-shaped track rather than a row of [FilterChip]s.
 */
@Composable
fun AppSegmentedTabs(
    options: List<SelectableOption>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, AppRadius.pill)
                .padding(3.dp),
    ) {
        options.forEach { option ->
            val selected = option.id == selectedId
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(AppRadius.pill)
                        .background(if (selected) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { onSelect(option.id) }
                        .padding(vertical = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        if (selected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
    }
}

/** Single-choice variant for small closed sets such as gender. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppSingleChoiceGroup(
    title: String,
    options: List<SelectableOption>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        AppSectionHeader(title = title)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            options.forEach { option ->
                val selected = option.id == selectedId
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .alpha(if (enabled) 1f else 0.45f)
                            .clickable(enabled = enabled) { onSelect(option.id) }
                            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = { onSelect(option.id) },
                        enabled = enabled,
                    )
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = Spacing.xs),
                    )
                }
            }
        }
    }
}
