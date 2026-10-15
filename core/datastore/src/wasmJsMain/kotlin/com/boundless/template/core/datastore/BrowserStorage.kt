@file:OptIn(ExperimentalWasmJsInterop::class)

package com.boundless.template.core.datastore

private const val PREFIX = "app."
private const val SESSION_MARKER_KEY = PREFIX + "session"
private const val VALUE_PREFIX = PREFIX + "kv."

/**
 * The refresh token lives in an HttpOnly cookie and the access token only in memory, so a script on
 * the page can read neither from storage. What survives a reload is a marker saying a cookie should
 * exist; `read()` then returns a blank access token, the API answers 401, and the client renews from
 * the cookie.
 */
internal class BrowserTokenStorage : TokenStorage {
    private var accessToken: String? = null

    override suspend fun read(): AuthTokens? {
        accessToken?.let { return AuthTokens(it, refreshToken = null) }
        return if (storageGet(SESSION_MARKER_KEY) != null) AuthTokens("", refreshToken = null) else null
    }

    override suspend fun save(tokens: AuthTokens) {
        accessToken = tokens.accessToken
        storageSet(SESSION_MARKER_KEY, "1")
    }

    override suspend fun clear() {
        accessToken = null
        storageRemove(SESSION_MARKER_KEY)
    }
}

/** Non-secret values only; anything here is readable and writable by any script on the page. */
internal class BrowserKeyValueStore : KeyValueStore {
    override suspend fun read(key: String): String? = storageGet(VALUE_PREFIX + key)

    override suspend fun write(
        key: String,
        value: String,
    ) = storageSet(VALUE_PREFIX + key, value)

    override suspend fun remove(key: String) = storageRemove(VALUE_PREFIX + key)
}

// Storage throws when blocked or full (e.g. some private modes); the app then behaves as if nothing was saved.
private fun storageGet(key: String): String? =
    js("(() => { try { return window.localStorage.getItem(key); } catch (e) { return null; } })()")

private fun storageSet(
    key: String,
    value: String,
): Unit = js("(() => { try { window.localStorage.setItem(key, value); } catch (e) {} })()")

private fun storageRemove(key: String): Unit =
    js("(() => { try { window.localStorage.removeItem(key); } catch (e) {} })()")
