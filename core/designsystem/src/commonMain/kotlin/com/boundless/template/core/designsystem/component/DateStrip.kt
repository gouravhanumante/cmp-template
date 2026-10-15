package com.boundless.template.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.theme.Spacing
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

/**
 * One horizontal strip of the next few available days, meant to sit right above a time ruler so
 * day and time are picked on a single screen instead of behind a month grid.
 * Days with nothing on offer stay visible but greyed out and inert.
 */
@Composable
fun AppDateStrip(
    dates: List<LocalDate>,
    selected: LocalDate?,
    availableDates: Set<LocalDate>,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val selectedIndex = selected?.let { sel -> dates.indexOfFirst { it == sel } } ?: -1

    // Re-centers on first composition and every time the selection changes (not just once), so the
    // chosen date is never left sitting at the edge of the strip.
    LaunchedEffect(selectedIndex) {
        if (selectedIndex < 0) return@LaunchedEffect
        // The viewport isn't measured yet the very first time this runs, so wait for it rather
        // than scrolling against a stale (zero-width) layout.
        val viewportWidth =
            snapshotFlow { listState.layoutInfo.viewportSize.width }
                .filter { it > 0 }
                .first()
        val itemWidth = with(density) { DateChipWidth.roundToPx() }
        val centeringOffset = -(viewportWidth / 2) + (itemWidth / 2)
        listState.animateScrollToItem(selectedIndex, centeringOffset)
    }

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        items(dates, key = { it.toString() }) { date ->
            DateChip(
                date = date,
                isSelected = date == selected,
                isAvailable = date in availableDates,
                onClick = { onSelect(date) },
            )
        }
    }
}

private val DateChipWidth = 64.dp

@Composable
private fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val background by
        animateColorAsState(
            when {
                isSelected -> colors.primary
                isAvailable -> colors.primary.copy(alpha = 0.12f)
                else -> colors.surfaceVariant.copy(alpha = 0.5f)
            },
        )
    val content by
        animateColorAsState(
            when {
                isSelected -> colors.onPrimary
                isAvailable -> colors.onSurface
                else -> colors.onSurfaceVariant.copy(alpha = 0.45f)
            },
        )
    val verticalPadding by animateDpAsState(if (isSelected) Spacing.md else Spacing.sm)

    Column(
        modifier =
            Modifier
                .width(DateChipWidth)
                // Sharp corners, not a rounded card — a date chip reads as a tab, not a tile.
                .background(background, RectangleShape)
                .let { if (isAvailable) it.clickable(onClick = onClick) else it }
                .padding(vertical = verticalPadding, horizontal = Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text = date.monthAbbrev(), style = MaterialTheme.typography.labelSmall, color = content)
        Text(
            text = date.day.toString(),
            style = if (isSelected) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
            color = content,
        )
        Text(text = date.weekdayAbbrev(), style = MaterialTheme.typography.labelSmall, color = content)
    }
}

private fun LocalDate.monthAbbrev(): String = month.name.lowercase().replaceFirstChar(Char::uppercase).take(3)

private fun LocalDate.weekdayAbbrev(): String = dayOfWeek.name.lowercase().replaceFirstChar(Char::uppercase).take(3)
