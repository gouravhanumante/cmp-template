package com.boundless.template.core.network

import com.boundless.template.core.common.getOrNull
import com.boundless.template.core.datastore.AuthTokens
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
private data class RefreshRequest(
    val refreshToken: String?,
)

@Serializable
private data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String? = null,
)

/** Uses the unauthenticated client so refreshing can never recurse into itself. */
internal class ApiTokenRefresher(
    private val client: HttpClient,
) : TokenRefresher {
    override suspend fun refresh(refreshToken: String?): AuthTokens? {
        val result: com.boundless.template.core.common.AppResult<RefreshResponse> =
            apiRequest {
                client.post("auth/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(RefreshRequest(refreshToken))
                }
            }
        return result.getOrNull()?.let { AuthTokens(it.accessToken, it.refreshToken) }
    }
}
