package com.boundless.template.core.network

import android.os.Build
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

// An explicit -PapiBaseUrl= override wins; otherwise the emulator reaches the host through 10.0.2.2.
actual fun defaultApiBaseUrl(): String =
    API_BASE_URL_OVERRIDE.ifBlank {
        when {
            isRunningOnEmulator() -> "http://10.0.2.2$LOCAL_API_PORT_AND_PATH"
            USE_LOCAL_BACKEND_FOR_REAL_DEVICE_ANDROID -> "http://$DEV_MACHINE_LAN_IP$LOCAL_API_PORT_AND_PATH"
            else -> HOSTED_API_BASE_URL
        }
    }

// No single reliable API; newer Play emulator images mimic real fingerprints, so HARDWARE/MODEL matter most.
private fun isRunningOnEmulator(): Boolean =
    Build.FINGERPRINT.startsWith("generic") ||
        Build.FINGERPRINT.startsWith("unknown") ||
        Build.MODEL.contains("google_sdk") ||
        Build.MODEL.contains("Emulator") ||
        Build.MODEL.contains("Android SDK built for x86") ||
        Build.MODEL.startsWith("sdk_gphone") ||
        Build.MANUFACTURER.contains("Genymotion") ||
        Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
        Build.PRODUCT == "google_sdk" ||
        Build.PRODUCT.startsWith("sdk_gphone") ||
        Build.HARDWARE.contains("goldfish") ||
        Build.HARDWARE.contains("ranchu")

actual fun httpClientEngine(): HttpClientEngine = OkHttp.create()
