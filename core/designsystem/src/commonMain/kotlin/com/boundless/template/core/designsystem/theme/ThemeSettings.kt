package com.boundless.template.core.designsystem.theme

import kotlinx.coroutines.flow.StateFlow

/** The colour theme the person picked, kept per device. The app shell implements it over core:datastore. */
interface ThemeSettings {
    val palette: StateFlow<AppPalette>

    fun select(palette: AppPalette)
}
