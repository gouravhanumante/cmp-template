package com.boundless.template.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.boundless.template.core.common.applicationSupportPath
import com.boundless.template.core.datastore.DataStoreKeyValueStore
import com.boundless.template.core.datastore.IosTokenStorage
import com.boundless.template.core.datastore.KeyValueStore
import com.boundless.template.core.datastore.KeychainStore
import com.boundless.template.core.datastore.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

private const val KEYCHAIN_SERVICE = "com.boundless.template.auth"
private const val PREFERENCES_FILE = "app_prefs.preferences_pb"

// Application Support, not Documents: Documents is shown in the Files app.
private fun preferencesPath(): String = applicationSupportPath() + "/" + PREFERENCES_FILE

actual val coreDatastorePlatformModule: Module =
    module {
        single { KeychainStore(KEYCHAIN_SERVICE) }
        single<TokenStorage> { IosTokenStorage(get()) }
        single<DataStore<Preferences>> {
            PreferenceDataStoreFactory.createWithPath(
                scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
                produceFile = { preferencesPath().toPath() },
            )
        }
        single<KeyValueStore> { DataStoreKeyValueStore(get()) }
    }
