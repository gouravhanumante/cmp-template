package com.boundless.template.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * The colour themes a person can switch between (ThemeSettings). Every role is built from the active
 * one, so a screen never needs to know which is on: only hue changes, never meaning. Danger red and
 * text colours stay the same in every theme.
 */
enum class AppPalette {
    /** Deep indigo with a coral counterpoint. */
    INDIGO,

    /** Petrol teal with amber. */
    TEAL,

    /** Burnt terracotta with navy. */
    TERRACOTTA,

    /** Muted forest green with navy. */
    FOREST,
    ;

    companion object {
        val Default = INDIGO
    }
}

/**
 * One colour family, sampled at the steps a UI actually needs. Everything visible is built from
 * two of these, which is what keeps a screen from turning into a fruit salad.
 *
 * [mid] and darker all clear 3:1 against white, and [dark]/[darkest] clear 4.5:1, so any
 * gradient built from them can carry white text and icons.
 */
private data class Hue(
    val container: Color,
    /** A second pale step, so two accents from one hue never render identically. */
    val containerStrong: Color,
    val light: Color,
    val base: Color,
    val mid: Color,
    /** The Material role colour. */
    val deep: Color,
    val dark: Color,
    val darkest: Color,
)

/** A deep, slightly desaturated navy: the quiet counterpoint for the warmer themes. */
private val InkBlue =
    Hue(
        container = Color(0xFFDDE3EC),
        containerStrong = Color(0xFFC3CEDD),
        light = Color(0xFF6B84A8),
        base = Color(0xFF4A6690),
        mid = Color(0xFF3A5477),
        deep = Color(0xFF2B3F5E),
        dark = Color(0xFF1E2E45),
        darkest = Color(0xFF141E2E),
    )

/** A deep, muted forest green. */
private val Forest =
    Hue(
        container = Color(0xFFDCEAE2),
        containerStrong = Color(0xFFC2DDCE),
        light = Color(0xFF6FA98C),
        base = Color(0xFF4D8F71),
        mid = Color(0xFF35735A),
        deep = Color(0xFF1F4D3A),
        dark = Color(0xFF163828),
        darkest = Color(0xFF0E2A1E),
    )

private val Apricot =
    Hue(
        container = Color(0xFFF8E2D5),
        containerStrong = Color(0xFFF2C8B4),
        light = Color(0xFFC97858),
        base = Color(0xFFB65D3B),
        mid = Color(0xFF98472C),
        deep = Color(0xFF76331F),
        dark = Color(0xFF572416),
        darkest = Color(0xFF3B170E),
    )

private val Indigo =
    Hue(
        container = Color(0xFFE4E5FB),
        containerStrong = Color(0xFFCACDF5),
        light = Color(0xFF8187E2),
        base = Color(0xFF5C61D4),
        mid = Color(0xFF4A4FC0),
        deep = Color(0xFF3B3FA6),
        dark = Color(0xFF2C2F7E),
        darkest = Color(0xFF1D1F55),
    )

private val Coral =
    Hue(
        container = Color(0xFFFCE4DF),
        containerStrong = Color(0xFFF8CABF),
        light = Color(0xFFF08A76),
        base = Color(0xFFDD624B),
        mid = Color(0xFFC24A35),
        deep = Color(0xFF9E3A28),
        dark = Color(0xFF74291C),
        darkest = Color(0xFF4D1B12),
    )

private val Teal =
    Hue(
        container = Color(0xFFD7ECEE),
        containerStrong = Color(0xFFB5DCE0),
        light = Color(0xFF5FA7AF),
        base = Color(0xFF2F8A94),
        mid = Color(0xFF23707A),
        deep = Color(0xFF1B5960),
        dark = Color(0xFF134247),
        darkest = Color(0xFF0C2C30),
    )

