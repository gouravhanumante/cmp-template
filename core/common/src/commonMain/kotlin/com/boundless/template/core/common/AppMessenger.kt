package com.boundless.template.core.common

import com.boundless.template.core.resources.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * App-wide channel for a one-off message the user should see, drained by the single snackbar host
 * in `App`. It exists for the state holders whose UI has no scaffold to host a snackbar of its
 * own — a banner rendered inside another feature's screen, for instance — which would otherwise
 * have no way to report that an action failed.
 *
 * Buffered and fire-and-forget: raising a message must never suspend the work that failed, and a
 * message with nothing listening yet is held rather than dropped.
 */
class AppMessenger {
    private val _messages = Channel<UiText>(Channel.BUFFERED)
    val messages: Flow<UiText> = _messages.receiveAsFlow()

    fun show(message: UiText) {
        _messages.trySend(message)
    }
}
