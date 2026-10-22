package com.boundless.template.app.di

import com.boundless.template.app.AppReadyState
import com.boundless.template.app.AppViewModel
import com.boundless.template.app.StoredThemeSettings
import com.boundless.template.app.SettingsViewModel
import com.boundless.template.core.designsystem.theme.ThemeSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule: Module =
    module {
        singleOf(::AppReadyState)
        // App-lifetime scope: saving a theme must finish even if the screen that chose it closes.
        single<ThemeSettings> { StoredThemeSettings(get(), CoroutineScope(SupervisorJob() + Dispatchers.Default)) }
        viewModelOf(::AppViewModel)
        viewModelOf(::SettingsViewModel)
    }
