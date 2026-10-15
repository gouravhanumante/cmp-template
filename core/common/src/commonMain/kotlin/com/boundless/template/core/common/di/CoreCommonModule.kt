package com.boundless.template.core.common.di

import com.boundless.template.core.common.AppMessenger
import com.boundless.template.core.common.DefaultDispatcherProvider
import com.boundless.template.core.common.DispatcherProvider
import com.boundless.template.core.common.ScrollToTopEvents
import com.boundless.template.core.common.SystemTodayProvider
import com.boundless.template.core.common.TodayProvider
import org.koin.dsl.module

val coreCommonModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<TodayProvider> { SystemTodayProvider() }
    single { AppMessenger() }
    single { ScrollToTopEvents() }
}
