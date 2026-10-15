package com.boundless.template.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class AppGateSignal {
    MAINTENANCE,
    UPDATE_REQUIRED,
}

/** App-wide signal that the server refused a request because the app is gated. Observed at the navigation root. */
class AppGateEvents {
    private val _events = MutableSharedFlow<AppGateSignal>(extraBufferCapacity = 1)
    val events: SharedFlow<AppGateSignal> = _events.asSharedFlow()

    fun notify(signal: AppGateSignal) {
        _events.tryEmit(signal)
    }
}

internal const val APP_GATE_HEADER = "X-App-Gate"
internal const val APP_PLATFORM_HEADER = "X-App-Platform"
internal const val APP_BUILD_HEADER = "X-App-Build"

internal fun appGateSignalFor(headerValue: String?): AppGateSignal? =
    when (headerValue) {
        "maintenance" -> AppGateSignal.MAINTENANCE
        "update-required" -> AppGateSignal.UPDATE_REQUIRED
        else -> null
    }
