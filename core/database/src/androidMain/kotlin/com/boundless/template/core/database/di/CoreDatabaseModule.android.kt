package com.boundless.template.core.database.di

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.boundless.template.core.database.AppDatabase
import com.boundless.template.core.database.DATABASE_FILE
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

// The app's private databases folder.
internal actual val coreDatabasePlatformModule: Module =
    module {
        single<RoomDatabase.Builder<AppDatabase>> {
            val context: Context = androidContext().applicationContext
            Room.databaseBuilder<AppDatabase>(context = context, name = context.getDatabasePath(DATABASE_FILE).absolutePath)
                .setDriver(BundledSQLiteDriver())
        }
    }
