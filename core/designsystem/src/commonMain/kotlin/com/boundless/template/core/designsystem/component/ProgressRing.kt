package com.boundless.template.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A percentage that's arriving, not just a fact — "profile strength" style ring instead of a
 * flat bar, for the one screen that's explicitly a checklist toward a goal.
 */
@Composable
fun AppProgressRing(
    percent: Int,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    strokeWidth: Dp = 8.dp,
) {
    val animated by animateFloatAsState(
        targetValue = percent.coerceIn(0, 100) / 100f,
        animationSpec = tween(600),
        label = "progressRing",
    )
    val track = MaterialTheme.colorScheme.surfaceContainerHigh
    val color = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx())
            drawArc(color = track, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = stroke)
            drawArc(color = color, startAngle = -90f, sweepAngle = 360f * animated, useCenter = false, style = stroke)
        }
        Text(text = "${percent.coerceIn(0, 100)}%", style = MaterialTheme.typography.titleLarge)
    }
}
