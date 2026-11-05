package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.model.protocol.A2uiClientErrorMessage
import androidx.a2ui.model.protocol.A2uiClientEventMessage
import org.junit.Assert.assertEquals
import org.junit.Test

class InspectorTest {

    private fun summary(message: String) = summarize(message, jvmMessageParser())

    @Test
    fun summariesSayWhatEachMessageDoes() {
        val create = """{"version": "v0.9.1", "createSurface": {"surfaceId": "s", "catalogId": "c"}}"""
        val components =
            """{"version": "v0.9.1", "updateComponents": {"surfaceId": "s", "components": [
                {"id": "root", "component": "Text", "text": "t"}]}}"""
        val data =
            """{"version": "v0.9.1", "updateDataModel": {"surfaceId": "s", "path": "/a", "value": 50}}"""
        val delete = """{"version": "v0.9.1", "deleteSurface": {"surfaceId": "s"}}"""

        assertEquals("createSurface s", summary(create))
        assertEquals("updateComponents s: 1 components", summary(components))
        assertEquals("updateDataModel s: /a = 50", summary(data))
        assertEquals("deleteSurface s", summary(delete))
    }

    @Test
    fun aListOfObjectsIsCountedAndAListOfWordsIsShown() {
        val rows =
            """{"version": "v0.9.1", "updateDataModel": {"surfaceId": "s", "path": "/a",
                "value": [{"v": 1}, {"v": 2}]}}"""
        val words =
            """{"version": "v0.9.1", "updateDataModel": {"surfaceId": "s", "path": "/a",
                "value": ["beta"]}}"""

        assertEquals("updateDataModel s: /a = [2 items]", summary(rows))
        assertEquals("updateDataModel s: /a = [beta]", summary(words))
    }

    @Test
    fun brokenJsonIsNamedAsSuch() {
        assertEquals("not a valid A2UI message", summary("{not json"))
    }

    @Test
    fun anActionListsItsValuesInOrder() {
        val event =
            A2uiClientEventMessage(
                type = "prepare_release",
                surfaceId = "release",
                componentId = "prepare",
                timestamp = 0,
                context = mapOf("version" to "2.4.0", "rollout" to 20.0),
            )

        assertEquals(
            "action prepare_release from prepare\n  rollout = 20\n  version = \"2.4.0\"",
            describe(event),
        )
    }

    @Test
    fun anErrorShowsCodeAndText() {
        val error =
            A2uiClientErrorMessage(
                code = "VALIDATION_FAILED",
                surfaceId = "release",
                message = "x",
                context = mapOf("path" to "/"),
            )

        assertEquals("error VALIDATION_FAILED: x", describe(error))
    }
}
