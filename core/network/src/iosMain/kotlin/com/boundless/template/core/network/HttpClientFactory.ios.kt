package com.boundless.template.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSProcessInfo

// An explicit API_BASE_URL override wins; otherwise the simulator shares the host's loopback.
actual fun defaultApiBaseUrl(): String =
    API_BASE_URL_OVERRIDE.ifBlank {
        when {
            isRunningOnSimulator() -> "http://localhost$LOCAL_API_PORT_AND_PATH"
            USE_LOCAL_BACKEND_FOR_REAL_DEVICE_IOS -> "http://$DEV_MACHINE_LAN_IP$LOCAL_API_PORT_AND_PATH"
            else -> HOSTED_API_BASE_URL
        }
    }

private fun isRunningOnSimulator(): Boolean = NSProcessInfo.processInfo.environment["SIMULATOR_UDID"] != null

actual fun httpClientEngine(): HttpClientEngine = Darwin.create()
