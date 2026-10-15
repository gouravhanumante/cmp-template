package com.boundless.template.core.designsystem.component

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Someone dials their hours on a clock face, where :40 is as easy to land on as :30. A server that
 * narrows a saved window to a 30-minute slot grid means this field has to move the time the same way
 * — otherwise they confirm 09:30 and 10:00 is what people can actually book.
 */
class TimeSnapTest {

    @Test
    fun `the opening end of a window never moves earlier than the time chosen`() {
        // 09:30 would sell the half hour before she said she was free.
        assertEquals(10 * 60, TimeSnap.UP.applyTo(9 * 60 + 40, 30))
    }

    @Test
    fun `the closing end never moves later than the time chosen`() {
        // 17:30 would keep her out half an hour past when she said she was done.
        assertEquals(17 * 60, TimeSnap.DOWN.applyTo(17 * 60 + 20, 30))
    }

    @Test
    fun `a time already on the grid is left exactly where it is`() {
        assertEquals(10 * 60, TimeSnap.UP.applyTo(10 * 60, 30))
        assertEquals(10 * 60, TimeSnap.DOWN.applyTo(10 * 60, 30))
        assertEquals(10 * 60, TimeSnap.NEAREST.applyTo(10 * 60, 30))
    }

    @Test
    fun `nearest goes whichever way is closer`() {
        assertEquals(10 * 60, TimeSnap.NEAREST.applyTo(9 * 60 + 50, 30))
        assertEquals(9 * 60 + 30, TimeSnap.NEAREST.applyTo(9 * 60 + 40, 30))
    }
}
