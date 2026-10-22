package com.boundless.template.core.designsystem.format

import kotlin.test.Test
import kotlin.test.assertEquals

// Money is kept in minor units; the screen shows it as the chosen country writes it.
class LocaleFormatTest {
    @Test
    fun wholeAmountsDropTheDecimalsAndPartAmountsKeepThem() {
        assertEquals("₹95", localeFormat("en-IN", "INR", "Asia/Kolkata").money(9_500))
        assertEquals("$95.50", localeFormat("en-US", "USD", "America/New_York").money(9_550))
    }

    @Test
    fun aCurrencyWithoutMinorUnitsIsNotDividedByAHundred() {
        assertEquals("¥500", localeFormat("en-US", "JPY", "Asia/Tokyo").money(500))
    }

    @Test
    fun indiaGroupsInLakhsAndCroresAndOtherCountriesInThousands() {
        val india = localeFormat("en-IN", "INR", "Asia/Kolkata")
        assertEquals("1,00,000", india.number(100_000))
        assertEquals("1,23,45,678", india.number(12_345_678))
        assertEquals("999", india.number(999))
        assertEquals("₹12,34,567.50", india.money(123_456_750))
        assertEquals("100,000", localeFormat("en-US", "USD", "America/New_York").number(100_000))
    }

    @Test
    fun timesAreShownInTheGivenTimeZoneNotTheDevices() {
        assertEquals("Fri 16 Oct, 3:00 pm", localeFormat("en-IN", "INR", "Asia/Kolkata").dateTime("2026-10-16T09:30:00Z"))
    }

    // 8:30 pm UTC on Thursday is already Friday 2 am in India: the day shown must be Friday.
    @Test
    fun theDayAndTimePartsAreAlsoInTheGivenTimeZone() {
        val india = localeFormat("en-IN", "INR", "Asia/Kolkata")
        assertEquals("Fri", india.weekday("2026-10-15T20:30:00Z"))
        assertEquals("16 Oct", india.dayMonth("2026-10-15T20:30:00Z"))
        assertEquals("2:00 am", india.time("2026-10-15T20:30:00Z"))
    }
}
