package com.boundless.template.core.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Re-tapping the tab you are already on means "take me back to the top" on both platforms, and a
 * long feed is where people reach for it. The bottom bar raises it; whichever tab root is on
 * screen answers, which is why this carries no tab identity — only the visible one is composed.
 *
 * Not buffered: a request with nothing listening is a request nobody made, and replaying it later
 * would yank a screen to the top for no reason.
 */
class ScrollToTopEvents {
    private val _events = MutableSharedFlow<Unit>()
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    suspend fun request() {
        _events.emit(Unit)
    }
}
