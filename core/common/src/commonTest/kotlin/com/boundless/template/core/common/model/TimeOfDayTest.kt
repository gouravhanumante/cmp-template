package com.boundless.template.core.common.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TimeOfDayTest {
    @Test
    fun formatsMinutesPastMidnightAsZeroPaddedClockTime() {
        assertEquals("00:00", TimeOfDay.format(0))
        assertEquals("09:05", TimeOfDay.format(545))
        assertEquals("23:59", TimeOfDay.format(1439))
    }

    @Test
    fun parsesValidClockTimes() {
        assertEquals(0, TimeOfDay.parse("00:00"))
        assertEquals(545, TimeOfDay.parse("09:05"))
        assertEquals(1439, TimeOfDay.parse("23:59"))
    }

    @Test
    fun rejectsOutOfRangeOrMalformedInput() {
        assertNull(TimeOfDay.parse("24:00"))
        assertNull(TimeOfDay.parse("12:60"))
        assertNull(TimeOfDay.parse("1230"))
        assertNull(TimeOfDay.parse("ab:cd"))
        assertNull(TimeOfDay.parse(""))
    }

    @Test
    fun roundTripsEveryMinuteOfTheDay() {
        for (minute in 0 until TimeOfDay.MINUTES_IN_DAY) {
            assertEquals(minute, TimeOfDay.parse(TimeOfDay.format(minute)))
        }
    }
}
