package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.compose.runtime.A2uiComponentProperties
import androidx.a2ui.compose.runtime.A2uiComponentScope
import androidx.a2ui.compose.runtime.A2uiComponentState
import androidx.a2ui.compose.runtime.A2uiProperty
import androidx.a2ui.compose.ui.A2uiComponent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.cixweb.a2uilab.R
import io.github.cixweb.a2uilab.ui.components.SectionCard
import io.github.cixweb.a2uilab.ui.components.Status
import io.github.cixweb.a2uilab.ui.components.StatusBanner

/**
 * The kit's [SectionCard] as an A2UI component, so the agent can ask for it by name.
 *
 * This class is only a translation: the properties say what the agent may send, [Content] turns
 * them into the arguments of the composable. The look stays in the kit.
 */
object SectionCardComponent : A2uiComponent {

    private val TitleProperty =
        A2uiProperty.dynamicString(
            key = "title",
            description = "The heading of the card. Leave it out for a card with no heading.",
        )

    private val ChildProperty =
        A2uiProperty.componentId(
            key = "child",
            required = true,
            description =
                "The ID of the one component inside the card. To show several, wrap them in a " +
                    "Column and pass the ID of the Column.",
        )

    override val name: String = "SectionCard"

    override val description: String =
        "A titled block of content on the screen background. Use one for each topic of the screen."

    override val properties: List<A2uiProperty<*>> = listOf(TitleProperty, ChildProperty)

    @Composable
    override fun A2uiComponentScope.Content(
        properties: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        // The validator has already refused any message without a child.
        val childId = checkNotNull(properties[ChildProperty])
        SectionCard(modifier = modifier, title = properties.bind(TitleProperty)) {
            when (val child = observeA2uiComponentState(childId)) {
                // The agent has not sent the child yet, or the child waits for its data.
                is A2uiComponentState.Loading ->
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                // The validator refused the child; the processor reports why in an error message.
                is A2uiComponentState.Error ->
                    StatusBanner(
                        text = stringResource(R.string.lab_card_error),
                        status = Status.Error,
                    )
                is A2uiComponentState.Success -> A2uiComponent(component = child.component)
            }
        }
    }
}
