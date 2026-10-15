package com.boundless.template.app

import com.boundless.template.app.di.initKoin
import com.boundless.template.core.network.ApiConfig
import platform.Foundation.NSBundle
import platform.Foundation.NSString

private var koinStarted = false

/** Idempotent: anything Swift calls before the first Compose view can start Koin too. */
fun ensureKoinStarted() {
    if (!koinStarted) {
        val networkLoggingEnabled =
            (NSBundle.mainBundle.objectForInfoDictionaryKey("AppNetworkLoggingEnabled") as? NSString)
                ?.toString() == "YES"
        val appBuild =
            (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? NSString)
                ?.toString()?.toIntOrNull()
        initKoin(apiConfig = ApiConfig(enableLogging = networkLoggingEnabled, appBuild = appBuild))
        koinStarted = true
    }
}
