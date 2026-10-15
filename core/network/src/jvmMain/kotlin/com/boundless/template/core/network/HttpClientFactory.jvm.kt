package com.boundless.template.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

// An explicit -PapiBaseUrl= override wins; otherwise the backend running on this machine.
actual fun defaultApiBaseUrl(): String = API_BASE_URL_OVERRIDE.ifBlank { "http://localhost$LOCAL_API_PORT_AND_PATH" }

actual fun httpClientEngine(): HttpClientEngine = OkHttp.create()
