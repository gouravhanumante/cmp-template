package com.boundless.template.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * The app's single colour identity. Every role (surfaces, accents, etc.) is built from this one
 * palette, so a screen never needs to know which one is active — only hue changes, never meaning.
 */
enum class AppPalette {
    INK,
    ;

    companion object {
        val Default = INK
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

/**
 * A deep, slightly desaturated navy. Was the palette's lead; now sits as Forest's counterpoint and
 * trust anchor — the same "meet a stranger safely" signal, one step quieter than the green.
 */
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

/**
 * A deep, muted forest green. Leads the palette: the product's hardest job is making "meet a
 * stranger in a public place" feel safe, and a settled green reads as safety without the
 * coldness of blue-as-corporate, with the ink navy as its quieter trust anchor.
 */
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

/** The app's one lead hue and its counterpoint. */
private fun AppPalette.hues(): Pair<Hue, Hue> = Forest to InkBlue

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

fun AppPalette.accents(): List<AppAccent> = InkAccents

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

private val WarmAccent = Apricot.accent(Apricot.base, Apricot.mid, Apricot.container)

private val InkAccents = AppPalette.INK.hues().let { (l, c) -> accentsOf(l, c) } + WarmAccent

fun AppPalette.colorScheme(): ColorScheme = InkScheme

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

/**
 * Warm paper neutrals under the forest lead. The pairing is the whole point: the green carries
 * trust, and the warm ground stops that trust reading as a bank dashboard — this product is for
 * people who are lonely, so the page should feel like paper rather than software.
 *
 * Neutrals are absolute rather than hue-tinted, because tinting them would spread the lead hue
 * across every surface again, which is what made the previous palette feel drenched in colour.
 */
private val InkScheme =
    AppPalette.INK.hues().let { (l, c) ->
        schemeOf(
            l,
            c,
            background = Color(0xFFF6F5F2),
            surfaceContainer = Color(0xFFEFEDE7),
            surfaceContainerHigh = Color(0xFFE8E5DD),
            surfaceContainerHighest = Color(0xFFE1DDD3),
            surfaceVariant = Color(0xFFEFEDE7),
            onSurfaceVariant = Color(0xFF6B6A66),
            outline = Color(0xFFBDB9B0),
            outlineVariant = Color(0xFFE4E2DB),
        )
    }
