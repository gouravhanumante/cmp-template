@file:OptIn(ExperimentalWasmJsInterop::class)

package com.boundless.template.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

// The page's own origin: the host forwards /api to the backend, so the browser never makes a cross-origin call.
actual fun defaultApiBaseUrl(): String = API_BASE_URL_OVERRIDE.ifBlank { "${pageOrigin()}/api/v1/" }

actual fun httpClientEngine(): HttpClientEngine = Js.create()

private fun pageOrigin(): String = js("window.location.origin")
