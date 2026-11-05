package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.compose.runtime.A2uiMessageParser
import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.A2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiMessageParser
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.processor.processInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Processes [messages] on the calling thread and returns the surfaces they produce.
 *
 * Only for previews and tests: they draw a single frame and cannot wait for the background loop
 * that a real screen runs in its ViewModel.
 *
 * @param catalog the catalog the surfaces may use
 * @param messages the A2UI envelopes, in the order the agent sends them
 * @param parser the JSON parser; tests on the JVM pass one that does not need Android
 * @return the active surfaces once every message has been handled
 */
fun settledSurfaces(
    catalog: A2uiCatalog,
    messages: List<String>,
    parser: A2uiMessageParser<String> = A2uiMessageParser(),
): List<A2uiSurfaceModel> {
    val processor = A2uiMessageProcessor(catalogs = listOf(catalog))
    // With Unconfined the loop runs inside each processInput call, so a message is handled before
    // the call returns. The loop is never cancelled: cancelling it disposes every surface.
    CoroutineScope(Dispatchers.Unconfined).launch { processor.collectMessages() }
    messages.forEach { processor.processInput(parser, it) }
    return processor.activeSurfaces.value
}
