package com.boundless.template.core.datastore

import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** macOS Keychain, Windows Credential Manager or Linux Secret Service, via java-keyring. */
internal class DesktopTokenStorage(
    private val service: String,
) : TokenStorage {
    override suspend fun read(): AuthTokens? =
        withContext(Dispatchers.IO) {
            Keyring.create().use { keyring ->
                val access = keyring.getOrNull(ACCESS_ACCOUNT) ?: return@withContext null
                AuthTokens(accessToken = access, refreshToken = keyring.getOrNull(REFRESH_ACCOUNT))
            }
        }

    override suspend fun save(tokens: AuthTokens) {
        withContext(Dispatchers.IO) {
            Keyring.create().use { keyring ->
                keyring.setPassword(service, ACCESS_ACCOUNT, tokens.accessToken)
                val refresh = tokens.refreshToken
                if (refresh != null) keyring.setPassword(service, REFRESH_ACCOUNT, refresh) else keyring.deleteOrIgnore(REFRESH_ACCOUNT)
            }
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            Keyring.create().use { keyring ->
                keyring.deleteOrIgnore(ACCESS_ACCOUNT)
                keyring.deleteOrIgnore(REFRESH_ACCOUNT)
            }
        }
    }

    // java-keyring reports a missing entry as an exception, not a null.
    private fun Keyring.getOrNull(account: String): String? =
        try {
            getPassword(service, account)
        } catch (_: PasswordAccessException) {
            null
        }

    private fun Keyring.deleteOrIgnore(account: String) {
        try {
            deletePassword(service, account)
        } catch (_: PasswordAccessException) {
        }
    }

    private companion object {
        const val ACCESS_ACCOUNT = "access_token"
        const val REFRESH_ACCOUNT = "refresh_token"
    }
}
