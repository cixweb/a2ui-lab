package io.github.cixweb.a2uilab.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Signal palette. Every color of the app comes from here: screens read them through
// MaterialTheme.colorScheme and AppTheme.colors, never as literals.

internal val LightColors = lightColorScheme(
    primary = Color(0xFF3D4EDB),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE5E8FB),
    onPrimaryContainer = Color(0xFF1A2380),
    secondary = Color(0xFF5A6170),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE1E5EE),
    onSecondaryContainer = Color(0xFF12151C),
    tertiary = Color(0xFF8A4FD6),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF0E6FC),
    onTertiaryContainer = Color(0xFF3F1A70),
    background = Color(0xFFF1F3F8),
    onBackground = Color(0xFF12151C),
    surface = Color(0xFFF1F3F8),
    onSurface = Color(0xFF12151C),
    surfaceVariant = Color(0xFFE1E5EE),
    onSurfaceVariant = Color(0xFF5A6170),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFF7F8FB),
    surfaceContainerHighest = Color(0xFFE9ECF3),
    outline = Color(0xFF7A8191),
    outlineVariant = Color(0xFFE1E5EE),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF8C1D18),
)

internal val DarkColors = darkColorScheme(
    primary = Color(0xFFA9B3FF),
    onPrimary = Color(0xFF121A5C),
    primaryContainer = Color(0xFF252B4A),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFFA0A6B4),
    onSecondary = Color(0xFF1A1D26),
    secondaryContainer = Color(0xFF2A2E38),
    onSecondaryContainer = Color(0xFFE8EAF0),
    tertiary = Color(0xFFD3B5FF),
    onTertiary = Color(0xFF3F1A70),
    tertiaryContainer = Color(0xFF3A2A55),
    onTertiaryContainer = Color(0xFFF0E6FC),
    background = Color(0xFF0F1117),
    onBackground = Color(0xFFE8EAF0),
    surface = Color(0xFF0F1117),
    onSurface = Color(0xFFE8EAF0),
    surfaceVariant = Color(0xFF2A2E38),
    onSurfaceVariant = Color(0xFFA0A6B4),
    surfaceContainerLowest = Color(0xFF0B0D12),
    surfaceContainerLow = Color(0xFF15171E),
    surfaceContainer = Color(0xFF1A1D26),
    surfaceContainerHigh = Color(0xFF222632),
    surfaceContainerHighest = Color(0xFF2A2E38),
    outline = Color(0xFF6E7484),
    outlineVariant = Color(0xFF2A2E38),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF4A1A18),
    onErrorContainer = Color(0xFFF9DEDC),
)

/** Status colors Material 3 does not define, used by StatusBanner. */
@Immutable
data class AppColors(
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
)

internal val LightAppColors = AppColors(
    successContainer = Color(0xFFE3F3E8),
    onSuccessContainer = Color(0xFF155E2E),
    warningContainer = Color(0xFFFDF0D5),
    onWarningContainer = Color(0xFF7A4A00),
)

internal val DarkAppColors = AppColors(
    successContainer = Color(0xFF14301E),
    onSuccessContainer = Color(0xFF9BE0AE),
    warningContainer = Color(0xFF3A2A06),
    onWarningContainer = Color(0xFFF5C56B),
)
