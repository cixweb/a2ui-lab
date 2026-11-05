package io.github.cixweb.a2uilab.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.cixweb.a2uilab.ui.theme.AppTheme

/** Every component of the kit on one screen: the reference for how they look together. */
@PreviewTest
@ScreenPreviews
@Composable
fun ComponentsScreenshot() {
    AppTheme {
        AppScaffold(
            title = "Components",
            subtitle = "The kit every screen is built from",
            action = { ActionChip(text = "Docs", onClick = {}) },
        ) {
            SectionCard(title = "Input") {
                AppTextField(value = "correct-horse-battery", onValueChange = {}, label = "Secret")
                PrimaryButton(text = "Save", onClick = {})
                SecondaryButton(text = "Reset", onClick = {})
                SwitchRow(
                    label = "Encrypt on save",
                    description = "Uses the key in the Keystore",
                    checked = true,
                    onCheckedChange = {},
                )
            }
            SectionCard(title = "Values") {
                LabeledValue(label = "File", value = "settings.pb", monospace = true)
                LabeledValue(label = "Updated", value = "Just now")
                Text(text = "Body text in a section.", style = MaterialTheme.typography.bodyMedium)
            }
            StatusBanner(text = "Check passed", status = Status.Success)
            StatusBanner(text = "Plaintext file still present", status = Status.Warning)
            StatusBanner(text = "Decryption failed", status = Status.Error)
            StatusBanner(text = "Migration runs on first read", status = Status.Info)
            SectionCard(title = "Code") {
                CodeBlock(text = "01 9f 3a c2 7e 44 b0 1d\ne8 52 0c 9a f1 6b 23 d7")
            }
        }
    }
}
