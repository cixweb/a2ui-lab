package io.github.cixweb.a2uilab.ui

import androidx.a2ui.model.protocol.A2uiClientErrorMessage
import androidx.a2ui.model.protocol.A2uiClientEventMessage
import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.cixweb.a2uilab.a2ui.LabCatalogId
import io.github.cixweb.a2uilab.a2ui.toTurn
import io.github.cixweb.a2uilab.agent.ReleaseAgent
import io.github.cixweb.a2uilab.agent.Turn
import io.github.cixweb.a2uilab.ui.components.ScreenPreviews
import io.github.cixweb.a2uilab.ui.theme.AppTheme
import kotlin.time.Duration
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

private val agent = ReleaseAgent(LabCatalogId, pause = Duration.ZERO)

private fun answer(turn: Turn): List<String> = runBlocking {
    agent.respond(turn).toList().map { it.toString() }
}

private val form = answer(Turn.UserText("Prepare the next release"))

private val prepare =
    A2uiClientEventMessage(
        type = ReleaseAgent.PREPARE_RELEASE,
        surfaceId = ReleaseAgent.SURFACE_ID,
        componentId = "prepare",
        timestamp = 0,
        context = mapOf("version" to "2.4.0", "track" to listOf("production"), "rollout" to 20),
    )

@PreviewTest
@ScreenPreviews
@Composable
fun LabScreenWaitingForDataScreenshot() {
    AppTheme {
        LabScreenAfter(messages = form.take(2))
    }
}

@PreviewTest
@ScreenPreviews
@Composable
fun LabScreenFormScreenshot() {
    AppTheme {
        LabScreenAfter(messages = form)
    }
}

@PreviewTest
@ScreenPreviews
@Composable
fun LabScreenPlanScreenshot() {
    AppTheme {
        LabScreenAfter(messages = form + answer(prepare.toTurn()), sent = listOf(prepare))
    }
}

@PreviewTest
@ScreenPreviews
@Composable
fun LabScreenRefusedListScreenshot() {
    AppTheme {
        LabScreenAfter(
            messages = answer(Turn.UserText(ReleaseAgent.LIST_REQUEST)),
            sent =
                listOf(
                    A2uiClientErrorMessage(
                        code = "VALIDATION_FAILED",
                        surfaceId = ReleaseAgent.SURFACE_ID,
                        message = "Component type 'List' not found in catalog",
                        context = mapOf("path" to "/components/history_rows"),
                    )
                ),
        )
    }
}
