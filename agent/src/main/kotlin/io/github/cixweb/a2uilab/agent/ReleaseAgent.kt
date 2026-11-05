package io.github.cixweb.a2uilab.agent

import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * A simulated agent with fixed rules and no model: it draws the release form and answers its button.
 *
 * It does not understand the user's words, so any request gets the same form. The sample data and
 * the rollout rule are not real.
 *
 * It remembers the releases it prepared, while the data model of a surface is gone as soon as the
 * surface is deleted. The memory is one list in this object, so one instance serves one client.
 *
 * @param catalogId the catalog to draw with, one the client said it supports
 * @param pause how long it "thinks" between the layout and the data, so the wait is visible
 */
class ReleaseAgent(
    private val catalogId: String,
    private val pause: Duration = 800.milliseconds,
) : Agent {

    private val releases = mutableListOf(release("2.3.0", "production", 50), release("2.3.1", "beta", 20))

    override fun respond(turn: Turn): Flow<JsonObject> = flow {
        when (turn) {
            is Turn.UserText -> sendForm()
            is Turn.Action ->
                when (turn.name) {
                    PREPARE_RELEASE -> sendPlan(turn.context)
                    START_OVER -> emit(A2uiMessages.deleteSurface(SURFACE_ID))
                }
        }
    }

    private suspend fun FlowCollector<JsonObject>.sendForm() {
        emit(A2uiMessages.createSurface(SURFACE_ID, catalogId))
        emit(A2uiMessages.updateComponents(SURFACE_ID, ReleaseForm.COMPONENTS))
        delay(pause)
        emit(A2uiMessages.updateDataModel(SURFACE_ID, ReleaseForm.formData(releases)))
    }

    /** Answers with the plan; a context without the values it needs gets no answer at all. */
    private suspend fun FlowCollector<JsonObject>.sendPlan(context: JsonObject) {
        val version = (context["version"] as? JsonPrimitive)?.contentOrNull ?: return
        val track = (context["track"] as? JsonArray)?.firstOrNull()?.jsonPrimitive?.contentOrNull ?: return
        val rollout = (context["rollout"] as? JsonPrimitive)?.doubleOrNull?.roundToInt() ?: return
        val release = release(version, track, rollout)
        releases += release
        emit(A2uiMessages.updateDataModel(SURFACE_ID, release, path = "/plan"))
        // One new row: the path points past the end of the list, so nothing else is sent again.
        emit(A2uiMessages.updateDataModel(SURFACE_ID, release, path = "/releases/${releases.lastIndex}"))
        emit(A2uiMessages.updateComponents(SURFACE_ID, ReleaseForm.PLAN_COMPONENTS))
    }

    private fun release(version: String, track: String, rollout: Int): JsonObject =
        buildJsonObject {
            put("version", version)
            put("track", track)
            put("stages", RolloutPlan.stages(track, rollout).joinToString(" → ") { "$it%" })
        }

    /** The names this agent shares with the client. */
    companion object {
        /** The id of the only surface this agent draws. */
        const val SURFACE_ID = "release"

        /** The event of the "Prepare release" button. */
        const val PREPARE_RELEASE = "prepare_release"

        /** The event of the "Start over" button. */
        const val START_OVER = "start_over"
    }
}
