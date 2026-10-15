package com.boundless.template.app

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Lets the native Android splash screen (MainActivity) know once startup has resolved its first
 * real destination, so it can keep showing the app's own logo for that whole window instead of
 * handing off to the in-app [com.boundless.template.app.App]'s generic loading screen — the two would
 * otherwise appear as two different logos in a row. One-way: once ready, always ready, since the
 * splash can only ever be dismissed once.
 */
class AppReadyState {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    fun markReady() {
        _isReady.value = true
    }
}
