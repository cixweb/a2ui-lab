package io.github.cixweb.a2uilab.agent

import kotlin.time.Duration
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseAgentTest {

    private val agent = ReleaseAgent(catalogId = "catalog", pause = Duration.ZERO)

    private fun answer(turn: Turn): List<JsonObject> = runBlocking { agent.respond(turn).toList() }

    private fun types(messages: List<JsonObject>) = messages.map { it.keys.single { k -> k != "version" } }

    private fun prepare(version: String = "2.4.0", rollout: Double = 20.0) =
        Turn.Action(
            name = ReleaseAgent.PREPARE_RELEASE,
            surfaceId = ReleaseAgent.SURFACE_ID,
            context =
                buildJsonObject {
                    put("version", version)
                    putJsonArray("track") { add("production") }
                    put("rollout", rollout)
                },
        )

    @Test
    fun aRequestGetsTheLayoutAndThenTheData() {
        assertEquals(
            listOf("createSurface", "updateComponents", "updateDataModel"),
            types(answer(Turn.UserText("prepare a release"))),
        )
    }

    @Test
    fun theFormDrawsWithTheCatalogTheClientSupports() {
        val create = answer(Turn.UserText("x")).first().getValue("createSurface").jsonObject
        assertEquals("catalog", create.getValue("catalogId").jsonPrimitive.content)
    }

    @Test
    fun onlyTheListRequestDrawsTheRowsInAList() {
        fun rows(text: String) =
            answer(Turn.UserText(text))[1]
                .getValue("updateComponents").jsonObject
                .getValue("components").jsonArray
                .map { it.jsonObject }
                .single { it.getValue("id").jsonPrimitive.content == "history_rows" }
                .getValue("component").jsonPrimitive.content

        assertEquals("Column", rows("prepare a release"))
        assertEquals("List", rows(ReleaseAgent.LIST_REQUEST))
    }

    private fun update(message: JsonObject) = message.getValue("updateDataModel").jsonObject

    private fun path(message: JsonObject) = update(message).getValue("path").jsonPrimitive.content

    private fun value(message: JsonObject) = update(message).getValue("value").jsonObject

    @Test
    fun prepareAnswersWithAPlanComputedByCode() {
        val (plan, row, components) = answer(prepare())

        assertEquals("/plan", path(plan))
        assertEquals("20% → 50% → 100%", value(plan).getValue("stages").jsonPrimitive.content)
        assertEquals("production", value(plan).getValue("track").jsonPrimitive.content)
        assertEquals("/releases/2", path(row))
        assertEquals(value(plan), value(row))
        assertEquals("updateComponents", types(listOf(components)).single())
    }

    @Test
    fun eachPreparedReleaseAddsOneRow() {
        answer(prepare())
        val (_, row, _) = answer(prepare(version = "2.4.1"))

        assertEquals("/releases/3", path(row))
        assertEquals("2.4.1", value(row).getValue("version").jsonPrimitive.content)
    }

    @Test
    fun theAgentRemembersReleasesAcrossSurfaces() {
        answer(prepare())
        answer(Turn.Action(ReleaseAgent.START_OVER, ReleaseAgent.SURFACE_ID, JsonObject(emptyMap())))

        val data = answer(Turn.UserText("again")).last()
        val versions =
            value(data).getValue("releases").jsonArray.map {
                it.jsonObject.getValue("version").jsonPrimitive.content
            }

        assertEquals(listOf("2.3.0", "2.3.1", "2.4.0"), versions)
    }

    @Test
    fun textTypedByTheUserCannotBreakTheMessage() {
        val tricky = "2\", \"rollout\": 100"
        val plan = value(answer(prepare(version = tricky)).first())

        assertEquals(tricky, plan.getValue("version").jsonPrimitive.content)
        assertEquals(setOf("version", "track", "stages"), plan.keys)
    }

    @Test
    fun aContextWithoutItsValuesGetsNoAnswer() {
        val empty = Turn.Action(ReleaseAgent.PREPARE_RELEASE, ReleaseAgent.SURFACE_ID, JsonObject(emptyMap()))
        val wrongType =
            Turn.Action(
                ReleaseAgent.PREPARE_RELEASE,
                ReleaseAgent.SURFACE_ID,
                JsonObject(mapOf("version" to JsonPrimitive("1"), "track" to JsonPrimitive("beta"))),
            )

        assertTrue(answer(empty).isEmpty())
        assertTrue(answer(wrongType).isEmpty())
    }

    @Test
    fun startOverDeletesTheSurface() {
        val messages = answer(Turn.Action(ReleaseAgent.START_OVER, ReleaseAgent.SURFACE_ID, JsonObject(emptyMap())))
        assertEquals(listOf("deleteSurface"), types(messages))
    }

    @Test
    fun anUnknownEventGetsNoAnswer() {
        assertTrue(answer(Turn.Action("dance", ReleaseAgent.SURFACE_ID, JsonObject(emptyMap()))).isEmpty())
    }
}
