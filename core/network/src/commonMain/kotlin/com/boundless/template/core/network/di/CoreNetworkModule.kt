package com.boundless.template.core.network.di

import com.boundless.template.core.network.ApiConfig
import com.boundless.template.core.network.ApiTokenRefresher
import com.boundless.template.core.network.AppGateEvents
import com.boundless.template.core.network.AuthTokenInvalidator
import com.boundless.template.core.network.SessionEvents
import com.boundless.template.core.network.TokenRefresher
import com.boundless.template.core.network.UiLog
import com.boundless.template.core.network.apiJson
import com.boundless.template.core.network.bearerTokenInvalidator
import com.boundless.template.core.network.createAuthenticatedClient
import com.boundless.template.core.network.createUnauthenticatedClient
import com.boundless.template.core.network.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/** Public endpoints (OTP, refresh) — deliberately has no Auth plugin. */
val UnauthenticatedClient = named("unauthenticatedClient")

/** Everything behind `Authorization: Bearer` with automatic refresh. */
val AuthenticatedClient = named("authenticatedClient")

fun coreNetworkModule(config: ApiConfig = ApiConfig()): Module =
    module {
        // One switch for both logs, so a release build is quiet without a second flag to forget.
        UiLog.enabled = config.enableLogging
        single { config }
        single { apiJson }
        single<HttpClientEngine> { httpClientEngine() }
        single { SessionEvents() }
        single { AppGateEvents() }

        single<HttpClient>(UnauthenticatedClient) {
            createUnauthenticatedClient(engine = get(), config = get(), appGateEvents = get(), json = get())
        }

        single<TokenRefresher> { ApiTokenRefresher(get(UnauthenticatedClient)) }

        single<HttpClient>(AuthenticatedClient) {
            createAuthenticatedClient(
                engine = get(),
                config = get(),
                tokenStorage = get(),
                tokenRefresher = get(),
                sessionEvents = get(),
                appGateEvents = get(),
                json = get(),
            )
        }

        single<AuthTokenInvalidator> { get<HttpClient>(AuthenticatedClient).bearerTokenInvalidator() }
    }
