package com.boundless.template

import android.app.Application
import com.boundless.template.app.di.initKoin
import com.boundless.template.core.common.AppBuildInfo
import com.boundless.template.core.network.ApiConfig
import com.boundless.template.core.network.defaultApiBaseUrl
import io.heimdall.core.Heimdall
import io.heimdall.core.PlatformContext
import io.heimdall.network.ktor.HeimdallKtor
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Heimdall.install(PlatformContext(this))
        // Baked in per build type (see androidApp/build.gradle.kts); blank falls back to platform defaults.
        val baseUrl = BuildConfig.API_BASE_URL.ifBlank { defaultApiBaseUrl() }
        initKoin(
            apiConfig =
                ApiConfig(
                    baseUrl = baseUrl,
                    enableLogging = BuildConfig.DEBUG || BuildConfig.ENABLE_NETWORK_LOGGING,
                    appBuild = BuildConfig.VERSION_CODE,
                    configureClients = { install(HeimdallKtor) },
                ),
            buildInfo = AppBuildInfo(isDebug = BuildConfig.DEBUG),
        ) { androidContext(this@MainApplication) }
    }
}
