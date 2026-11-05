package io.github.cixweb.a2uilab.agent

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/** Builds the four A2UI v0.9.1 messages an agent can send. */
object A2uiMessages {

    private const val VERSION = "v0.9.1"

    /**
     * Opens a surface.
     *
     * @param surfaceId the id of the new surface
     * @param catalogId the catalog its components come from, one the client said it supports
     * @return the `createSurface` envelope
     */
    fun createSurface(surfaceId: String, catalogId: String): JsonObject =
        envelope("createSurface") {
            put("surfaceId", surfaceId)
            put("catalogId", catalogId)
        }

    /**
     * Adds or replaces components, matched by id.
     *
     * @param surfaceId the target surface
     * @param components the components as a JSON array, the flat list the protocol asks for
     * @return the `updateComponents` envelope
     */
    fun updateComponents(surfaceId: String, components: String): JsonObject =
        envelope("updateComponents") {
            put("surfaceId", surfaceId)
            put("components", Json.parseToJsonElement(components))
        }

    /**
     * Changes the data model. Values built here are escaped by the JSON library, so text typed by
     * the user can never break the message.
     *
     * @param surfaceId the target surface
     * @param value the new value
     * @param path the JSON pointer to change, or null to replace the whole model
     * @return the `updateDataModel` envelope
     */
    fun updateDataModel(surfaceId: String, value: JsonElement, path: String? = null): JsonObject =
        envelope("updateDataModel") {
            put("surfaceId", surfaceId)
            if (path != null) put("path", path)
            put("value", value)
        }

    /**
     * Closes a surface and forgets its components and data.
     *
     * @param surfaceId the surface to delete
     * @return the `deleteSurface` envelope
     */
    fun deleteSurface(surfaceId: String): JsonObject =
        envelope("deleteSurface") { put("surfaceId", surfaceId) }

    private fun envelope(type: String, body: JsonObjectBuilder.() -> Unit): JsonObject =
        buildJsonObject {
            put("version", VERSION)
            putJsonObject(type, body)
        }
}
