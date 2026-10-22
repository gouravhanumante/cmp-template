package com.boundless.template.core.database.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.boundless.template.core.common.desktopAppDataDir
import com.boundless.template.core.database.AppDatabase
import com.boundless.template.core.database.DATABASE_FILE
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

// The per-user app data folder on macOS, Windows or Linux (core:common AppDataFolder).
internal actual val coreDatabasePlatformModule: Module =
    module {
        single<RoomDatabase.Builder<AppDatabase>> {
            Room.databaseBuilder<AppDatabase>(name = File(desktopAppDataDir(), DATABASE_FILE).absolutePath)
                .setDriver(BundledSQLiteDriver())
        }
    }
