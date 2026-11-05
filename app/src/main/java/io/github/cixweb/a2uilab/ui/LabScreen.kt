package io.github.cixweb.a2uilab.ui

import androidx.a2ui.compose.ui.catalog.A2uiBasicCatalogV1
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.protocol.A2uiClientToServerMessage
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.a2ui.A2uiSurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.cixweb.a2uilab.R
import io.github.cixweb.a2uilab.a2ui.LabCatalogId
import io.github.cixweb.a2uilab.a2ui.dataModelLines
import io.github.cixweb.a2uilab.a2ui.describe
import io.github.cixweb.a2uilab.a2ui.labCatalog
import io.github.cixweb.a2uilab.a2ui.settledSurfaces
import io.github.cixweb.a2uilab.a2ui.summarize
import io.github.cixweb.a2uilab.agent.ReleaseAgent
import io.github.cixweb.a2uilab.ui.components.AppScaffold
import io.github.cixweb.a2uilab.ui.components.CodeBlock
import io.github.cixweb.a2uilab.ui.components.PrimaryButton
import io.github.cixweb.a2uilab.ui.components.SectionCard

/** The lab screen wired to its [LabViewModel]. */
@Composable
fun LabRoute(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val request = stringResource(R.string.lab_request)
    val viewModel = viewModel {
        LabViewModel(
            catalog = labCatalog(A2uiBasicCatalogV1.Functions(context.applicationContext)),
            agent = ReleaseAgent(catalogId = LabCatalogId),
        )
    }
    val surfaces by viewModel.surfaces.collectAsStateWithLifecycle()
    val received by viewModel.received.collectAsStateWithLifecycle()
    val sent by viewModel.sent.collectAsStateWithLifecycle()
    LabScreen(
        surfaces = surfaces,
        received = received,
        sent = sent,
        onAsk = { viewModel.ask(request) },
        modifier = modifier,
    )
}

/**
 * Shows what the agent drew, the data behind it and the traffic in both directions.
 *
 * @param surfaces the active A2UI surfaces, drawn in order
 * @param received the A2UI messages received so far
 * @param sent the actions and errors the app has for the agent
 * @param onAsk sends the request to the agent; offered while no surface is open
 */
@Composable
fun LabScreen(
    surfaces: List<A2uiSurfaceModel>,
    received: List<String>,
    sent: List<A2uiClientToServerMessage>,
    onAsk: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        title = stringResource(R.string.app_name),
        subtitle = stringResource(R.string.app_tagline),
        modifier = modifier,
    ) {
        if (surfaces.isEmpty()) {
            SectionCard(title = stringResource(R.string.lab_agent_title)) {
                Text(
                    text = stringResource(R.string.lab_agent_body),
                    style = MaterialTheme.typography.bodyMedium,
                )
                PrimaryButton(text = stringResource(R.string.lab_ask), onClick = onAsk)
            }
        }
        surfaces.forEach { surface -> key(surface.id) { A2uiSurface(surfaceModel = surface) } }
        if (sent.isNotEmpty()) {
            SectionCard(title = stringResource(R.string.lab_sent_title)) {
                CodeBlock(text = sent.joinToString(separator = "\n", transform = ::describe))
            }
        }
        surfaces.forEach { surface ->
            val lines = dataModelLines(surface)
            if (lines.isNotEmpty()) {
                SectionCard(title = stringResource(R.string.lab_data_title)) {
                    CodeBlock(text = lines.joinToString(separator = "\n"))
                }
            }
        }
        if (received.isNotEmpty()) {
            SectionCard(title = stringResource(R.string.lab_received_title)) {
                CodeBlock(
                    text =
                        received
                            .mapIndexed { index, message -> "${index + 1}. ${summarize(message)}" }
                            .joinToString(separator = "\n")
                )
            }
        }
    }
}

/**
 * The screen after [messages] from the agent, for previews and screenshot tests.
 *
 * @param messages the A2UI envelopes the agent has sent
 * @param sent what the panel of sent messages shows
 */
@Composable
fun LabScreenAfter(
    messages: List<String>,
    sent: List<A2uiClientToServerMessage> = emptyList(),
) {
    val context = LocalContext.current
    val surfaces = remember(messages) {
        settledSurfaces(labCatalog(A2uiBasicCatalogV1.Functions(context)), messages)
    }
    LabScreen(surfaces = surfaces, received = messages, sent = sent, onAsk = {})
}
