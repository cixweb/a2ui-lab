package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.catalog.A2uiBasicCatalogV1
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.a2ui.catalog.MaterialA2uiBasicCatalogV1Defaults
import androidx.compose.material3.a2ui.catalog.materialA2uiBasicCatalogV1

/**
 * The name of the lab's catalog. It identifies the contract and is never fetched, so nothing has to
 * exist at this address. A new version of the contract gets a new name.
 */
const val LabCatalogId = "https://cixweb.github.io/a2ui-lab/catalog/v1"

/**
 * The catalog the lab offers to the agent: only what this app can draw well.
 *
 * It lists what is allowed instead of removing what is not, so a new component in a later version
 * of the library does not reach the agent by itself. Left out on purpose:
 * - `List`: it is lazy and crashes a screen that scrolls;
 * - `Card`: [SectionCardComponent] takes its place, so the cards are the kit's;
 * - `Image`, `Video`, `AudioPlayer`: the lab has no image loader and no player.
 *
 * @param functions the client-side functions of the catalog (`openUrl`, `required`, `formatString`…)
 * @return the catalog to register with the message processor
 */
fun labCatalog(functions: A2uiBasicCatalogV1.Functions): A2uiCatalog {
    // The only public way to get the functions; its media components are not offered to the agent.
    val basic =
        materialA2uiBasicCatalogV1(
            image = MaterialA2uiBasicCatalogV1Defaults.image { _, _, _, modifier, _ -> Box(modifier) },
            video = MaterialA2uiBasicCatalogV1Defaults.video { _, modifier, _ -> Box(modifier) },
            audioPlayer =
                MaterialA2uiBasicCatalogV1Defaults.audioPlayer { _, _, modifier, _ -> Box(modifier) },
            functions = functions,
        )
    return A2uiCatalog(
        catalogId = LabCatalogId,
        components =
            listOf(
                SectionCardComponent,
                MaterialA2uiBasicCatalogV1Defaults.text,
                MaterialA2uiBasicCatalogV1Defaults.row,
                MaterialA2uiBasicCatalogV1Defaults.column,
                MaterialA2uiBasicCatalogV1Defaults.divider,
                MaterialA2uiBasicCatalogV1Defaults.button,
                MaterialA2uiBasicCatalogV1Defaults.textField,
                MaterialA2uiBasicCatalogV1Defaults.checkBox,
                MaterialA2uiBasicCatalogV1Defaults.choicePicker,
                MaterialA2uiBasicCatalogV1Defaults.slider,
            ),
        functions = basic.functions,
    )
}
