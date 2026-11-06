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

/** The rows as a `List`, which the app does not offer: no agent answer asks for it, so it is written here. */
private val refusedList =
    """{"version": "v0.9.1", "updateComponents": {"surfaceId": "release", "components": [
       {"id": "history_rows", "component": "List", "direction": "vertical",
        "children": {"path": "/releases", "componentId": "release_row"}}]}}"""

private val prepare =
    A2uiClientEventMessage(
        type = ReleaseAgent.PREPARE_RELEASE,
        surfaceId = ReleaseAgent.SURFACE_ID,
        componentId = "prepare",
        timestamp = 0,
        context = mapOf("version" to "2.4.0", "track" to listOf("beta"), "rollout" to 20),
    )

// Answered once: the agent remembers, so each new answer would add another row.
private val plan = answer(prepare.toTurn())

/** The box the user ticks before the button is enabled. */
private val changelogReviewed =
    """{"version": "v0.9.1", "updateDataModel": {"surfaceId": "release",
        "path": "/changelogReviewed", "value": true}}"""

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
        LabScreenAfter(messages = form + changelogReviewed + plan, sent = listOf(prepare))
    }
}

@PreviewTest
@ScreenPreviews
@Composable
fun LabScreenRefusedListScreenshot() {
    AppTheme {
        LabScreenAfter(
            messages = form + refusedList,
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
