package io.github.cixweb.a2uilab.a2ui

import androidx.a2ui.compose.ui.toJsonSchemaString
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Keeps `contract/catalog.schema.json` equal to the catalog the app registers.
 *
 * The file is what the agent reads to know which components and properties exist, so it must never
 * drift from the app. On a change, the test writes the new schema under `build/` and fails.
 */
class CatalogContractTest {

    @Test
    fun contractMatchesTheCatalog() {
        val schema = testCatalog.toJsonSchemaString()
        val generated = File("build/a2ui/catalog.schema.json")
        generated.parentFile?.mkdirs()
        generated.writeText(schema)

        val contract = File("../contract/catalog.schema.json")
        assertTrue("Missing ${contract.path}: copy app/${generated.path}", contract.isFile)
        assertEquals(
            "The catalog changed: copy app/${generated.path} to contract/",
            contract.readText(),
            schema,
        )
    }
}
