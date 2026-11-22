package ai.elimu.kukariri

import org.junit.Assert.assertEquals
import org.junit.Test

class MasteryStarsTest {

    @Test
    fun starsFor_thresholds() {
        assertEquals(0, starsFor(0.00f))
        assertEquals(0, starsFor(0.19f))
        assertEquals(1, starsFor(0.20f))
        assertEquals(2, starsFor(0.35f))
        assertEquals(3, starsFor(0.50f))
        assertEquals(4, starsFor(0.65f))
        assertEquals(5, starsFor(0.80f))
        assertEquals(MAX_STARS, starsFor(1.00f))
    }
}
