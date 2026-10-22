package com.boundless.template.app

import androidx.lifecycle.ViewModel
import com.boundless.template.core.designsystem.theme.AppPalette
import com.boundless.template.core.designsystem.theme.ThemeSettings

class SettingsViewModel(private val settings: ThemeSettings) : ViewModel() {
    val palette = settings.palette

    fun select(palette: AppPalette) = settings.select(palette)
}