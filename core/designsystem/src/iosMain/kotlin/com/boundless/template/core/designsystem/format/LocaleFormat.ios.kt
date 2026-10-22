package com.boundless.template.core.designsystem.format

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeZoneWithName
import kotlin.math.pow

actual fun localeFormat(
    locale: String,
    currency: String,
    timeZone: String,
): LocaleFormat {
    val place = NSLocale(localeIdentifier = locale.replace('-', '_'))
    val numbers = NSNumberFormatter().apply {
        this.locale = place
        numberStyle = NSNumberFormatterDecimalStyle
        maximumFractionDigits = 0u
    }
    val dates = NSDateFormatter().apply {
        this.locale = place
        NSTimeZone.timeZoneWithName(timeZone)?.let { this.timeZone = it }
        setLocalizedDateFormatFromTemplate("EEEdMMMhmm")
    }
    val pattern = { template: String ->
        NSDateFormatter().apply {
            this.locale = place
            NSTimeZone.timeZoneWithName(timeZone)?.let { this.timeZone = it }
            setLocalizedDateFormatFromTemplate(template)
        }
    }
    val times = pattern("hmm")
    val weekdays = pattern("EEE")
    val dayMonths = pattern("dMMM")
    val date = { iso: String -> NSDate.dateWithTimeIntervalSince1970(kotlin.time.Instant.parse(iso).toEpochMilliseconds() / 1000.0) }
    return object : LocaleFormat {
        override fun number(value: Int): String = numbers.stringFromNumber(NSNumber(int = value)) ?: value.toString()

        override fun money(minorUnits: Long): String {
            val format = NSNumberFormatter().apply {
                this.locale = place
                numberStyle = NSNumberFormatterCurrencyStyle
                currencyCode = currency
            }
            val digits = format.maximumFractionDigits.toInt()
            val scale = 10.0.pow(digits).toLong()
            if (minorUnits % scale == 0L) {
                format.minimumFractionDigits = 0u
                format.maximumFractionDigits = 0u
            }
            return format.stringFromNumber(NSNumber(double = minorUnits.toDouble() / scale)) ?: minorUnits.toString()
        }

        override fun dateTime(iso: String): String = dates.stringFromDate(date(iso))

        override fun time(iso: String): String = times.stringFromDate(date(iso))

        override fun weekday(iso: String): String = weekdays.stringFromDate(date(iso))

        override fun dayMonth(iso: String): String = dayMonths.stringFromDate(date(iso))
    }
}
