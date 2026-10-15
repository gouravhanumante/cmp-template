package com.boundless.template.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.boundless.template.core.datastore.DataStoreKeyValueStore
import com.boundless.template.core.datastore.IosTokenStorage
import com.boundless.template.core.datastore.KeyValueStore
import com.boundless.template.core.datastore.KeychainStore
import com.boundless.template.core.datastore.TokenStorage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private const val KEYCHAIN_SERVICE = "com.boundless.template.auth"
private const val PREFERENCES_FILE = "app_prefs.preferences_pb"

@OptIn(ExperimentalForeignApi::class)
private fun preferencesPath(): String {
    val documents: NSURL? =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    return requireNotNull(documents?.path) { "Documents directory unavailable" } + "/" + PREFERENCES_FILE
}

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