private val Amber =
    Hue(
        container = Color(0xFFFBECCF),
        containerStrong = Color(0xFFF6DBA6),
        light = Color(0xFFE3A33F),
        base = Color(0xFFC9861E),
        mid = Color(0xFFA86A12),
        deep = Color(0xFF85520C),
        dark = Color(0xFF603B08),
        darkest = Color(0xFF3F2705),
    )

private val Terracotta =
    Hue(
        container = Color(0xFFF6E0D6),
        containerStrong = Color(0xFFEEC7B5),
        light = Color(0xFFD4805C),
        base = Color(0xFFBF6440),
        mid = Color(0xFFA0502F),
        deep = Color(0xFF803F24),
        dark = Color(0xFF5E2E1A),
        darkest = Color(0xFF3E1E11),
    )

/** Neutral greys for one theme: cool under blue leads, warm under warm ones. */
private data class Neutrals(
    val background: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
)

private val CoolNeutrals =
    Neutrals(
        background = Color(0xFFF6F6FA),
        surfaceContainer = Color(0xFFEEEEF5),
        surfaceContainerHigh = Color(0xFFE7E7F0),
        surfaceContainerHighest = Color(0xFFDFDFEA),
        onSurfaceVariant = Color(0xFF62637A),
        outline = Color(0xFFB8B9CC),
        outlineVariant = Color(0xFFE2E2EC),
    )

private val MistNeutrals =
    Neutrals(
        background = Color(0xFFF3F7F7),
        surfaceContainer = Color(0xFFEAF1F1),
        surfaceContainerHigh = Color(0xFFE2EBEB),
        surfaceContainerHighest = Color(0xFFD9E4E4),
        onSurfaceVariant = Color(0xFF5F6E70),
        outline = Color(0xFFB2C2C3),
        outlineVariant = Color(0xFFDDE7E7),
    )

private val PaperNeutrals =
    Neutrals(
        background = Color(0xFFF6F5F2),
        surfaceContainer = Color(0xFFEFEDE7),
        surfaceContainerHigh = Color(0xFFE8E5DD),
        surfaceContainerHighest = Color(0xFFE1DDD3),
        onSurfaceVariant = Color(0xFF6B6A66),
        outline = Color(0xFFBDB9B0),
        outlineVariant = Color(0xFFE4E2DB),
    )

/** A theme: its lead hue, the counterpoint, one extra accent for variety, and its neutrals. */
private class Theme(
    val lead: Hue,
    val counter: Hue,
    val extra: Hue,
    val neutrals: Neutrals,
)

private fun AppPalette.theme(): Theme =
    when (this) {
        AppPalette.INDIGO -> Theme(Indigo, Coral, Teal, CoolNeutrals)
        AppPalette.TEAL -> Theme(Teal, Amber, Coral, MistNeutrals)
        AppPalette.TERRACOTTA -> Theme(Terracotta, InkBlue, Teal, PaperNeutrals)
        AppPalette.FOREST -> Theme(Forest, InkBlue, Apricot, PaperNeutrals)
    }

/**
 * A palette accent in two strengths. [brush] is loud and is meant to be spent once per screen on
 * the thing that matters; [soft] carries the same hue quietly for everything else. Bright colour
 * only signals importance while it stays rare.
 */
data class AppAccent(
    val start: Color,
    val end: Color,
    /** Pale fill for low-priority surfaces. */
    val soft: Color,
    /** Text colour on [soft]. */
    val onSoft: Color,
    /** Icon and detail colour on [soft]. */
    val mark: Color,
) {
    val content: Color = Color.White
    val brush: Brush = Brush.linearGradient(listOf(start, end))
}

@Composable
fun appAccents(): List<AppAccent> = LocalAppPalette.current.accents()

fun AppPalette.accents(): List<AppAccent> = accentsByPalette.getValue(this)

/**
 * Two hues, each at two well-separated depths — a vivid one and a deep one — so a grid of four
 * cards shows real variation without ever leaving the palette's own two colours.
 */
