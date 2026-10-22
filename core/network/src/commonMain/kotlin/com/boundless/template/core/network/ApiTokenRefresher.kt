package com.boundless.template.core.network

import com.boundless.template.core.datastore.AuthTokens
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
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
    override suspend fun refresh(refreshToken: String?): RefreshResult =
        try {
            val response =
                client.post("auth/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(RefreshRequest(refreshToken))
                }
            val status = response.status
            when {
                status.isSuccess() ->
                    response.body<ApiEnvelope<RefreshResponse>>().data
                        ?.let { RefreshResult.Renewed(AuthTokens(it.accessToken, it.refreshToken)) }
                        ?: RefreshResult.Refused
                // The server answered and said no; being rate-limited is not a no.
                status.value in 400..499 && status != HttpStatusCode.TooManyRequests -> RefreshResult.Refused
                else -> RefreshResult.Unreachable
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Throwable) {
            // Includes a captive portal's HTML page that doesn't parse.
            RefreshResult.Unreachable
        }
}
