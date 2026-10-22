@file:OptIn(ExperimentalWasmJsInterop::class)

package com.boundless.template.core.database.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import com.boundless.template.core.database.AppDatabase
import com.boundless.template.core.database.DATABASE_FILE
import org.koin.core.module.Module
import org.koin.dsl.module
import org.w3c.dom.Worker

// The worker keeps the file in the browser's Origin Private File System (core/database/worker).
internal actual val coreDatabasePlatformModule: Module =
    module {
        single<RoomDatabase.Builder<AppDatabase>> {
            Room.databaseBuilder<AppDatabase>(name = DATABASE_FILE).setDriver(WebWorkerSQLiteDriver(sqliteWorker()))
        }
    }

// Webpack sees this exact `new Worker(new URL(...))` shape and bundles the worker with its imports.
private fun sqliteWorker(): Worker = js("""new Worker(new URL("app-sqlite-worker/worker.js", import.meta.url))""")
