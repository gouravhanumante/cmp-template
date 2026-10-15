package com.boundless.template.core.common.model

/** Minutes past midnight, as used by the time pickers in core:designsystem. */
object TimeOfDay {
    const val MINUTES_IN_DAY = 24 * 60

    fun format(minutes: Int): String {
        val hours = (minutes / 60).coerceIn(0, 23)
        val mins = (minutes % 60).coerceIn(0, 59)
        return "${hours.pad()}:${mins.pad()}"
    }

    /** 12-hour AM/PM form for display only. Never fed into [parse] — the wire format stays 24-hour. */
    fun formatDisplay(minutes: Int): String {
        val hours = (minutes / 60).coerceIn(0, 23)
        val mins = (minutes % 60).coerceIn(0, 59)
        val period = if (hours < 12) "AM" else "PM"
        val hour12 = if (hours % 12 == 0) 12 else hours % 12
        return "$hour12:${mins.pad()} $period"
    }

    /** Parses `HH:mm`; null when malformed. */
    fun parse(value: String): Int? {
        val parts = value.split(":")
        if (parts.size != 2) return null
        val hours = parts[0].toIntOrNull() ?: return null
        val minutes = parts[1].toIntOrNull() ?: return null
        if (hours !in 0..23 || minutes !in 0..59) return null
        return hours * 60 + minutes
    }

    private fun Int.pad(): String = toString().padStart(2, '0')
}
