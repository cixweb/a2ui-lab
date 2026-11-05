package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.model.protocol.A2uiClientEventMessage
import io.github.cixweb.a2uilab.agent.Turn
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Translates a button tap from the library's type to the agent's.
 *
 * The context is copied right here: its lists are live views of the data model, and the user may
 * change them while the agent is still working.
 *
 * @return the agent's view of the same event
 */
fun A2uiClientEventMessage.toTurn(): Turn.Action =
    Turn.Action(
        name = type,
        surfaceId = surfaceId,
        context = JsonObject(context.mapValues { (_, value) -> value.toJsonElement() }),
    )

private fun Any?.toJsonElement(): JsonElement =
    when (this) {
        null -> JsonNull
        is String -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is Map<*, *> -> JsonObject(entries.associate { (k, v) -> k.toString() to v.toJsonElement() })
        is Iterable<*> -> JsonArray(map { it.toJsonElement() })
        else -> JsonPrimitive(toString())
    }
