package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.model.protocol.A2uiClientEventMessage
import io.github.cixweb.a2uilab.agent.ReleaseAgent
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

/** An event as the library would emit it for the "Prepare release" button. */
val samplePrepareEvent =
    A2uiClientEventMessage(
        type = ReleaseAgent.PREPARE_RELEASE,
        surfaceId = ReleaseAgent.SURFACE_ID,
        componentId = "prepare",
        timestamp = 0,
        context = mapOf("version" to "2.4.0", "track" to listOf("production"), "rollout" to 20f),
    )

class AgentBridgeTest {

    @Test
    fun anEventBecomesATurnWithTheSameValues() {
        val turn = samplePrepareEvent.toTurn()

        assertEquals(ReleaseAgent.PREPARE_RELEASE, turn.name)
        assertEquals(ReleaseAgent.SURFACE_ID, turn.surfaceId)
        assertEquals(JsonPrimitive("2.4.0"), turn.context["version"])
        assertEquals(JsonArray(listOf(JsonPrimitive("production"))), turn.context["track"])
        assertEquals(JsonPrimitive(20f), turn.context["rollout"])
    }

    @Test
    fun nestedValuesAndNullsSurvive() {
        val event =
            A2uiClientEventMessage(
                type = "e",
                surfaceId = "s",
                componentId = "c",
                timestamp = 0,
                context = mapOf("nested" to mapOf("flag" to true), "nothing" to null),
            )

        val context = event.toTurn().context

        assertEquals(JsonObject(mapOf("flag" to JsonPrimitive(true))), context["nested"])
        assertEquals(JsonNull, context["nothing"])
    }
}
