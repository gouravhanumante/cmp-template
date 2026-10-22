package com.boundless.template.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.Font

/**
 * Montserrat (SIL OFL, core/resources/FONT-LICENSE-Montserrat.txt), one static file per weight: Android's
 * Compose resource font loader fails to read variable fonts and silently falls back to the system font.
 */
@Composable
private fun appFontFamily(): FontFamily =
    FontFamily(
        Font(Res.font.montserrat_regular, FontWeight.Normal),
        Font(Res.font.montserrat_medium, FontWeight.Medium),
        Font(Res.font.montserrat_semibold, FontWeight.SemiBold),
        Font(Res.font.montserrat_bold, FontWeight.Bold),
    )

@Composable
internal fun appTypography(): Typography {
    val family = appFontFamily()
    fun style(size: TextUnit, height: TextUnit, weight: FontWeight = FontWeight.Normal) =
        TextStyle(fontFamily = family, fontSize = size, lineHeight = height, fontWeight = weight, letterSpacing = 0.sp)
    return Typography(
        displayLarge = style(48.sp, 56.sp, FontWeight.Bold),
        displayMedium = style(40.sp, 48.sp, FontWeight.Bold),
        displaySmall = style(32.sp, 42.sp, FontWeight.Bold),
        headlineLarge = style(30.sp, 38.sp, FontWeight.Bold),
        headlineMedium = style(26.sp, 36.sp, FontWeight.Bold),
        headlineSmall = style(22.sp, 32.sp, FontWeight.SemiBold),
        titleLarge = style(20.sp, 30.sp, FontWeight.SemiBold),
        titleMedium = style(16.sp, 25.sp, FontWeight.SemiBold),
        titleSmall = style(14.sp, 20.sp, FontWeight.SemiBold),
        bodyLarge = style(16.sp, 25.sp),
        bodyMedium = style(14.sp, 21.sp),
        bodySmall = style(12.sp, 17.sp),
        labelLarge = style(15.sp, 21.sp, FontWeight.SemiBold),
        labelMedium = style(13.sp, 19.sp, FontWeight.SemiBold),
        labelSmall = style(11.sp, 17.sp, FontWeight.SemiBold),
    )
}
