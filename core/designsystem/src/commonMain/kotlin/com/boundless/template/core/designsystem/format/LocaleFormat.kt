package com.boundless.template.core.designsystem.format

/** Numbers, money and times as a given country writes them, in a given time zone (not the device's). */
interface LocaleFormat {
    fun number(value: Int): String

    /** [minorUnits] are paise, cents; whole amounts drop the decimals: ₹95, not ₹95.00. */
    fun money(minorUnits: Long): String

    /** e.g. "Thu 15 Oct, 12:00 pm". [iso] is an ISO-8601 instant with offset. */
    fun dateTime(iso: String): String

    /** e.g. "12:00 pm". */
    fun time(iso: String): String

    /** e.g. "Thu". */
    fun weekday(iso: String): String

    /** e.g. "15 Oct". */
    fun dayMonth(iso: String): String
}

/** [locale] like "en-IN", [currency] like "INR", [timeZone] like "Asia/Kolkata". */
expect fun localeFormat(
    locale: String,
    currency: String,
    timeZone: String,
): LocaleFormat
