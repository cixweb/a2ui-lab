package io.github.cixweb.a2uilab.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.cixweb.a2uilab.R

// Inter for text, JetBrains Mono for code, keys and bytes. Both fonts are bundled
// (SIL Open Font License, see licenses/), so previews and screenshots match the device.

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
)

private fun inter(weight: FontWeight, size: Int, line: Int, tracking: Double = 0.0) = TextStyle(
    fontFamily = Inter,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp,
)

internal val AppTypographyM3 = Typography(
    displayLarge = inter(FontWeight.Bold, 48, 56, -0.5),
    displayMedium = inter(FontWeight.Bold, 40, 48, -0.5),
    displaySmall = inter(FontWeight.Bold, 34, 42, -0.25),
    headlineLarge = inter(FontWeight.Bold, 30, 38, -0.25),
    headlineMedium = inter(FontWeight.Bold, 26, 34, -0.2),
    headlineSmall = inter(FontWeight.SemiBold, 22, 30, -0.1),
    titleLarge = inter(FontWeight.Bold, 20, 26, -0.2),
    titleMedium = inter(FontWeight.SemiBold, 15, 20),
    titleSmall = inter(FontWeight.SemiBold, 14, 20),
    bodyLarge = inter(FontWeight.Normal, 16, 24),
    bodyMedium = inter(FontWeight.Normal, 14, 20),
    bodySmall = inter(FontWeight.Normal, 13, 18),
    labelLarge = inter(FontWeight.SemiBold, 15, 20),
    labelMedium = inter(FontWeight.Medium, 13, 18),
    labelSmall = inter(FontWeight.Medium, 12, 16, 0.2),
)

/** Monospace styles Material 3 does not define. */
@Immutable
data class AppTypography(
    val code: TextStyle = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    ),
    val codeLabel: TextStyle = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)
