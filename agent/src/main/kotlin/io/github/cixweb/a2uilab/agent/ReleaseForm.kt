package io.github.cixweb.a2uilab.agent

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

/**
 * The components of the release form, as JSON an agent would send.
 *
 * In these raw strings `${'$'}` is Kotlin for a literal dollar sign, so `${'$'}{/version}` reaches
 * the JSON as `${/version}`: a `formatString` expression that reads `/version` from the data model.
 */
internal object ReleaseForm {

    /** A version name must look like 2.4.0; used by the text field and by the button. */
    private const val VERSION_CHECK =
        """{"condition": {"call": "regex", "args": {"value": {"path": "/version"},
           "pattern": "^\\d+\\.\\d+\\.\\d+${'$'}"}}, "message": "Use major.minor.patch."}"""

    /**
     * The form and the history, all but the rows of the history. The inputs write to the data model
     * on the device; the button stays disabled until its `checks` pass, and its `action` sends the
     * values named in `context`. `SectionCard` is the app's own component, not one of the Basic
     * Catalog: the agent reads its properties from the contract like those of any other.
     *
     * `release_row` is the template of [HISTORY_ROWS]. Inside a row, paths without a leading slash
     * (`stages`) read from that item.
     */
    private const val FORM_AND_HISTORY =
        """
          {"id": "root", "component": "Column", "align": "stretch",
           "children": ["form_card", "history_card"]},
          {"id": "form_card", "component": "SectionCard",
           "title": {"call": "formatString", "args": {"value": "Release ${'$'}{/version}"}},
           "child": "content"},
          {"id": "content", "component": "Column", "align": "stretch",
           "children": ["notes", "divider", "track", "rollout", "changelog", "version", "prepare"]},
          {"id": "notes", "component": "Text", "text": {"path": "/notes"}},
          {"id": "divider", "component": "Divider"},
          {"id": "track", "component": "ChoicePicker", "label": "Track",
           "variant": "mutuallyExclusive", "displayStyle": "chips",
           "options": [
             {"label": "Internal", "value": "internal"},
             {"label": "Beta", "value": "beta"},
             {"label": "Production", "value": "production"}
           ],
           "value": {"path": "/track"}},
          {"id": "rollout", "component": "Slider", "label": "Rollout %",
           "min": 1, "max": 100, "value": {"path": "/rollout"}},
          {"id": "changelog", "component": "CheckBox", "label": "Changelog reviewed",
           "value": {"path": "/changelogReviewed"}},
          {"id": "version", "component": "TextField", "label": "Version name",
           "value": {"path": "/version"},
           "checks": [$VERSION_CHECK]},
          {"id": "prepare", "component": "Button", "variant": "primary",
           "child": "prepare_label",
           "checks": [
             $VERSION_CHECK,
             {"condition": {"path": "/changelogReviewed"},
              "message": "Review the changelog first."}
           ],
           "action": {"event": {"name": "prepare_release", "context": {
             "version": {"path": "/version"},
             "track": {"path": "/track"},
             "rollout": {"path": "/rollout"}
           }}}},
          {"id": "prepare_label", "component": "Text", "text": "Prepare release"},
          {"id": "history_card", "component": "SectionCard", "title": "Recent releases",
           "child": "history_rows"},
          {"id": "release_row", "component": "Row", "align": "center",
           "children": ["row_release", "row_stages"]},
          {"id": "row_release", "component": "Text", "weight": 1,
           "text": {"call": "formatString", "args": {"value": "${'$'}{version} on ${'$'}{track}"}}},
          {"id": "row_stages", "component": "Text", "variant": "caption",
           "text": {"path": "stages"}}
        """

    /** One `release_row` for each item of `/releases`. */
    private const val HISTORY_ROWS =
        """{"id": "history_rows", "component": "Column", "align": "stretch",
            "children": {"path": "/releases", "componentId": "release_row"}}"""

    /** The same rows as a `List`, which the app does not offer: this one comes back as an error. */
    private const val HISTORY_ROWS_IN_A_LIST =
        """{"id": "history_rows", "component": "List", "direction": "vertical",
            "children": {"path": "/releases", "componentId": "release_row"}}"""

    /** The screen the agent draws. */
    const val COMPONENTS = "[$FORM_AND_HISTORY, $HISTORY_ROWS]"

    /** The same screen as a model could write it, with one component outside the contract. */
    const val COMPONENTS_WITH_A_LIST = "[$FORM_AND_HISTORY, $HISTORY_ROWS_IN_A_LIST]"

    /**
     * What the agent adds once it has a plan. `updateComponents` matches by id, so sending
     * `content` again replaces it: that is how the new section enters the card.
     */
    const val PLAN_COMPONENTS =
        """[
          {"id": "content", "component": "Column", "align": "stretch",
           "children": ["notes", "divider", "track", "rollout", "changelog", "version", "prepare",
                        "plan_divider", "plan_title", "plan_stages", "restart"]},
          {"id": "plan_divider", "component": "Divider"},
          {"id": "plan_title", "component": "Text", "variant": "h4",
           "text": {"call": "formatString", "args": {
             "value": "Plan for ${'$'}{/plan/version} on ${'$'}{/plan/track}"}}},
          {"id": "plan_stages", "component": "Text", "text": {"path": "/plan/stages"}},
          {"id": "restart", "component": "Button", "variant": "borderless",
           "child": "restart_label",
           "action": {"event": {"name": "start_over"}}},
          {"id": "restart_label", "component": "Text", "text": "Start over"}
        ]"""

    /**
     * The data of the form when it opens: the changelog is not reviewed, so the button is off.
     *
     * @param releases the releases the agent remembers, oldest first
     * @return the whole data model of the surface
     */
    fun formData(releases: List<JsonObject>): JsonObject = buildJsonObject {
        put("version", "2.4.0")
        put("notes", "Offline drafts and a faster cold start.")
        putJsonArray("track") { add("beta") }
        put("rollout", 20)
        put("changelogReviewed", false)
        put("releases", JsonArray(releases))
    }
}
