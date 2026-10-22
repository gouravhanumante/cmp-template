package com.boundless.template.feature.devtools.di

import com.boundless.template.core.database.databaseStorage
import com.boundless.template.core.network.di.UnauthenticatedClient
import com.boundless.template.feature.devtools.data.CoreChecksRepositoryImpl
import com.boundless.template.feature.devtools.data.DevHealthApi
import com.boundless.template.feature.devtools.data.RoomNotesRepository
import com.boundless.template.feature.devtools.domain.CoreChecksRepository
import com.boundless.template.feature.devtools.domain.NotesRepository
import com.boundless.template.feature.devtools.presentation.checks.ChecksViewModel
import com.boundless.template.feature.devtools.presentation.components.ComponentsViewModel
import com.boundless.template.feature.devtools.presentation.database.DatabaseViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val devToolsModule: Module =
    module {
        single<NotesRepository> { RoomNotesRepository(get(), get(), get(), databaseStorage()) }
        single { DevHealthApi(get(UnauthenticatedClient)) }
        single<CoreChecksRepository> { CoreChecksRepositoryImpl(get(), get(), get(), get(), get(), get()) }
        viewModelOf(::DatabaseViewModel)
        viewModelOf(::ChecksViewModel)
        viewModelOf(::ComponentsViewModel)
    }
