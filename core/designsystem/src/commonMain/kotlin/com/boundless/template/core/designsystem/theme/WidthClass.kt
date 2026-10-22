package com.boundless.template.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Material 3 window widths: phone, tablet or narrow window, wide screen. */
enum class WidthClass { COMPACT, MEDIUM, EXPANDED }

object Breakpoints {
    val medium = 600.dp
    val expanded = 840.dp

    /** Content area wide enough to put two cards side by side. */
    val twoColumns = 720.dp

    /** Longest comfortable line for forms and text pages. */
    val readable = 720.dp

    /** Right-hand panel for forms and details on wide screens. */
    val sidePanel = 420.dp
}

fun widthClassOf(width: Dp): WidthClass =
    when {
        width < Breakpoints.medium -> WidthClass.COMPACT
        width < Breakpoints.expanded -> WidthClass.MEDIUM
        else -> WidthClass.EXPANDED
    }

@Composable
fun windowWidthClass(): WidthClass = widthClassOf(with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() })
