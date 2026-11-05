package io.github.cixweb.a2uilab.ui

import androidx.a2ui.compose.runtime.A2uiMessageParser
import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.A2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.processor.processInput
import androidx.a2ui.model.protocol.A2uiClientEventMessage
import androidx.a2ui.model.protocol.A2uiClientToServerMessage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.cixweb.a2uilab.a2ui.toTurn
import io.github.cixweb.a2uilab.agent.Agent
import io.github.cixweb.a2uilab.agent.Turn
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Connects the A2UI message processor of the lab screen to the agent, in both directions.
 *
 * Messages from the agent go into the processor, which turns them into [surfaces]. Events from the
 * processor go to the agent, which answers with more messages. [received] and [sent] record the
 * traffic for the screen.
 *
 * @param catalog the catalog the agent may use, advertised to it and used to validate its messages
 * @param agent answers each turn; here it is simulated and runs inside the app
 */
class LabViewModel(catalog: A2uiCatalog, private val agent: Agent) : ViewModel() {

    private val parser = A2uiMessageParser()
    private val processor = A2uiMessageProcessor(catalogs = listOf(catalog))
    private val receivedMessages = MutableStateFlow<List<String>>(emptyList())
    private val sentMessages = MutableStateFlow<List<A2uiClientToServerMessage>>(emptyList())

    /** The surfaces the agent has created and not deleted yet. */
    val surfaces: StateFlow<List<A2uiSurfaceModel>> = processor.activeSurfaces

    /** Every message received so far, in order. */
    val received: StateFlow<List<String>> = receivedMessages.asStateFlow()

    /** Every action and error the app has for the agent, in order. */
    val sent: StateFlow<List<A2uiClientToServerMessage>> = sentMessages.asStateFlow()

    init {
        // The processor handles queued messages only while this loop runs, off the main thread.
        viewModelScope.launch(Dispatchers.Default) { processor.collectMessages() }
        // UNDISPATCHED subscribes right here: the flow does not replay what it already emitted.
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            processor.outboundEvents.collect { message ->
                sentMessages.update { it + message }
                if (message is A2uiClientEventMessage) play(message.toTurn())
            }
        }
    }

    /**
     * Sends the user's words to the agent.
     *
     * @param text the request
     */
    fun ask(text: String) {
        viewModelScope.launch { play(Turn.UserText(text)) }
    }

    private suspend fun play(turn: Turn) {
        agent.respond(turn).collect { message ->
            val json = message.toString()
            receivedMessages.update { it + json }
            processor.processInput(parser, json)
        }
    }
}
