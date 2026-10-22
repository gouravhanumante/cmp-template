package com.boundless.template.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import com.boundless.template.core.designsystem.theme.AppTheme
import com.boundless.template.core.resources.asRawText
import com.boundless.template.feature.home.presentation.HomeContent
import com.boundless.template.feature.home.presentation.HomeUiState
import org.jetbrains.skia.EncodedImageFormat
import org.junit.Assume.assumeTrue
import java.io.File
import kotlin.test.Test

/**
 * Not a test: renders screens to PNGs for a human to look at (build/render), at phone and desktop
 * width. Run with: ./gradlew :desktopApp:test -Drender=true
 */
class RenderScreens {
    private fun render(
        name: String,
        widthDp: Int,
        heightDp: Int,
        content: @Composable () -> Unit,
    ) {
        val scene =
            ImageComposeScene(widthDp * 2, heightDp * 2, Density(2f)) {
                AppTheme { Surface(color = MaterialTheme.colorScheme.background) { content() } }
            }
        // A second frame later, so entering panels and dialogs have finished animating.
        scene.render(0)
        val png = scene.render(2_000_000_000L).encodeToData(EncodedImageFormat.PNG)!!.bytes
        scene.close()
        File("build/render/$name.png").apply { parentFile.mkdirs() }.writeBytes(png)
    }

    @Test
    fun render() {
        assumeTrue(System.getProperty("render") == "true")
        val loaded = HomeUiState(isLoading = false, version = "1.4.0")
        render("home-phone", 360, 800) { HomeContent(loaded, {}) }
        render("home-desktop", 1280, 800) { HomeContent(loaded, {}) }
        render("home-loading-phone", 360, 800) { HomeContent(HomeUiState(), {}) }
        render("home-error-phone", 360, 800) { HomeContent(HomeUiState(isLoading = false, errorMessage = "Can't reach the server.".asRawText()), {}) }
    }
}
