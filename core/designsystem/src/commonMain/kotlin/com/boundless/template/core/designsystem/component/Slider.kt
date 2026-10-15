package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.boundless.template.core.designsystem.theme.Spacing

/** A single numeric range picker, e.g. a search radius. */
@Composable
fun AppSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    valueLabel: (Float) -> String = { "${it.toInt()} km" },
    enabled: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        AppSectionHeader(title = title, subtitle = valueLabel(value))
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange, enabled = enabled)
    }
}
