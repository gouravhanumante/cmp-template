package com.boundless.template.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boundless.template.core.common.AppMessenger
import com.boundless.template.core.designsystem.theme.AppPalette
import com.boundless.template.core.designsystem.theme.ThemeSettings
import com.boundless.template.core.network.AppGateEvents
import com.boundless.template.core.network.AppGateSignal
import com.boundless.template.core.network.SessionEndReason
import com.boundless.template.core.network.SessionEvents
import com.boundless.template.core.resources.Res
import com.boundless.template.core.resources.UiText
import com.boundless.template.core.resources.app_session_expired
import com.boundless.template.core.resources.asText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AppDestination {
    data object Main : AppDestination

    data object Maintenance : AppDestination

    data object UpdateRequired : AppDestination
}

/** Decides what the root shows. Screens never handle app-wide gates themselves. */
class AppViewModel(
    appGateEvents: AppGateEvents,
    sessionEvents: SessionEvents,
    appMessenger: AppMessenger,
    appReadyState: AppReadyState,
    themeSettings: ThemeSettings,
) : ViewModel() {
    private val _destination = MutableStateFlow<AppDestination>(AppDestination.Main)
    val destination: StateFlow<AppDestination> = _destination.asStateFlow()

    val palette: StateFlow<AppPalette> = themeSettings.palette

    val messages: Flow<UiText> = appMessenger.messages

    init {
        appReadyState.markReady()
        viewModelScope.launch {
            appGateEvents.events.collect { signal ->
                _destination.value =
                    when (signal) {
                        AppGateSignal.MAINTENANCE -> AppDestination.Maintenance
                        AppGateSignal.UPDATE_REQUIRED -> AppDestination.UpdateRequired
                    }
            }
        }
        // Signing out on purpose needs no message; a refused renewal does, or the person just sees screens fail.
        viewModelScope.launch {
            sessionEvents.events.collect { reason ->
                when (reason) {
                    SessionEndReason.SESSION_EXPIRED -> appMessenger.show(Res.string.app_session_expired.asText())
                    SessionEndReason.USER_INITIATED -> Unit
                }
            }
        }
    }

    fun onRetry() {
        _destination.value = AppDestination.Main
    }
}
