package com.boundless.template.core.designsystem.component

import androidx.compose.animation.core.animate
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.common.model.TimeOfDay
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.roundToInt

private const val DAY_MINUTES = 24 * 60
private val MINUTE_WIDTH = 1.6.dp
private val TICKS_HEIGHT = 24.dp
private val LABELS_HEIGHT = 18.dp
private val LABEL_GAP = 4.dp
private val TRACK_HEIGHT = TICKS_HEIGHT + LABEL_GAP + LABELS_HEIGHT
private val EDGE_MARGIN_PX = 60f

/**
 * A 24-hour ruler you drag under a fixed pointer, rather than a thumb you drag along a fixed
 * track — the timeline moves, the pointer never does. Every tick and label is positioned
 * relative to the track's own measured width (via [onSizeChanged]), so nothing depends on a
 * giant off-screen canvas or a parent's constraints resolving a particular way. Green marks the
 * stretch of the day this package could actually start in; releasing always snaps to the nearest
 * real start time.
 */
@Composable
fun AppTimeRuler(
    slots: List<String>,
    selected: String?,
    durationLabel: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelFor: (String) -> String = { it },
    /** Already-booked stretches of the day — drawn red and never reachable via drag, since
     *  [slots] (what dragging can actually land on) never contains a busy minute to begin with. */
    busyRanges: List<Pair<Int, Int>> = emptyList(),
    /** How long the chosen slot runs — extends each green band past its last selectable
     *  *start* time out to where that slot would end, and sizes the occupancy
     *  preview drawn under the pointer. */
    durationMinutes: Int = 0,
) {
    val parsedSlots = remember(slots) { slots.mapNotNull { raw -> TimeOfDay.parse(raw)?.let { it to raw } } }
    if (parsedSlots.isEmpty()) return
    val bands = remember(parsedSlots, durationMinutes) { bandsFrom(parsedSlots.map { it.first }, durationMinutes) }

    var centerMinute by remember(slots) {
        mutableFloatStateOf((selected?.let(TimeOfDay::parse) ?: parsedSlots.first().first).toFloat())
    }
    LaunchedEffect(selected, slots) {
        selected?.let(TimeOfDay::parse)?.let { centerMinute = it.toFloat() }
    }

    val trackPast = MaterialTheme.colorScheme.outlineVariant
    val trackAvailable = MaterialTheme.colorScheme.primary
    val trackBusy = MaterialTheme.colorScheme.error
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val density = LocalDensity.current
    val minuteWidthPx = with(density) { MINUTE_WIDTH.toPx() }
    val labelHalfWidthPx = with(density) { 22.dp.toPx() }
    var widthPx by remember { mutableStateOf(0f) }

    Column(modifier = modifier.fillMaxWidth()) {
        // IntrinsicSize.Min caps the row at its content's natural height — without it, the
        // divider's fillMaxHeight() stretches the whole row (and the line) to fill however much
        // vertical space this Column happens to be given, which is nearly the full screen here.
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Column(
                modifier = Modifier.weight(1f).padding(end = Spacing.smd),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = stringResource(Res.string.ds_time_start), style = MaterialTheme.typography.labelSmall, color = labelColor)
                Text(
                    text = selected?.let(labelFor) ?: "--:--",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = Spacing.smd),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = stringResource(Res.string.ds_time_end), style = MaterialTheme.typography.labelSmall, color = labelColor)
                Text(
                    // Same start-minute-to-string round trip the ruler already draws its
                    // occupancy preview from, so this always agrees with what's on screen.
                    text = selected?.let(TimeOfDay::parse)?.let { labelFor(TimeOfDay.format(it + durationMinutes)) } ?: "--:--",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Column(
                modifier = Modifier.weight(1f).padding(start = Spacing.smd),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = stringResource(Res.string.ds_time_duration), style = MaterialTheme.typography.labelSmall, color = labelColor)
                Text(text = durationLabel, style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        Text(
            text = "⌄",
            style = MaterialTheme.typography.titleMedium,
            color = trackAvailable,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        // Column, not Box — a Box stacks children on top of each other (z-order), which is
        // exactly what put the tick line and the hour labels at the same y and made them
        // overlap. This needs the ticks row and the label row to sit one above the other.
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(TRACK_HEIGHT)
                    .onSizeChanged { widthPx = it.width.toFloat() }
                    .clipToBounds()
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state =
                            rememberDraggableState { delta ->
                                centerMinute = (centerMinute - delta / minuteWidthPx)
                                    .coerceIn(0f, DAY_MINUTES - 1f)
                            },
                        onDragStopped = {
                            val nearest = parsedSlots.minByOrNull { abs(it.first - centerMinute) }
                            if (nearest != null) {
                                animate(
                                    initialValue = centerMinute,
                                    targetValue = nearest.first.toFloat(),
                                ) { value, _ -> centerMinute = value }
                                onSelect(nearest.second)
                            }
                        },
                    ),
        ) {
            if (widthPx > 0f) {
                val centerPx = widthPx / 2f
                fun xFor(minute: Int) = centerPx + (minute - centerMinute) * minuteWidthPx

                Canvas(modifier = Modifier.fillMaxWidth().height(TICKS_HEIGHT)) {
                    val cy = 4.dp.toPx()
                    drawLine(
                        color = trackPast,
                        start = Offset(0f, cy),
                        end = Offset(size.width, cy),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    bands.forEach { band ->
                        val startX = xFor(band.first)
                        val endX = xFor(band.second).let { if (it - startX < 6.dp.toPx()) startX + 6.dp.toPx() else it }
                        drawLine(
                            color = trackAvailable,
                            start = Offset(startX, cy),
                            end = Offset(endX, cy),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    busyRanges.forEach { (start, end) ->
                        val startX = xFor(start)
                        val endX = xFor(end).let { if (it - startX < 6.dp.toPx()) startX + 6.dp.toPx() else it }
                        drawLine(
                            color = trackBusy,
                            start = Offset(startX, cy),
                            end = Offset(endX, cy),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    var minute = 0
                    while (minute < DAY_MINUTES) {
                        val x = xFor(minute)
                        if (x in -EDGE_MARGIN_PX..(size.width + EDGE_MARGIN_PX)) {
                            val isHour = minute % 60 == 0
                            val inBand = bands.any { minute in it.first..it.second }
                            val inBusy = busyRanges.any { minute in it.first until it.second }
                            val tickColor = if (inBand) trackAvailable else if (inBusy) trackBusy else trackPast
                            drawLine(
                                color = tickColor,
                                start = Offset(x, cy - 5.dp.toPx()),
                                end = Offset(x, cy + 5.dp.toPx()),
                                strokeWidth = 1.dp.toPx(),
                            )
                            if (isHour) {
                                drawCircle(color = tickColor, radius = 2.dp.toPx(), center = Offset(x, cy + 10.dp.toPx()))
                            }
                        }
                        minute += 30
                    }

                    // The occupancy preview: how much of the day the current selection would
                    // actually take up, starting at the pointer (which never itself moves — the
                    // ruler scrolls under it) and running for the slot's duration. A double
                    // line rather than the single-line style used everywhere else on the ruler,
                    // so it reads as "this much" rather than another availability band.
                    if (durationMinutes > 0) {
                        val previewEndX = centerPx + durationMinutes * minuteWidthPx
                        listOf(cy - 3.dp.toPx(), cy + 3.dp.toPx()).forEach { y ->
                            drawLine(
                                color = trackAvailable,
                                start = Offset(centerPx, y),
                                end = Offset(previewEndX, y),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(LABEL_GAP))

                Box(modifier = Modifier.fillMaxWidth().height(LABELS_HEIGHT)) {
                    var hour = 0
                    while (hour < 24) {
                        val x = xFor(hour * 60)
                        if (x in -EDGE_MARGIN_PX..(widthPx + EDGE_MARGIN_PX)) {
                            Text(
                                text = TimeOfDay.formatDisplay(hour * 60),
                                style = MaterialTheme.typography.labelSmall,
                                color = labelColor,
                                modifier = Modifier.offset { IntOffset((x - labelHalfWidthPx).roundToInt(), 0) },
                            )
                        }
                        hour += 1
                    }
                }
            }
        }
    }
}

/** Consecutive 30-minute start times collapse into one continuous band, extended past the last
 *  start to where a slot starting there would end — otherwise the tail end of a
 *  window (as much as one duration's worth) reads as unavailable even though a slot can
 *  still start there. */
private fun bandsFrom(minutes: List<Int>, durationMinutes: Int): List<Pair<Int, Int>> {
    if (minutes.isEmpty()) return emptyList()
    val sorted = minutes.sorted()
    val bands = mutableListOf<Pair<Int, Int>>()
    var start = sorted.first()
    var prev = sorted.first()
    sorted.drop(1).forEach { m ->
        if (m - prev > 30) {
            bands += start to prev + durationMinutes
            start = m
        }
        prev = m
    }
    bands += start to prev + durationMinutes
    return bands
}
