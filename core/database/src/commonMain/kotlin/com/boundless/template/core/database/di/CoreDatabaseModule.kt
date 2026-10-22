package com.boundless.template.core.database.di

import androidx.room3.RoomDatabase
import com.boundless.template.core.database.AppDatabase
import com.boundless.template.core.database.NoteDao
import org.koin.core.module.Module
import org.koin.dsl.module

/** Supplies a `RoomDatabase.Builder` with this platform's file location and SQLite driver. */
internal expect val coreDatabasePlatformModule: Module

val coreDatabaseModule: Module =
    module {
        includes(coreDatabasePlatformModule)
        single<AppDatabase> { get<RoomDatabase.Builder<AppDatabase>>().build() }
        single<NoteDao> { get<AppDatabase>().noteDao() }
    }
