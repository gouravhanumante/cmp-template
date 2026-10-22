package com.boundless.template.core.designsystem.format

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

// java.text groups only in threes; India writes 1,00,000 and 1,23,45,678 (lakhs, crores).
private val THREE_GROUPS = Regex("\\d{1,3}(,\\d{3})+")

private fun indianGrouping(text: String): String =
    THREE_GROUPS.replace(text) { match ->
        val digits = match.value.replace(",", "")
        val last3 = digits.takeLast(3)
        val rest = digits.dropLast(3)
        if (rest.isEmpty()) last3 else rest.reversed().chunked(2).joinToString(",").reversed() + "," + last3
    }

actual fun localeFormat(
    locale: String,
    currency: String,
    timeZone: String,
): LocaleFormat {
    val place = Locale.forLanguageTag(locale)
    val group: (String) -> String = if (place.country == "IN") ::indianGrouping else { text -> text }
    val money = Currency.getInstance(currency)
    val dateTime = DateTimeFormatter.ofPattern("EEE d MMM, h:mm a", place).withZone(ZoneId.of(timeZone))
    val pattern = { text: String -> DateTimeFormatter.ofPattern(text, place).withZone(ZoneId.of(timeZone)) }
    val time = pattern("h:mm a")
    val weekday = pattern("EEE")
    val dayMonth = pattern("d MMM")
    val instant = { iso: String -> Instant.ofEpochMilli(kotlin.time.Instant.parse(iso).toEpochMilliseconds()) }
    return object : LocaleFormat {
        override fun number(value: Int): String = group(NumberFormat.getIntegerInstance(place).format(value))

        override fun money(minorUnits: Long): String {
            val amount = BigDecimal.valueOf(minorUnits, money.defaultFractionDigits)
            val digits = if (amount.stripTrailingZeros().scale() <= 0) 0 else money.defaultFractionDigits
            val format = NumberFormat.getCurrencyInstance(place)
            format.currency = money
            format.minimumFractionDigits = digits
            format.maximumFractionDigits = digits
            return group(format.format(amount))
        }

        override fun dateTime(iso: String): String = dateTime.format(instant(iso))

        override fun time(iso: String): String = time.format(instant(iso))

        override fun weekday(iso: String): String = weekday.format(instant(iso))

        override fun dayMonth(iso: String): String = dayMonth.format(instant(iso))
    }
}
