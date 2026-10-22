package com.boundless.template.core.common

import kotlin.time.Clock

/** The current instant. Injected so tests can pin "now" instead of reading the clock. */
fun interface WallClock {
    fun nowMillis(): Long
}

object SystemWallClock : WallClock {
    override fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
