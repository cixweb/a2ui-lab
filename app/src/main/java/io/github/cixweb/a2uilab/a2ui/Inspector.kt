package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.compose.runtime.A2uiMessageParser
import androidx.a2ui.engine.model.A2uiCoreSurfaceModel
import androidx.a2ui.model.processor.A2uiMessageParser
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.protocol.A2uiClientErrorMessage
import androidx.a2ui.model.protocol.A2uiClientEventMessage
import androidx.a2ui.model.protocol.A2uiClientToServerMessage
import androidx.a2ui.model.protocol.A2uiCreateSurfaceMessage
import androidx.a2ui.model.protocol.A2uiDataPath
import androidx.a2ui.model.protocol.A2uiDeleteSurfaceMessage
import androidx.a2ui.model.protocol.A2uiUpdateComponentsMessage
import androidx.a2ui.model.protocol.A2uiUpdateDataModelMessage

/**
 * One line that says what an A2UI message does, for the lab's message list.
 *
 * @param message one A2UI envelope as JSON text
 * @param parser the JSON parser; tests on the JVM pass one that does not need Android
 * @return the message type, its surface and what it changes
 */
fun summarize(message: String, parser: A2uiMessageParser<String> = A2uiMessageParser()): String =
    when (val parsed = runCatching { parser.parse(message) }.getOrNull()) {
        is A2uiCreateSurfaceMessage -> "createSurface ${parsed.surfaceId}"
        is A2uiUpdateComponentsMessage ->
            "updateComponents ${parsed.surfaceId}: ${parsed.components.size} components"
        is A2uiUpdateDataModelMessage ->
            "updateDataModel ${parsed.surfaceId}: ${parsed.path} = ${shortValue(parsed.value)}"
        is A2uiDeleteSurfaceMessage -> "deleteSurface ${parsed.surfaceId}"
        null -> "not a valid A2UI message"
    }

/**
 * What the app sends to the agent, in a few lines, for the lab's "sent" panel.
 *
 * @param message an action or an error from the message processor
 * @return the event name and its values, or the error code and text
 */
fun describe(message: A2uiClientToServerMessage): String =
    when (message) {
        is A2uiClientEventMessage ->
            (listOf("action ${message.type} from ${message.componentId}") +
                    message.context.toSortedMap().map { (key, value) ->
                        "  $key = ${shortValue(value)}"
                    })
                .joinToString(separator = "\n")
        is A2uiClientErrorMessage -> "error ${message.code}: ${message.message}"
    }

/**
 * The top-level values of a surface's data model, one `/key = value` line each.
 *
 * @param surface the surface to read
 * @return the lines in the model's order, empty while the agent has sent no data
 */
fun dataModelLines(surface: A2uiSurfaceModel): List<String> {
    val model = (surface as? A2uiCoreSurfaceModel)?.dataModel ?: return emptyList()
    val root = model[A2uiDataPath("/")] as? Map<*, *> ?: return emptyList()
    return root.map { (key, value) -> "/$key = ${shortValue(value)}" }
}

private fun shortValue(value: Any?): String =
    when (value) {
        is Map<*, *> -> value.keys.joinToString(prefix = "{", postfix = "}")
        is List<*> ->
            if (value.any { it is Map<*, *> }) "[${value.size} items]" else value.toString()
        is String -> "\"$value\""
        is Number -> value.toInt().takeIf { it.toDouble() == value.toDouble() }?.toString()
            ?: value.toString()
        else -> value.toString()
    }
