package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.A2uiMessageProcessor
import androidx.a2ui.compose.ui.catalog.A2uiBasicCatalogV1
import androidx.a2ui.model.processor.A2uiJsonMessageParser
import androidx.a2ui.model.processor.A2uiJsonReader
import androidx.a2ui.model.processor.A2uiJsonToken
import androidx.a2ui.model.processor.A2uiMessageParser
import androidx.a2ui.model.processor.processInput
import androidx.a2ui.model.protocol.A2uiClientToServerMessage
import androidx.a2ui.model.protocol.A2uiExecutionContext
import com.google.gson.stream.JsonReader
import java.io.StringReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** The lab catalog with functions that need no Android context. */
val testCatalog: A2uiCatalog = labCatalog(NoUrls)

/**
 * The A2UI message parser for tests on the JVM.
 *
 * The library's default parser reads JSON with `android.util.JsonReader`, which is only a stub
 * outside a device; Gson's reader has the same API.
 */
fun jvmMessageParser(): A2uiMessageParser<String> = A2uiJsonMessageParser(::GsonJsonReader)

/**
 * Everything the processor has for the agent after [messages]: validation errors above all.
 *
 * @param messages the A2UI envelopes, in order
 * @return the outbound messages; empty when every message fits the catalog
 */
fun outboundAfter(messages: List<String>): List<A2uiClientToServerMessage> {
    val processor = A2uiMessageProcessor(catalogs = listOf(testCatalog))
    val outbound = mutableListOf<A2uiClientToServerMessage>()
    val scope = CoroutineScope(Dispatchers.Unconfined)
    scope.launch { processor.outboundEvents.collect { outbound += it } }
    scope.launch { processor.collectMessages() }
    val parser = jvmMessageParser()
    messages.forEach { processor.processInput(parser, it) }
    return outbound
}

private object NoUrls : A2uiBasicCatalogV1.Functions {
    override fun openUrl(url: String, executionContext: A2uiExecutionContext) = Unit
}

private class GsonJsonReader(json: String) : A2uiJsonReader {
    private val delegate = JsonReader(StringReader(json))

    // Gson names its tokens exactly like A2uiJsonToken.
    override fun peek(): A2uiJsonToken = A2uiJsonToken.valueOf(delegate.peek().name)

    override fun beginObject() = delegate.beginObject()

    override fun endObject() = delegate.endObject()

    override fun beginArray() = delegate.beginArray()

    override fun endArray() = delegate.endArray()

    override fun hasNext(): Boolean = delegate.hasNext()

    override fun nextName(): String = delegate.nextName()

    override fun nextString(): String = delegate.nextString()

    override fun nextBoolean(): Boolean = delegate.nextBoolean()

    override fun nextDouble(): Double = delegate.nextDouble()

    override fun nextInt(): Int = delegate.nextInt()

    override fun nextLong(): Long = delegate.nextLong()

    override fun nextNull() = delegate.nextNull()

    override fun skipValue() = delegate.skipValue()

    override fun close() = delegate.close()
}
