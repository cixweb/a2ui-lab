package io.github.cixweb.a2uilab.agent

import org.junit.Assert.assertEquals
import org.junit.Test

class RolloutPlanTest {

    @Test
    fun startsAtTheChosenPercentAndClimbsTheLadder() {
        assertEquals(listOf(20, 50, 100), RolloutPlan.stages("production", 20))
        assertEquals(listOf(35, 50, 100), RolloutPlan.stages("beta", 35))
    }

    @Test
    fun internalTestersGetEverythingAtOnce() {
        assertEquals(listOf(100), RolloutPlan.stages("internal", 20))
    }

    @Test
    fun percentOutsideTheRangeIsForcedIn() {
        assertEquals(listOf(1, 5, 10, 20, 50, 100), RolloutPlan.stages("production", -3))
        assertEquals(listOf(100), RolloutPlan.stages("production", 400))
    }
}
