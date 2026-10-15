package com.boundless.template.core.network

import io.ktor.client.HttpClientConfig

/**
 * [baseUrl] must end with a slash so Ktor resolves relative paths correctly.
 * Defaults point at a locally running backend; see [defaultApiBaseUrl].
 */
data class ApiConfig(
    val baseUrl: String = defaultApiBaseUrl(),
    val enableLogging: Boolean = true,
    /** Android versionCode / iOS CFBundleVersion, sent so the server can refuse retired builds. Null = unknown. */
    val appBuild: Int? = null,
    val configureClients: HttpClientConfig<*>.() -> Unit = {},
)

/** Emulator/simulator: the local backend. Physical device: [HOSTED_API_BASE_URL] or the LAN backend. */
expect fun defaultApiBaseUrl(): String

// TODO(template): set these for your project.
/** Port and path of the backend when it runs on the dev machine. */
internal const val LOCAL_API_PORT_AND_PATH = ":8080/api/v1/"

/** The dev machine's LAN IP, for a physical device on the same Wi-Fi (`ipconfig getifaddr en0`). */
internal const val DEV_MACHINE_LAN_IP = "192.168.0.10"

/** Where a physical device points unless the flags below say otherwise. */
internal const val HOSTED_API_BASE_URL = "https://api.example.com/api/v1/"

/** Point a physical Android device at the dev machine instead of [HOSTED_API_BASE_URL]. */
internal const val USE_LOCAL_BACKEND_FOR_REAL_DEVICE_ANDROID = false

/** Point a physical iPhone at the dev machine instead of [HOSTED_API_BASE_URL]. */
internal const val USE_LOCAL_BACKEND_FOR_REAL_DEVICE_IOS = false
