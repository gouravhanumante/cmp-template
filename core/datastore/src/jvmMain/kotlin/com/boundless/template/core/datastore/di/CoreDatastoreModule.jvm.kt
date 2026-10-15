package com.boundless.template.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.boundless.template.core.datastore.DataStoreKeyValueStore
import com.boundless.template.core.datastore.DesktopTokenStorage
import com.boundless.template.core.datastore.KeyValueStore
import com.boundless.template.core.datastore.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

// TODO(template): rename per app.
private const val KEYRING_SERVICE = "com.boundless.template.auth"
private const val APP_DIR_NAME = "BoundlessTemplate"
private const val PREFERENCES_FILE = "app_prefs.preferences_pb"

/** The OS's per-user app data folder: Application Support, %APPDATA%, or XDG data home. */
private fun appDataDir(): File {
    val os = System.getProperty("os.name").lowercase()
    val home = System.getProperty("user.home")
    val base =
        when {
            os.contains("mac") -> File(home, "Library/Application Support")
            os.contains("win") -> File(System.getenv("APPDATA") ?: home)
            else -> File(System.getenv("XDG_DATA_HOME") ?: "$home/.local/share")
        }
    return File(base, APP_DIR_NAME).apply { mkdirs() }
}

actual val coreDatastorePlatformModule: Module =
    module {
        single<TokenStorage> { DesktopTokenStorage(KEYRING_SERVICE) }
        single<DataStore<Preferences>> {
            PreferenceDataStoreFactory.createWithPath(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { File(appDataDir(), PREFERENCES_FILE).absolutePath.toPath() },
            )
        }
        single<KeyValueStore> { DataStoreKeyValueStore(get()) }
    }
