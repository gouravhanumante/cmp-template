package com.boundless.template.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.boundless.template.app.App
import com.boundless.template.app.desktop.SingleInstance
import com.boundless.template.app.di.initKoin
import com.boundless.template.core.common.AppBuildInfo
import com.boundless.template.core.common.desktopAppDataDir
import com.boundless.template.core.network.ApiConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import java.awt.Dimension
import java.awt.Taskbar
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
    val activations = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    // Already open: the running copy comes to the front instead.
    val instance = SingleInstance.claim(desktopAppDataDir()) { activations.tryEmit(Unit) } ?: exitProcess(0)
    runCatching {
        if (Taskbar.isTaskbarSupported()) {
            val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("icon.png")
            if (stream != null) Taskbar.getTaskbar().iconImage = ImageIO.read(stream)
        }
    }
    // Network logs can carry personal data, and there is no separate release variant here yet.
    initKoin(
        apiConfig = ApiConfig(enableLogging = System.getProperty("app.networkLogging") == "true"),
        // An installed app is started by jpackage's launcher, which sets this; `./gradlew run` doesn't.
        buildInfo = AppBuildInfo(isDebug = System.getProperty("jpackage.app-version") == null),
    )
    application {
        // Opens maximized; the size is what un-maximizing returns to.
        val state = rememberWindowState(placement = WindowPlacement.Maximized, width = 1280.dp, height = 800.dp)
        Window(
            onCloseRequest = {
                instance.close()
                exitApplication()
            },
            icon = painterResource("icon.png"),
            title = "Template",
            state = state,
        ) {
            LaunchedEffect(Unit) {
                window.minimumSize = Dimension(360, 600)
                activations.collect {
                    state.isMinimized = false
                    window.toFront()
                    window.requestFocus()
                }
            }
            App()
        }
    }
}