private fun accentsOf(
    lead: Hue,
    counter: Hue,
): List<AppAccent> =
    listOf(
        lead.accent(lead.deep, lead.darkest, lead.container),
        counter.accent(counter.deep, counter.darkest, counter.container),
        lead.accent(lead.base, lead.mid, lead.containerStrong),
        counter.accent(counter.base, counter.mid, counter.containerStrong),
    )

private fun Hue.accent(
    start: Color,
    end: Color,
    soft: Color,
): AppAccent = AppAccent(start = start, end = end, soft = soft, onSoft = darkest, mark = deep)

private val accentsByPalette: Map<AppPalette, List<AppAccent>> =
    AppPalette.entries.associateWith { palette ->
        val theme = palette.theme()
        accentsOf(theme.lead, theme.counter) + theme.extra.accent(theme.extra.base, theme.extra.mid, theme.extra.container)
    }

fun AppPalette.colorScheme(): ColorScheme = schemesByPalette.getValue(this)

/** Neutrals are the lead hue mixed into white, so no grey is ever foreign to the palette. */
private fun Hue.tint(amount: Float): Color = lerp(Color.White, deep, amount)

/**
 * Builds a light scheme from two hues. Tertiary deliberately stays inside the lead family: a
 * genuine third accent is what stops a colour scheme reading as deliberate.
 *
 * Neutrals default to the lead hue tinted into white, but a palette can override them with an
 * absolute warm/cool neutral instead — useful when the brand hue should stay rare rather than
 * wash over every background and border.
 */
private fun schemeOf(
    lead: Hue,
    counter: Hue,
    background: Color = lead.tint(0.035f),
    surfaceContainer: Color = lead.tint(0.07f),
    surfaceContainerHigh: Color = lead.tint(0.10f),
    surfaceContainerHighest: Color = lead.tint(0.14f),
    surfaceVariant: Color = lead.tint(0.11f),
    onSurfaceVariant: Color = lerp(Slate, lead.deep, 0.25f),
    outline: Color = lead.tint(0.42f),
    outlineVariant: Color = lead.tint(0.18f),
): ColorScheme =
    lightColorScheme(
        primary = lead.deep,
        onPrimary = Color.White,
        primaryContainer = lead.container,
        onPrimaryContainer = lead.darkest,
        secondary = counter.deep,
        onSecondary = Color.White,
        secondaryContainer = counter.container,
        onSecondaryContainer = counter.darkest,
        tertiary = lead.mid,
        onTertiary = Color.White,
        tertiaryContainer = lead.container,
        onTertiaryContainer = lead.darkest,
        background = background,
        onBackground = Ink,
        surface = Color.White,
        onSurface = Ink,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        // Navigation bars, sheets and menus read these; leaving them unset falls back to
        // Material's stock grey and the app stops matching its own palette.
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = background,
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
        outline = outline,
        // Hairline borders replace elevation, so this is used on every card.
        outlineVariant = outlineVariant,
        inverseSurface = Ink,
        inverseOnSurface = background,
        inversePrimary = lead.light,
        error = Danger,
        onError = Color.White,
        errorContainer = DangerLight,
        onErrorContainer = OnDangerLight,
    )

// Neutrals are absolute rather than hue-tinted, so the lead colour stays rare instead of washing
// over every background and border.
private val schemesByPalette: Map<AppPalette, ColorScheme> =
    AppPalette.entries.associateWith { palette ->
        val theme = palette.theme()
        val n = theme.neutrals
        schemeOf(
            theme.lead,
            theme.counter,
            background = n.background,
            surfaceContainer = n.surfaceContainer,
            surfaceContainerHigh = n.surfaceContainerHigh,
            surfaceContainerHighest = n.surfaceContainerHighest,
            surfaceVariant = n.surfaceContainer,
            onSurfaceVariant = n.onSurfaceVariant,
            outline = n.outline,
            outlineVariant = n.outlineVariant,
        )
    }
