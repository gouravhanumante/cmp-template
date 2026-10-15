package com.boundless.template.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val angle: Float,
    val distance: Float,
    val radius: Float,
    val color: Color,
)

/** A brief outward burst of dots from the centre \u2014 reserved for a genuine success, never a retry. */
@Composable
fun AppConfettiBurst(modifier: Modifier = Modifier) {
    val colors = with(MaterialTheme.colorScheme) { listOf(primary, secondary, tertiary) }
    val particles =
        remember {
            List(16) {
                ConfettiParticle(
                    angle = Random.nextFloat() * 360f,
                    distance = 60f + Random.nextFloat() * 60f,
                    radius = 3f + Random.nextFloat() * 3f,
                    color = colors[it % colors.size],
                )
            }
        }
    val progress by animateFloatAsState(targetValue = 1f, animationSpec = tween(700, easing = LinearEasing), label = "confetti")

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        particles.forEach { particle ->
            val radians = particle.angle * (kotlin.math.PI / 180f)
            val travelled = particle.distance * progress
            val point =
                Offset(
                    x = center.x + cos(radians).toFloat() * travelled,
                    y = center.y + sin(radians).toFloat() * travelled,
                )
            drawCircle(color = particle.color.copy(alpha = 1f - progress), radius = particle.radius, center = point)
        }
    }
}
