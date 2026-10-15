package com.boundless.template.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.Font

/**
 * Static per-weight instances, not the original single variable file: Android's Compose resource
 * font loader (unlike iOS's CoreText) failed to read the variable font's contents at runtime
 * ("Failed to read font contents"), silently falling back to the system font — text still looked
 * fine on iOS, "normal" on Android. Instanced offline with fontTools.varLib.instancer.
 */
@Composable
private fun appFontFamily(): FontFamily =
    FontFamily(
        Font(Res.font.plus_jakarta_sans_regular, FontWeight.Normal),
        Font(Res.font.plus_jakarta_sans_medium, FontWeight.Medium),
        Font(Res.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
        Font(Res.font.plus_jakarta_sans_bold, FontWeight.Bold),
    )

@Composable
internal fun appTypography(): Typography {
    val family = appFontFamily()
    return Typography(
        // Keep headings nearly neutral while adding enough tracking for Plus Jakarta Sans to breathe.
        displaySmall =
            TextStyle(fontFamily = family, fontSize = 32.sp, lineHeight = 42.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.05.sp),
        headlineMedium =
            TextStyle(fontFamily = family, fontSize = 26.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.05.sp),
        headlineSmall =
            TextStyle(fontFamily = family, fontSize = 22.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.05.sp),
        // SemiBold rather than Medium: this is every card title, person name and price in the app,
        // and one weight step above body is what makes a card scannable.
        titleLarge = TextStyle(fontFamily = family, fontSize = 20.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.sp),
        titleMedium = TextStyle(fontFamily = family, fontSize = 16.sp, lineHeight = 25.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.sp),
        bodyLarge = TextStyle(fontFamily = family, fontSize = 16.sp, lineHeight = 25.sp, letterSpacing = 0.1.sp),
        bodyMedium = TextStyle(fontFamily = family, fontSize = 14.sp, lineHeight = 21.sp, letterSpacing = 0.1.sp),
        bodySmall = TextStyle(fontFamily = family, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.1.sp),
        labelLarge = TextStyle(fontFamily = family, fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.sp),
        // Eyebrows, badges and meta, so small text isn't only shrunken body copy.
        labelMedium = TextStyle(fontFamily = family, fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.sp),
        labelSmall = TextStyle(fontFamily = family, fontSize = 11.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.sp),
    )
}
