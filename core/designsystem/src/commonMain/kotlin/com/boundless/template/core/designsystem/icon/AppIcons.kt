package com.boundless.template.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Hand-drawn icon set. Compose Multiplatform 1.11 does not publish `material-icons-core`, so
 * rather than pull an incompatible artifact these are defined directly as vectors.
 * Stroke-based and uniform, so they read as one family.
 */
object AppIcons {
    val Search: ImageVector by lazy {
        stroked("Search") {
            // Circle approximated with two arcs, then the handle.
            moveTo(11f, 4f)
            arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 14f)
            arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -14f)
            moveTo(16.2f, 16.2f)
            lineTo(21f, 21f)
        }
    }

    val Calendar: ImageVector by lazy {
        stroked("Calendar") {
            moveTo(4f, 6f)
            lineTo(20f, 6f)
            lineTo(20f, 20f)
            lineTo(4f, 20f)
            close()
            moveTo(4f, 10f)
            lineTo(20f, 10f)
            moveTo(8f, 3f)
            lineTo(8f, 7f)
            moveTo(16f, 3f)
            lineTo(16f, 7f)
        }
    }

    val Person: ImageVector by lazy {
        stroked("Person") {
            moveTo(12f, 12.5f)
            arcToRelative(4.25f, 4.25f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -8.5f)
            arcToRelative(4.25f, 4.25f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 8.5f)
            close()
            moveTo(4.5f, 19.5f)
            curveTo(4.5f, 15.9f, 7.7f, 15f, 12f, 15f)
            curveTo(16.3f, 15f, 19.5f, 15.9f, 19.5f, 19.5f)
        }
    }

    val Wallet: ImageVector by lazy {
        stroked("Wallet") {
            moveTo(3f, 7f)
            lineTo(19f, 7f)
            lineTo(19f, 19f)
            lineTo(3f, 19f)
            close()
            moveTo(3f, 7f)
            lineTo(3f, 5f)
            lineTo(16f, 5f)
            moveTo(15f, 13f)
            lineTo(19f, 13f)
        }
    }

    val Home: ImageVector by lazy {
        stroked("Home") {
            moveTo(4f, 11f)
            lineTo(12f, 4f)
            lineTo(20f, 11f)
            moveTo(6f, 10f)
            lineTo(6f, 20f)
            lineTo(18f, 20f)
            lineTo(18f, 10f)
        }
    }

    val Check: ImageVector by lazy {
        stroked("Check") {
            moveTo(5f, 12.5f)
            lineTo(10f, 17.5f)
            lineTo(19f, 6.5f)
        }
    }

    val Shield: ImageVector by lazy {
        stroked("Shield") {
            moveTo(12f, 3f)
            lineTo(20f, 6f)
            curveTo(20f, 14f, 16.5f, 19f, 12f, 21f)
            curveTo(7.5f, 19f, 4f, 14f, 4f, 6f)
            close()
        }
    }

    val Pin: ImageVector by lazy {
        stroked("Pin") {
            moveTo(12f, 21f)
            curveTo(12f, 21f, 19f, 14.5f, 19f, 10f)
            arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = false, -14f, 0f)
            curveTo(5f, 14.5f, 12f, 21f, 12f, 21f)
            close()
            moveTo(12f, 10f)
            arcToRelative(2.2f, 2.2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0.1f, 0f)
        }
    }

    val Clock: ImageVector by lazy {
        stroked("Clock") {
            moveTo(12f, 3f)
            arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 18f)
            arcToRelative(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -18f)
            moveTo(12f, 7f)
            lineTo(12f, 12.5f)
            lineTo(16f, 14.5f)
        }
    }

    val Refresh: ImageVector by lazy {
        stroked("Refresh") {
            moveTo(20f, 11f)
            arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = false, -14f, -5f)
            lineTo(4f, 3f)
            moveTo(4f, 3f)
            lineTo(4f, 8f)
            lineTo(9f, 8f)
            moveTo(4f, 13f)
            arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = false, 14f, 5f)
            lineTo(20f, 21f)
            moveTo(20f, 21f)
            lineTo(20f, 16f)
            lineTo(15f, 16f)
        }
    }

    val Chevron: ImageVector by lazy {
        stroked("Chevron") {
            moveTo(9f, 5f)
            lineTo(16f, 12f)
            lineTo(9f, 19f)
        }
    }

    val Back: ImageVector by lazy {
        stroked("Back") {
            moveTo(15f, 5f)
            lineTo(8f, 12f)
            lineTo(15f, 19f)
        }
    }

    val Close: ImageVector by lazy {
        stroked("Close") {
            moveTo(6f, 6f)
            lineTo(18f, 18f)
            moveTo(18f, 6f)
            lineTo(6f, 18f)
        }
    }

    val Plus: ImageVector by lazy {
        stroked("Plus") {
            moveTo(12f, 5f)
            lineTo(12f, 19f)
            moveTo(5f, 12f)
            lineTo(19f, 12f)
        }
    }

    val Coffee: ImageVector by lazy {
        stroked("Coffee") {
            moveTo(4f, 8f)
            lineTo(17f, 8f)
            lineTo(17f, 15f)
            arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4f, 4f)
            lineTo(8f, 19f)
            arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4f, -4f)
            close()
            moveTo(17f, 10f)
            lineTo(19f, 10f)
            arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 4f)
            lineTo(17f, 14f)
            moveTo(8f, 5f)
            lineTo(8f, 7f)
            moveTo(12f, 5f)
            lineTo(12f, 7f)
        }
    }

    val Movie: ImageVector by lazy {
        stroked("Movie") {
            moveTo(4f, 6f)
            lineTo(20f, 6f)
            lineTo(20f, 18f)
            lineTo(4f, 18f)
            close()
            moveTo(4f, 10f)
            lineTo(20f, 10f)
            moveTo(8f, 6f)
            lineTo(8f, 10f)
            moveTo(12f, 6f)
            lineTo(12f, 10f)
            moveTo(16f, 6f)
            lineTo(16f, 10f)
        }
    }

    val Bike: ImageVector by lazy {
        stroked("Bike") {
            moveTo(8f, 18f)
            arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -6f)
            arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 6f)
            moveTo(22f, 18f)
            arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -6f)
            arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 6f)
            moveTo(8f, 18f)
            lineTo(11f, 10f)
            lineTo(14f, 18f)
            lineTo(19f, 18f)
            moveTo(10f, 10f)
            lineTo(14f, 10f)
            moveTo(13f, 7f)
            lineTo(15f, 7f)
            moveTo(14f, 10f)
            lineTo(17f, 14f)
        }
    }

    val Car: ImageVector by lazy {
        stroked("Car") {
            moveTo(5f, 16f)
            lineTo(7f, 9f)
            lineTo(17f, 9f)
            lineTo(19f, 16f)
            close()
            moveTo(4f, 16f)
            lineTo(20f, 16f)
            moveTo(7f, 19f)
            lineTo(7f, 17f)
            moveTo(17f, 19f)
            lineTo(17f, 17f)
            moveTo(8f, 12f)
            lineTo(16f, 12f)
        }
    }

    val Utensils: ImageVector by lazy {
        stroked("Utensils") {
            moveTo(7f, 4f)
            lineTo(7f, 20f)
            moveTo(4f, 4f)
            lineTo(4f, 10f)
            arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6f, 0f)
            lineTo(10f, 4f)
            moveTo(16f, 4f)
            lineTo(16f, 20f)
            moveTo(16f, 4f)
            curveTo(20f, 7f, 20f, 11f, 16f, 12f)
        }
    }

    val Map: ImageVector by lazy {
        stroked("Map") {
            moveTo(4f, 5f)
            lineTo(9f, 3f)
            lineTo(15f, 5f)
            lineTo(20f, 3f)
            lineTo(20f, 19f)
            lineTo(15f, 21f)
            lineTo(9f, 19f)
            lineTo(4f, 21f)
            close()
            moveTo(9f, 3f)
            lineTo(9f, 19f)
            moveTo(15f, 5f)
            lineTo(15f, 21f)
        }
    }

    val Sparkle: ImageVector by lazy {
        stroked("Sparkle") {
            // Four points, each a concave curve rather than a straight edge — reads as a soft
            // sparkle instead of a sharp diamond.
            moveTo(12f, 3f)
            curveTo(12.4f, 7.4f, 13.4f, 9.5f, 15f, 10f)
            curveTo(18f, 10.9f, 19.6f, 11.1f, 20.5f, 11.5f)
            curveTo(19.6f, 11.9f, 18f, 12.1f, 15f, 13f)
            curveTo(13.4f, 13.5f, 12.4f, 15.6f, 12f, 20f)
            curveTo(11.6f, 15.6f, 10.6f, 13.5f, 9f, 13f)
            curveTo(6f, 12.1f, 4.4f, 11.9f, 3.5f, 11.5f)
            curveTo(4.4f, 11.1f, 6f, 10.9f, 9f, 10f)
            curveTo(10.6f, 9.5f, 11.6f, 7.4f, 12f, 3f)
            close()
        }
    }

    val Star: ImageVector by lazy {
        stroked("Star") {
            moveTo(12f, 2.5f)
            lineTo(14.9f, 8.6f)
            lineTo(21.5f, 9.5f)
            lineTo(16.8f, 14.1f)
            lineTo(17.9f, 20.7f)
            lineTo(12f, 17.6f)
            lineTo(6.1f, 20.7f)
            lineTo(7.2f, 14.1f)
            lineTo(2.5f, 9.5f)
            lineTo(9.1f, 8.6f)
            close()
        }
    }

    val ShoppingBag: ImageVector by lazy {
        stroked("ShoppingBag") {
            moveTo(5f, 8f)
            lineTo(19f, 8f)
            lineTo(18f, 20f)
            lineTo(6f, 20f)
            close()
            moveTo(9f, 8f)
            arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 0f)
        }
    }

    val Walk: ImageVector by lazy {
        stroked("Walk") {
            moveTo(13f, 5f)
            arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
            moveTo(11f, 8f)
            lineTo(10f, 13f)
            lineTo(6f, 18f)
            moveTo(10f, 13f)
            lineTo(15f, 16f)
            lineTo(18f, 20f)
            moveTo(10f, 10f)
            lineTo(15f, 11f)
        }
    }

    val Gamepad: ImageVector by lazy {
        stroked("Gamepad") {
            moveTo(7f, 9f)
            curveTo(5f, 9f, 4f, 10.4f, 3.5f, 13.2f)
            lineTo(3f, 16f)
            curveTo(2.5f, 18.8f, 4.8f, 20.5f, 6.7f, 18.7f)
            lineTo(8.5f, 17f)
            lineTo(15.5f, 17f)
            lineTo(17.3f, 18.7f)
            curveTo(19.2f, 20.5f, 21.5f, 18.8f, 21f, 16f)
            lineTo(20.5f, 13.2f)
            curveTo(20f, 10.4f, 19f, 9f, 17f, 9f)
            close()
            moveTo(7f, 13f)
            lineTo(11f, 13f)
            moveTo(9f, 11f)
            lineTo(9f, 15f)
            moveTo(16.5f, 12.5f)
            lineTo(16.5f, 12.5f)
            moveTo(18.5f, 14.5f)
            lineTo(18.5f, 14.5f)
        }
    }

    val Palette: ImageVector by lazy {
        stroked("Palette") {
            moveTo(12f, 3f)
            curveTo(6.5f, 3f, 3f, 6.7f, 3f, 12f)
            curveTo(3f, 17.3f, 6.7f, 21f, 12f, 21f)
            curveTo(14.2f, 21f, 15f, 19.8f, 14.4f, 18.5f)
            curveTo(13.8f, 17.2f, 14.7f, 16f, 16.2f, 16f)
            lineTo(18f, 16f)
            curveTo(20.1f, 16f, 21f, 14.4f, 20.8f, 12.2f)
            curveTo(20.4f, 7f, 16.6f, 3f, 12f, 3f)
            close()
            moveTo(8f, 10f)
            lineTo(8f, 10f)
            moveTo(11f, 7.5f)
            lineTo(11f, 7.5f)
            moveTo(15f, 8.5f)
            lineTo(15f, 8.5f)
            moveTo(7f, 14f)
            lineTo(7f, 14f)
        }
    }

    val Ticket: ImageVector by lazy {
        stroked("Ticket") {
            moveTo(4f, 7f)
            lineTo(20f, 7f)
            lineTo(20f, 11f)
            curveTo(18.5f, 11f, 18.5f, 13f, 20f, 13f)
            lineTo(20f, 17f)
            lineTo(4f, 17f)
            lineTo(4f, 13f)
            curveTo(5.5f, 13f, 5.5f, 11f, 4f, 11f)
            close()
            moveTo(12f, 8f)
            lineTo(12f, 16f)
        }
    }

    val Heart: ImageVector by lazy {
        stroked("Heart") {
            moveTo(12f, 20f)
            curveTo(12f, 20f, 4f, 14.5f, 4f, 9f)
            curveTo(4f, 6.2f, 6.2f, 4f, 9f, 4f)
            curveTo(10.3f, 4f, 11.4f, 4.6f, 12f, 5.6f)
            curveTo(12.6f, 4.6f, 13.7f, 4f, 15f, 4f)
            curveTo(17.8f, 4f, 20f, 6.2f, 20f, 9f)
            curveTo(20f, 14.5f, 12f, 20f, 12f, 20f)
            close()
        }
    }

    /** Saved. Same outline as [Heart] so the two read as one shape filling in, not two icons. */
    val HeartFilled: ImageVector by lazy {
        filled("HeartFilled") {
            moveTo(12f, 20f)
            curveTo(12f, 20f, 4f, 14.5f, 4f, 9f)
            curveTo(4f, 6.2f, 6.2f, 4f, 9f, 4f)
            curveTo(10.3f, 4f, 11.4f, 4.6f, 12f, 5.6f)
            curveTo(12.6f, 4.6f, 13.7f, 4f, 15f, 4f)
            curveTo(17.8f, 4f, 20f, 6.2f, 20f, 9f)
            curveTo(20f, 14.5f, 12f, 20f, 12f, 20f)
            close()
        }
    }

    val ArrowUp: ImageVector by lazy {
        stroked("ArrowUp") {
            moveTo(12f, 19f)
            lineTo(12f, 5f)
            moveTo(6f, 11f)
            lineTo(12f, 5f)
            lineTo(18f, 11f)
        }
    }

    val Flag: ImageVector by lazy {
        stroked("Flag") {
            moveTo(6f, 21f)
            lineTo(6f, 4f)
            moveTo(6f, 4f)
            lineTo(18f, 4f)
            lineTo(14.5f, 9f)
            lineTo(18f, 14f)
            lineTo(6f, 14f)
        }
    }
}

private fun stroked(
    name: String,
    pathBuilder: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit,
): ImageVector =
    ImageVector
        .Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = pathBuilder,
        ).build()

/** For the handful of icons that carry an on/off state, where a fill is the clearest "on". */
private fun filled(
    name: String,
    pathBuilder: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit,
): ImageVector =
    ImageVector
        .Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).path(
            fill = SolidColor(Color.Black),
            pathBuilder = pathBuilder,
        ).build()
