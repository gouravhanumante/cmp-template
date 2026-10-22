package com.boundless.template.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.Density
import com.boundless.template.core.designsystem.component.AppFormSheet
import com.boundless.template.core.designsystem.component.AppTextField
import com.boundless.template.core.designsystem.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Priya types "Thor Kumar" into the New contact panel on her Mac.
@OptIn(ExperimentalComposeUiApi::class, InternalComposeUiApi::class)
class SidePanelTest {
    private var dismissed = false
    private var saved = false

    private fun scene(): ImageComposeScene =
        ImageComposeScene(1200, 800, Density(1f)) {
            AppTheme {
                var name by remember { mutableStateOf("") }
                val focus = remember { FocusRequester() }
                AppFormSheet(title = "New contact", onDismiss = { dismissed = true }, onConfirm = { saved = true }, confirmLabel = "Create") {
                    AppTextField(name, { name = it }, "Name", modifier = Modifier.focusRequester(focus))
                }
                LaunchedEffect(Unit) { focus.requestFocus() }
            }
        }

    private fun press(scene: ImageComposeScene, key: Key, char: Char) {
        for (type in listOf(KeyEventType.KeyDown, KeyEventType.KeyUp)) {
            scene.sendKeyEvent(KeyEvent(key, type, codePoint = char.code))
            scene.render()
        }
    }

    @Test
    fun typingASpaceInThePanelKeepsItOpen() {
        scene().use { scene ->
            repeat(3) { scene.render(it * 500_000_000L) }
            press(scene, Key.T, 'T')
            press(scene, Key.Spacebar, ' ')
            press(scene, Key.K, 'K')
            assertFalse(dismissed)
            assertFalse(saved)
        }
    }

    // She presses Enter after the name, as in any desktop form.
    @Test
    fun pressingEnterSaves() {
        scene().use { scene ->
            repeat(3) { scene.render(it * 500_000_000L) }
            press(scene, Key.T, 'T')
            press(scene, Key.Enter, '\n')
            assertTrue(saved)
        }
    }

    @Test
    fun clickingBesideThePanelClosesIt() {
        scene().use { scene ->
            repeat(3) { scene.render(it * 500_000_000L) }
            scene.sendPointerEvent(PointerEventType.Press, Offset(200f, 400f))
            scene.sendPointerEvent(PointerEventType.Release, Offset(200f, 400f))
            scene.render()
            assertTrue(dismissed)
        }
    }

    @Test
    fun clickingInsideThePanelKeepsItOpen() {
        scene().use { scene ->
            repeat(3) { scene.render(it * 500_000_000L) }
            scene.sendPointerEvent(PointerEventType.Press, Offset(1000f, 700f))
            scene.sendPointerEvent(PointerEventType.Release, Offset(1000f, 700f))
            scene.render()
            assertFalse(dismissed)
        }
    }
}

private inline fun <T> ImageComposeScene.use(block: (ImageComposeScene) -> T): T = try { block(this) } finally { close() }
