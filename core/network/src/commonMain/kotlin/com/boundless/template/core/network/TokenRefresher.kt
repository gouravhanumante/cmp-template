package com.boundless.template.core.network

import com.boundless.template.core.datastore.AuthTokens

/** What renewing an expired sign-in gave. */
sealed interface RefreshResult {
    data class Renewed(val tokens: AuthTokens) : RefreshResult

    /** The server said no (revoked, expired, account deleted): the person must sign in again. */
    data object Refused : RefreshResult

    /** No answer (offline, timeout, server down). The sign-in is kept and tried again on the next request. */
    data object Unreachable : RefreshResult
}

/** Exchanges a refresh token for a rotated pair; a null token renews from the browser's cookie. */
fun interface TokenRefresher {
    suspend fun refresh(refreshToken: String?): RefreshResult
}
