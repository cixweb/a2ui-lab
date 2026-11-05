package io.github.cixweb.a2uilab.agent

/**
 * Computes the stages of a release rollout. A sample rule, not the policy of any store.
 *
 * This is plain code on purpose: a model may compose the screen, but the numbers on it come from
 * here.
 */
object RolloutPlan {

    private const val INTERNAL_TRACK = "internal"
    private val LADDER = listOf(1, 5, 10, 20, 50, 100)

    /**
     * The percentages of users that get the release, stage after stage.
     *
     * @param track the release track; internal testers get everything at once
     * @param startPercent the first stage, forced into 1..100
     * @return the stages in growing order, always ending at 100
     */
    fun stages(track: String, startPercent: Int): List<Int> {
        if (track == INTERNAL_TRACK) return listOf(100)
        val start = startPercent.coerceIn(1, 100)
        return listOf(start) + LADDER.filter { it > start }
    }
}
