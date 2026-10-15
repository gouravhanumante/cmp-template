package com.boundless.template.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

/**
 * Plain (unencrypted) key-value storage for non-sensitive data such as the cached server
 * config. Secrets belong in [TokenStorage] instead.
 */
interface KeyValueStore {
    suspend fun read(key: String): String?

    suspend fun write(key: String, value: String)

    suspend fun remove(key: String)
}

internal class DataStoreKeyValueStore(
    private val dataStore: DataStore<Preferences>,
) : KeyValueStore {
    override suspend fun read(key: String): String? = dataStore.data.first()[stringPreferencesKey(key)]

    override suspend fun write(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    override suspend fun remove(key: String) {
        dataStore.edit { it.remove(stringPreferencesKey(key)) }
    }
}
