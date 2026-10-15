package com.boundless.template.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

@OptIn(ExperimentalComposeUiApi::class)
fun MainViewController(): UIViewController {
    ensureKoinStarted()
    return ComposeUIViewController(
        configure = { parallelRendering = false },
    ) { App() }
}
