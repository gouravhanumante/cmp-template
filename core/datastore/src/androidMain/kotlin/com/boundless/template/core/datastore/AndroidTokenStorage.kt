package com.boundless.template.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

internal class AndroidTokenStorage(
    private val dataStore: DataStore<Preferences>,
) : TokenStorage {
    private val accessKey = stringPreferencesKey("access_token")
    private val refreshKey = stringPreferencesKey("refresh_token")

    override suspend fun read(): AuthTokens? {
        val prefs = dataStore.data.first()
        val access = prefs[accessKey]?.let(KeystoreCipher::decrypt) ?: return null
        val refresh = prefs[refreshKey]?.let(KeystoreCipher::decrypt) ?: return null
        return AuthTokens(accessToken = access, refreshToken = refresh)
    }

    override suspend fun save(tokens: AuthTokens) {
        dataStore.edit { prefs ->
            prefs[accessKey] = KeystoreCipher.encrypt(tokens.accessToken)
            val refresh = tokens.refreshToken
            if (refresh != null) prefs[refreshKey] = KeystoreCipher.encrypt(refresh) else prefs.remove(refreshKey)
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
