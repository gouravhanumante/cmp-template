package com.boundless.template.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.boundless.template.core.common.desktopAppDataDir
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
private const val PREFERENCES_FILE = "app_prefs.preferences_pb"

actual val coreDatastorePlatformModule: Module =
    module {
        single<TokenStorage> { DesktopTokenStorage(KEYRING_SERVICE) }
        single<DataStore<Preferences>> {
            PreferenceDataStoreFactory.createWithPath(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { File(desktopAppDataDir(), PREFERENCES_FILE).absolutePath.toPath() },
            )
        }
        single<KeyValueStore> { DataStoreKeyValueStore(get()) }
    }
