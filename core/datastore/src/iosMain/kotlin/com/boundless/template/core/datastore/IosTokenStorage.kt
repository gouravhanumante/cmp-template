package com.boundless.template.core.datastore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class IosTokenStorage(
    private val keychain: KeychainStore,
) : TokenStorage {
    override suspend fun read(): AuthTokens? =
        withContext(Dispatchers.Default) {
            val access = keychain.read(ACCESS_ACCOUNT) ?: return@withContext null
            val refresh = keychain.read(REFRESH_ACCOUNT) ?: return@withContext null
            AuthTokens(accessToken = access, refreshToken = refresh)
        }

    override suspend fun save(tokens: AuthTokens) {
        withContext(Dispatchers.Default) {
            keychain.write(ACCESS_ACCOUNT, tokens.accessToken)
            tokens.refreshToken?.let { keychain.write(REFRESH_ACCOUNT, it) } ?: keychain.delete(REFRESH_ACCOUNT)
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.Default) {
            keychain.delete(ACCESS_ACCOUNT)
            keychain.delete(REFRESH_ACCOUNT)
        }
    }

    private companion object {
        const val ACCESS_ACCOUNT = "access_token"
        const val REFRESH_ACCOUNT = "refresh_token"
    }
}
