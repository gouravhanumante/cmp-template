package com.boundless.template.app

import com.boundless.template.app.di.initKoin
import com.boundless.template.core.common.AppBuildInfo
import com.boundless.template.core.network.ApiConfig
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import kotlin.experimental.ExperimentalNativeApi

private var koinStarted = false

/** Idempotent: anything Swift calls before the first Compose view can start Koin too. */
@OptIn(ExperimentalNativeApi::class)
fun ensureKoinStarted() {
    if (!koinStarted) {
        val networkLoggingEnabled =
            (NSBundle.mainBundle.objectForInfoDictionaryKey("AppNetworkLoggingEnabled") as? NSString)
                ?.toString() == "YES"
        val appBuild =
            (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? NSString)
                ?.toString()?.toIntOrNull()
        initKoin(
            apiConfig = ApiConfig(enableLogging = networkLoggingEnabled, appBuild = appBuild),
            // Xcode's Debug configuration links the debug framework; Release and releaseDebug link release.
            buildInfo = AppBuildInfo(isDebug = Platform.isDebugBinary),
        )
        koinStarted = true
    }
}
