package com.boundless.template.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Local calendar day. Injected so tests can pin "today" instead of reading the clock. */
fun interface TodayProvider {
    fun today(): LocalDate
}

class SystemTodayProvider(
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) : TodayProvider {
    override fun today(): LocalDate = Clock.System.todayIn(timeZone)
}
