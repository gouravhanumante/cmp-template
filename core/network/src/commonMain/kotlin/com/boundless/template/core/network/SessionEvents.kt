package com.boundless.template.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class SessionEndReason {
    /** Refresh token was rejected — the user must sign in again. */
    SESSION_EXPIRED,

    USER_INITIATED,
}

/**
 * App-wide signal that the session is over. Emitted from the token refresh path,
 * observed once at the navigation root so no screen has to handle logout itself.
 */
class SessionEvents {
    private val _events = MutableSharedFlow<SessionEndReason>(extraBufferCapacity = 1)
    val events: SharedFlow<SessionEndReason> = _events.asSharedFlow()

    fun notifySessionEnded(reason: SessionEndReason) {
        _events.tryEmit(reason)
    }
}
