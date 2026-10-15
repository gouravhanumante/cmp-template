package com.boundless.template.core.designsystem.theme

import androidx.compose.ui.unit.dp

/** Multiples of 4. Values outside this scale are a bug, not a judgement call. */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val smd = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp

    /** Between a heading and the content it labels — deliberately tighter than [section]. */
    val group = 8.dp

    /** Between unrelated sections. The gap is what tells them apart. */
    val section = 24.dp

    /** Left/right screen margin. */
    val screen = 16.dp
}

/** Fixed element sizes, so no screen invents its own avatar or icon dimension. */
object AppSize {
    val avatarSm = 40.dp
    val avatarMd = 48.dp
    val avatarLg = 64.dp
    val hero = 220.dp

    /** Discovery list card photo — big enough to be photo-forward without eating the whole card. */
    val photoMd = 96.dp

    /** Full-bleed profile photo at the top of a detail screen. */
    val photoHero = 340.dp

    val iconSm = 18.dp
    val iconMd = 24.dp
    val iconLg = 30.dp

    val tile = 64.dp

    /** Maximum width for compact feedback dialogs on larger screens. */
    val modalMaxWidth = 360.dp

    /** Dashboard shortcut tile. Wider than tall so four fit without dominating the screen. */
    val tileHeight = 112.dp

    /** WCAG 2.2 asks for 24dp; Material and Apple both ask for more. */
    val minTouch = 48.dp
    val rowHeight = 56.dp
    val buttonHeight = 52.dp
}
