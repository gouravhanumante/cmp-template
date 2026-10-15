package com.boundless.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.boundless.template.app.App
import com.boundless.template.app.AppReadyState
import io.heimdall.ui.HeimdallOverlay
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val appReadyState: AppReadyState by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Kept on screen until startup resolves, so the in-app loading screen never flashes after it.
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !appReadyState.isReady.value }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            HeimdallOverlay {
                App()
            }
        }
    }
}
