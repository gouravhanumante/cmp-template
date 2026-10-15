package com.boundless.template.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.boundless.template.app.App
import com.boundless.template.app.di.initKoin
import com.boundless.template.core.network.ApiConfig
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // A web build has no debug-only variant, and network logs can carry personal data.
    initKoin(apiConfig = ApiConfig(enableLogging = false))
    ComposeViewport(viewportContainer = requireNotNull(document.getElementById("app"))) { App() }
}
