package com.boundless.template.core.network

import com.boundless.template.core.datastore.AuthTokens

/**
 * Exchanges a refresh token for a rotated pair; a null token renews from the browser's cookie.
 * Returns null when the session is unrecoverable.
 */
fun interface TokenRefresher {
    suspend fun refresh(refreshToken: String?): AuthTokens?
}
