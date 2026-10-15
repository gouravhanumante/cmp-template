package com.boundless.template.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

/**
 * Drops the bearer token Ktor holds in memory.
 *
 * `loadTokens` runs only once per client and the result is cached for the client's lifetime, so
 * after a sign-out the next user's requests would still go out with the previous user's token.
 */
fun interface AuthTokenInvalidator {
    fun invalidate()
}

internal fun HttpClient.bearerTokenInvalidator(): AuthTokenInvalidator =
    AuthTokenInvalidator { authProvider<BearerAuthProvider>()?.clearToken() }
