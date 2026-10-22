package com.boundless.template.core.database.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.boundless.template.core.common.applicationSupportPath
import com.boundless.template.core.database.AppDatabase
import com.boundless.template.core.database.DATABASE_FILE
import org.koin.core.module.Module
import org.koin.dsl.module

// Application Support: backed up, not shown in the Files app.
internal actual val coreDatabasePlatformModule: Module =
    module {
        single<RoomDatabase.Builder<AppDatabase>> {
            Room.databaseBuilder<AppDatabase>(name = "${applicationSupportPath()}/$DATABASE_FILE")
                .setDriver(BundledSQLiteDriver())
        }
    }
