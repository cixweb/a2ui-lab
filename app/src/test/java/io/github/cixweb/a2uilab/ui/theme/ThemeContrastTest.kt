package io.github.cixweb.a2uilab.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test

/** Text on every container the components use must reach WCAG AA (4.5:1). */
class ThemeContrastTest {
    private fun contrast(a: Color, b: Color): Double {
        val (light, dark) = listOf(a.luminance(), b.luminance()).sortedDescending()
        return (light + 0.05) / (dark + 0.05)
    }

    private fun pairs(scheme: ColorScheme, app: AppColors) = mapOf(
        "onBackground" to (scheme.onBackground to scheme.background),
        "onSurfaceVariant on background" to (scheme.onSurfaceVariant to scheme.background),
        "onSurface on card" to (scheme.onSurface to scheme.surfaceContainer),
        "onSurfaceVariant on card" to (scheme.onSurfaceVariant to scheme.surfaceContainer),
        "onPrimary" to (scheme.onPrimary to scheme.primary),
        "primary on primaryContainer" to (scheme.primary to scheme.primaryContainer),
        "onPrimaryContainer" to (scheme.onPrimaryContainer to scheme.primaryContainer),
        "onSecondaryContainer" to (scheme.onSecondaryContainer to scheme.secondaryContainer),
        "onErrorContainer" to (scheme.onErrorContainer to scheme.errorContainer),
        "onSuccessContainer" to (app.onSuccessContainer to app.successContainer),
        "onWarningContainer" to (app.onWarningContainer to app.warningContainer),
    )

    private fun assertReadable(scheme: ColorScheme, app: AppColors) {
        for ((name, pair) in pairs(scheme, app)) {
            val ratio = contrast(pair.first, pair.second)
            assertTrue("$name is $ratio:1", ratio >= 4.5)
        }
    }

    @Test
    fun lightThemeIsReadable() = assertReadable(LightColors, LightAppColors)

    @Test
    fun darkThemeIsReadable() = assertReadable(DarkColors, DarkAppColors)
}
