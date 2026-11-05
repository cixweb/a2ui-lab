package io.github.cixweb.a2uilab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.cixweb.a2uilab.ui.LabRoute
import io.github.cixweb.a2uilab.ui.theme.AppTheme

/** The only activity: shows the lab screen in the app theme. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                LabRoute()
            }
        }
    }
}
