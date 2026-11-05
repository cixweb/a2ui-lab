package io.github.cixweb.a2uilab.agent

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

/** What happened on the client, from the agent's point of view. */
sealed interface Turn {

    /**
     * The user asked for something in words.
     *
     * @property text the request
     */
    data class UserText(val text: String) : Turn

    /**
     * The user touched a control that sends an A2UI event.
     *
     * @property name the event name, as written in the component's `action`
     * @property surfaceId the surface the control belongs to
     * @property context the values the `action` asked for, already read from the data model
     */
    data class Action(val name: String, val surfaceId: String, val context: JsonObject) : Turn
}

/** The brain of the backend: turns one client turn into a stream of A2UI messages. */
fun interface Agent {

    /**
     * Answers one turn.
     *
     * @param turn what happened on the client
     * @return the A2UI envelopes to send back, in order
     */
    fun respond(turn: Turn): Flow<JsonObject>
}
