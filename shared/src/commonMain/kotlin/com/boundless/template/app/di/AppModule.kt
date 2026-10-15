package com.boundless.template.app.di

import com.boundless.template.app.AppReadyState
import com.boundless.template.app.AppViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule: Module =
    module {
        singleOf(::AppReadyState)
        viewModelOf(::AppViewModel)
    }
