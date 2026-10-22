package com.boundless.template.core.common

/**
 * What kind of build is running, as each platform's entry point knows it: Android `BuildConfig.DEBUG`,
 * an iOS debug binary, desktop run from Gradle (not a packaged installer), web served from a dev host.
 * Release builds hide anything meant only for developers.
 */
data class AppBuildInfo(
    val isDebug: Boolean = false,
)
