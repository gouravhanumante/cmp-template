@file:OptIn(ExperimentalWasmJsInterop::class)

package com.boundless.template.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.boundless.template.app.App
import com.boundless.template.app.di.initKoin
import com.boundless.template.core.common.AppBuildInfo
import com.boundless.template.core.network.ApiConfig
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // A web build has no debug-only variant, and network logs can carry personal data.
    initKoin(
        apiConfig = ApiConfig(enableLogging = false),
        buildInfo = AppBuildInfo(isDebug = servedFromDevHost()),
    )
    ComposeViewport(viewportContainer = requireNotNull(document.getElementById("app"))) { App() }
}

// One compiled bundle serves both dev and production, so the host is the only signal: this machine,
// or a LAN address when the dev server is opened to a phone (APP_DEV_HOST=0.0.0.0).
private fun servedFromDevHost(): Boolean =
    js(
        """(() => {
            const host = window.location.hostname;
            return host === 'localhost' || host === '127.0.0.1' || host === '[::1]' ||
                /^(10|192\.168|172\.(1[6-9]|2\d|3[01]))\./.test(host);
        })()""",
    )
