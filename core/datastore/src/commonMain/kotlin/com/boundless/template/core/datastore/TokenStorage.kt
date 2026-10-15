package com.boundless.template.core.datastore

/**
 * Persists the access/refresh pair in platform secure storage
 * (Android Keystore-encrypted DataStore, iOS Keychain). Never log these values.
 */
interface TokenStorage {
    suspend fun read(): AuthTokens?

    suspend fun save(tokens: AuthTokens)

    suspend fun clear()
}
