package com.boundless.template.core.datastore

data class AuthTokens(
    val accessToken: String,
    /** Null on the web, where the browser holds it in an HttpOnly cookie this code cannot read. */
    val refreshToken: String?,
)
