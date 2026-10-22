package com.boundless.template.feature.devtools.presentation

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** "2026-10-22 21:05" in the device's zone; developer pages need precision, not locale polish. */
internal fun formatTimestamp(epochMillis: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    val second = local.second.toString().padStart(2, '0')
    return "${local.date} $hour:$minute:$second"
}
