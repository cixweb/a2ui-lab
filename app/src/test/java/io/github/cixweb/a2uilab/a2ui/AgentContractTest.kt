package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.engine.model.A2uiCoreSurfaceModel
import androidx.a2ui.model.protocol.A2uiClientErrorMessage
import androidx.a2ui.model.protocol.A2uiDataPath
import io.github.cixweb.a2uilab.agent.ReleaseAgent
import io.github.cixweb.a2uilab.agent.Turn
import kotlin.time.Duration
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Runs what the agent really sends through the app's real processor and validator. */
class AgentContractTest {

    private val agent = ReleaseAgent(LabCatalogId, pause = Duration.ZERO)

    private fun answer(turn: Turn): List<String> = runBlocking {
        agent.respond(turn).toList().map { it.toString() }
    }

    private val form = answer(Turn.UserText("Prepare the next release"))
    private val plan = answer(samplePrepareEvent.toTurn())
    private val startOver =
        answer(Turn.Action(ReleaseAgent.START_OVER, ReleaseAgent.SURFACE_ID, JsonObject(emptyMap())))

    @Test
    fun everyMessageFitsTheCatalog() {
        assertEquals(emptyList<Any>(), outboundAfter(form + plan + startOver))
    }

    @Test
    fun theFormFillsTheDataModel() {
        val surface = settledSurfaces(testCatalog, form, jvmMessageParser()).single()
        val model = (surface as A2uiCoreSurfaceModel).dataModel

        assertEquals(ReleaseAgent.SURFACE_ID, surface.id)
        assertEquals("2.4.0", model[A2uiDataPath("/version")])
        assertEquals(false, model[A2uiDataPath("/changelogReviewed")])
    }

    @Test
    fun thePlanLandsInTheDataModel() {
        val surface = settledSurfaces(testCatalog, form + plan, jvmMessageParser()).single()
        val model = (surface as A2uiCoreSurfaceModel).dataModel

        assertEquals("20% → 50% → 100%", model[A2uiDataPath("/plan/stages")])
        assertEquals("2.4.0", model[A2uiDataPath("/version")])
    }

    @Test
    fun theNewRowExtendsTheList() {
        val surface = settledSurfaces(testCatalog, form + plan, jvmMessageParser()).single()
        val model = (surface as A2uiCoreSurfaceModel).dataModel

        assertEquals(3, (model[A2uiDataPath("/releases")] as List<*>).size)
        assertEquals("2.3.0", model[A2uiDataPath("/releases/0/version")])
        assertEquals("2.4.0", model[A2uiDataPath("/releases/2/version")])
    }

    @Test
    fun aComponentTheAppDoesNotOfferComesBackAsAnError() {
        val create = form.first()
        val list =
            """{"version": "v0.9.1", "updateComponents": {"surfaceId": "release", "components": [
               {"id": "history_rows", "component": "List", "direction": "vertical",
                "children": {"path": "/releases", "componentId": "release_row"}}]}}"""

        val error = outboundAfter(listOf(create, list)).single() as A2uiClientErrorMessage

        assertEquals("VALIDATION_FAILED", error.code)
        assertEquals("Component type 'List' not found in catalog", error.message)
        assertEquals("/components/history_rows", error.context["path"])
    }

    @Test
    fun theOwnCardFollowsItsSchemaToo() {
        val create = form.first()
        val noChild =
            """{"version": "v0.9.1", "updateComponents": {"surfaceId": "release", "components": [
               {"id": "card", "component": "SectionCard", "title": "No child"}]}}"""

        val error = outboundAfter(listOf(create, noChild)).single() as A2uiClientErrorMessage

        assertEquals("/components/card", error.context["path"])
        assertTrue(error.message, error.message.contains("child"))
    }

    @Test
    fun startOverClosesTheSurface() {
        assertTrue(settledSurfaces(testCatalog, form + startOver, jvmMessageParser()).isEmpty())
    }
}
