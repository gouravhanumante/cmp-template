package com.boundless.template.core.network

import com.boundless.template.core.common.currentPlatform
import com.boundless.template.core.datastore.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TIMEOUT_MS = 20_000L

val apiJson: Json =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
        coerceInputValues = true
    }

expect fun httpClientEngine(): HttpClientEngine

fun createUnauthenticatedClient(
    engine: HttpClientEngine,
    config: ApiConfig,
    appGateEvents: AppGateEvents,
    json: Json = apiJson,
): HttpClient = HttpClient(engine) { applyDefaults(config, appGateEvents, json) }

fun createAuthenticatedClient(
    engine: HttpClientEngine,
    config: ApiConfig,
    tokenStorage: TokenStorage,
    tokenRefresher: TokenRefresher,
    sessionEvents: SessionEvents,
    appGateEvents: AppGateEvents,
    json: Json = apiJson,
): HttpClient =
    HttpClient(engine) {
        applyDefaults(config, appGateEvents, json)

        install(Auth) {
            bearer {
                loadTokens {
                    tokenStorage.read()?.let { BearerTokens(it.accessToken, it.refreshToken) }
                }
                refreshTokens {
                    // Ktor serialises this block, so concurrent 401s trigger a single refresh.
                    val stored = tokenStorage.read()
                    val rotated = stored?.let { tokenRefresher.refresh(it.refreshToken) }
                    if (rotated == null) {
                        tokenStorage.clear()
                        // A fresh install has nothing stored, so there is no session to announce
                        // the end of — only a real one that stopped working is worth telling about.
                        if (stored != null) sessionEvents.notifySessionEnded(SessionEndReason.SESSION_EXPIRED)
                        null
                    } else {
                        tokenStorage.save(rotated)
                        BearerTokens(rotated.accessToken, rotated.refreshToken)
                    }
                }
                sendWithoutRequest { true }
            }
        }
    }

private fun HttpClientConfig<*>.applyDefaults(
    config: ApiConfig,
    appGateEvents: AppGateEvents,
    json: Json,
) {
    // Error envelopes carry the meaning of a failure, so non-2xx must reach the caller unthrown.
    expectSuccess = false

    install(ContentNegotiation) { json(json) }

    HttpResponseValidator {
        validateResponse { response ->
            appGateSignalFor(response.headers[APP_GATE_HEADER])?.let(appGateEvents::notify)
        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT_MS
        connectTimeoutMillis = TIMEOUT_MS
        socketTimeoutMillis = TIMEOUT_MS
    }

    if (config.enableLogging) {
        install(Logging) {
            logger =
                object : Logger {
                    override fun log(message: String) = networkLog(message)
                }
            // Bodies contain OTP codes and phone numbers, so this must stay off in release builds.
            level = LogLevel.ALL
            sanitizeHeader { header -> header.equals(HttpHeaders.Authorization, ignoreCase = true) }
        }
    }

    defaultRequest {
        url(config.baseUrl)
        contentType(ContentType.Application.Json)
        // The backend uses the platform to decide how the website receives its refresh token.
        header(APP_PLATFORM_HEADER, currentPlatform.headerValue)
        config.appBuild?.let { build ->
            header(APP_BUILD_HEADER, build.toString())
        }
    }

    config.configureClients(this)
}
