package com.boundless.template.core.common

enum class AppPlatform(val headerValue: String) {
    ANDROID("android"),
    IOS("ios"),
    DESKTOP("desktop"),
    WEB("web"),
}

/** Desktop and web have no app store, so anything keyed by platform must handle them explicitly. */
expect val currentPlatform: AppPlatform

// TODO(template): rename per app. Names the data folder on computers (lowercase on Linux).
const val APP_NAME = "BoundlessTemplate"
