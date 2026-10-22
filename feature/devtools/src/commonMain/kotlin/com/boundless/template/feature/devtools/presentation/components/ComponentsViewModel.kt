package com.boundless.template.feature.devtools.presentation.components

import androidx.lifecycle.ViewModel
import com.boundless.template.core.designsystem.theme.AppPalette
import com.boundless.template.core.designsystem.theme.ThemeSettings
import kotlinx.coroutines.flow.StateFlow

/** The gallery's only state is which theme the whole app shows; picking one restyles every screen. */
class ComponentsViewModel(
    private val themeSettings: ThemeSettings,
) : ViewModel() {
    val palette: StateFlow<AppPalette> = themeSettings.palette

    fun onSelectPalette(palette: AppPalette) = themeSettings.select(palette)
}
