package com.boundless.template.core.designsystem.format

import android.icu.text.NumberFormat
import android.icu.util.Currency
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ICU, not java.text: ICU knows Indian grouping (1,00,000).
actual fun localeFormat(
    locale: String,
    currency: String,
    timeZone: String,
): LocaleFormat {
    val place = Locale.forLanguageTag(locale)
    val money = Currency.getInstance(currency)
    val dateTime = DateTimeFormatter.ofPattern("EEE d MMM, h:mm a", place).withZone(ZoneId.of(timeZone))
    val pattern = { text: String -> DateTimeFormatter.ofPattern(text, place).withZone(ZoneId.of(timeZone)) }
    val time = pattern("h:mm a")
    val weekday = pattern("EEE")
    val dayMonth = pattern("d MMM")
    val instant = { iso: String -> Instant.ofEpochMilli(kotlin.time.Instant.parse(iso).toEpochMilliseconds()) }
    return object : LocaleFormat {
        override fun number(value: Int): String = NumberFormat.getIntegerInstance(place).format(value)

        override fun money(minorUnits: Long): String {
            val amount = BigDecimal.valueOf(minorUnits, money.defaultFractionDigits)
            val digits = if (amount.stripTrailingZeros().scale() <= 0) 0 else money.defaultFractionDigits
            val format = NumberFormat.getCurrencyInstance(place)
            format.currency = money
            format.minimumFractionDigits = digits
            format.maximumFractionDigits = digits
            return format.format(amount)
        }

        override fun dateTime(iso: String): String = dateTime.format(instant(iso))

        override fun time(iso: String): String = time.format(instant(iso))

        override fun weekday(iso: String): String = weekday.format(instant(iso))

        override fun dayMonth(iso: String): String = dayMonth.format(instant(iso))
    }
}
