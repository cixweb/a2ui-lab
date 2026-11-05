package io.github.cixweb.a2uilab.ui.components

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/** The phone every screen preview and screenshot uses, so all images share one size. */
const val SCREEN_DEVICE = "spec:width=390dp,height=844dp,dpi=440"

/** A whole screen, in light and dark: annotate each screen preview with it. */
@Preview(name = "Light", device = SCREEN_DEVICE, showBackground = true)
@Preview(
    name = "Dark",
    device = SCREEN_DEVICE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
annotation class ScreenPreviews
