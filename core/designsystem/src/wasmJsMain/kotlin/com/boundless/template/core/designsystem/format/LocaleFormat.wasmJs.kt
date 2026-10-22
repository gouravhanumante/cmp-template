@file:OptIn(ExperimentalWasmJsInterop::class)

package com.boundless.template.core.designsystem.format

import kotlin.math.pow
import kotlin.time.Instant

// The browser's Intl already knows each country's grouping, including India's lakhs and crores.
actual fun localeFormat(
    locale: String,
    currency: String,
    timeZone: String,
): LocaleFormat {
    val digits = currencyDigits(locale, currency)
    val scale = 10.0.pow(digits).toLong()
    val date = { iso: String, parts: String ->
        formatDate(locale, timeZone, Instant.parse(iso).toEpochMilliseconds().toDouble(), parts)
    }
    return object : LocaleFormat {
        override fun number(value: Int): String = formatNumber(locale, value)

        override fun money(minorUnits: Long): String {
            val shown = if (minorUnits % scale == 0L) 0 else digits
            return formatMoney(locale, currency, minorUnits.toDouble() / scale, shown)
        }

        override fun dateTime(iso: String): String =
            date(iso, """{"weekday":"short","day":"numeric","month":"short","hour":"numeric","minute":"2-digit"}""")

        override fun time(iso: String): String = date(iso, """{"hour":"numeric","minute":"2-digit"}""")

        override fun weekday(iso: String): String = date(iso, """{"weekday":"short"}""")

        override fun dayMonth(iso: String): String = date(iso, """{"day":"numeric","month":"short"}""")
    }
}

private fun formatNumber(
    locale: String,
    value: Int,
): String = js("new Intl.NumberFormat(locale, { maximumFractionDigits: 0 }).format(value)")

private fun currencyDigits(
    locale: String,
    currency: String,
): Int = js("new Intl.NumberFormat(locale, { style: 'currency', currency: currency }).resolvedOptions().maximumFractionDigits")

private fun formatMoney(
    locale: String,
    currency: String,
    amount: Double,
    digits: Int,
): String =
    js(
        "new Intl.NumberFormat(locale, { style: 'currency', currency: currency, minimumFractionDigits: digits, maximumFractionDigits: digits }).format(amount)",
    )

private fun formatDate(
    locale: String,
    timeZone: String,
    epochMillis: Double,
    parts: String,
): String = js("new Intl.DateTimeFormat(locale, Object.assign({ timeZone: timeZone }, JSON.parse(parts))).format(new Date(epochMillis))")
