package com.boundless.template.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.theme.AppRadius
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing

// Without a shared clock, every appShimmer() box runs its own independent infinite transition, so
// a screen full of placeholders flickers out of phase instead of reading as one coherent sweep.
private val LocalShimmerProgress = compositionLocalOf<Float?> { null }

/**
 * Wrap a whole loading layout in this so every [appShimmer] placeholder inside sweeps together,
 * the way a single light source passing over the page would. Falls back to an independent timer
 * per box when a placeholder is used outside a host, so existing call sites are unaffected.
 */
@Composable
fun ShimmerHost(content: @Composable () -> Unit) {
    val progress = rememberShimmerProgress()
    CompositionLocalProvider(LocalShimmerProgress provides progress) { content() }
}

@Composable
private fun rememberShimmerProgress(): Float {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerProgress",
    )
    return progress
}

/**
 * A soft diagonal sheen sweeping across the element, so a loading list reads as "arriving" rather
 * than "stuck". The band is sized relative to the element it's drawn on (not a fixed pixel width),
 * so it looks right whether it's a small chip or a full-width photo placeholder, and it eases in
 * and out at the edges instead of popping. Join a [ShimmerHost] to sweep several of these in sync.
 */
@Composable
fun Modifier.appShimmer(): Modifier {
    val base = MaterialTheme.colorScheme.surfaceContainerHigh
    val highlight = MaterialTheme.colorScheme.surfaceContainerHighest
    val progress = LocalShimmerProgress.current ?: rememberShimmerProgress()

    return this.drawWithCache {
        // The band travels a bit past the element's own bounds on each side so the sheen eases in
        // and out at the edges, and cuts across on a slight diagonal for a less mechanical wipe
        // than a flat horizontal pass.
        val band = size.width * 0.5f + size.height
        val travel = size.width + band * 2f
        val x = -band + progress * travel
        val brush =
            Brush.linearGradient(
                colors = listOf(base, base, highlight, base, base),
                start = Offset(x, 0f),
                end = Offset(x + band, size.height),
            )
        onDrawBehind { drawRect(brush) }
    }
}

/** A thin rounded-rect placeholder standing in for a line of text. */
@Composable
fun AppSkeletonLine(
    width: Dp,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    shape: Shape = AppRadius.chip,
) {
    Box(modifier = modifier.size(width = width, height = height).clip(shape).appShimmer())
}
