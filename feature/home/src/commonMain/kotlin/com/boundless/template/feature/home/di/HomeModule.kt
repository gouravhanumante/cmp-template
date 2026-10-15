package com.boundless.template.feature.home.di

import com.boundless.template.core.network.di.UnauthenticatedClient
import com.boundless.template.feature.home.data.HealthApi
import com.boundless.template.feature.home.data.HealthRepositoryImpl
import com.boundless.template.feature.home.domain.HealthRepository
import com.boundless.template.feature.home.presentation.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule: Module =
    module {
        single { HealthApi(get(UnauthenticatedClient)) }
        single<HealthRepository> { HealthRepositoryImpl(get()) }
        viewModelOf(::HomeViewModel)
    }
