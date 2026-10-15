package com.boundless.template.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boundless.template.core.common.AppMessenger
import com.boundless.template.core.network.AppGateEvents
import com.boundless.template.core.network.AppGateSignal
import com.boundless.template.core.resources.UiText
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
    appMessenger: AppMessenger,
    appReadyState: AppReadyState,
) : ViewModel() {
    private val _destination = MutableStateFlow<AppDestination>(AppDestination.Main)
    val destination: StateFlow<AppDestination> = _destination.asStateFlow()

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
    }

    fun onRetry() {
        _destination.value = AppDestination.Main
    }
}
