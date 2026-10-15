package com.boundless.template.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.boundless.template.app.App
import com.boundless.template.app.di.initKoin
import com.boundless.template.core.network.ApiConfig

fun main() {
    // Network logs can carry personal data, and there is no separate release variant here yet.
    initKoin(apiConfig = ApiConfig(enableLogging = System.getProperty("app.networkLogging") == "true"))
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Template",
            state = rememberWindowState(width = 420.dp, height = 820.dp),
        ) {
            App()
        }
    }
}
