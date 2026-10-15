package com.boundless.template.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.boundless.template.core.datastore.AndroidTokenStorage
import com.boundless.template.core.datastore.DataStoreKeyValueStore
import com.boundless.template.core.datastore.KeyValueStore
import com.boundless.template.core.datastore.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val SECURE_STORE_FILE = "app_secure.preferences_pb"

actual val coreDatastorePlatformModule: Module =
    module {
        single<DataStore<Preferences>> {
            val context: Context = androidContext()
            PreferenceDataStoreFactory.createWithPath(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { context.filesDir.resolve(SECURE_STORE_FILE).absolutePath.toPath() },
            )
        }
        single<TokenStorage> { AndroidTokenStorage(get()) }
        single<KeyValueStore> { DataStoreKeyValueStore(get()) }
    }
