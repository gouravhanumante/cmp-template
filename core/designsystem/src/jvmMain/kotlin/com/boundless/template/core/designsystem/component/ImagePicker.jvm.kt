package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt

private const val MAX_PICKED_DIMENSION = 2048

// Desktop has no camera flow; every source opens the system file chooser.
@Composable
actual fun rememberImagePicker(
    source: ImageSource,
    onImageSelected: (ByteArray, String) -> Unit,
    onError: (String) -> Unit,
): () -> Unit {
    val scope = rememberCoroutineScope()
    return {
        scope.launch {
            val file = chooseImageFile() ?: return@launch
            val jpeg = withContext(Dispatchers.IO) { runCatching { file.readBytes().toScaledJpeg() }.getOrNull() }
            if (jpeg == null) {
                onError("We couldn't read that photo. Please choose another image.")
            } else {
                onImageSelected(jpeg, "image/jpeg")
            }
        }
    }
}

private fun chooseImageFile(): File? {
    val dialog = FileDialog(null as Frame?, "Choose a photo", FileDialog.LOAD)
    dialog.setFilenameFilter { _, name -> name.lowercase().substringAfterLast('.') in setOf("jpg", "jpeg", "png", "webp") }
    dialog.isVisible = true
    val name = dialog.file ?: return null
    return File(dialog.directory, name)
}

private fun ByteArray.toScaledJpeg(): ByteArray {
    val original = Image.makeFromEncoded(this)
    val scale = minOf(1f, MAX_PICKED_DIMENSION.toFloat() / max(original.width, original.height))
    val width = (original.width * scale).roundToInt()
    val height = (original.height * scale).roundToInt()
    val surface = Surface.makeRasterN32Premul(width, height)
    surface.canvas.drawImageRect(original, Rect.makeWH(width.toFloat(), height.toFloat()))
    return requireNotNull(surface.makeImageSnapshot().encodeToData(EncodedImageFormat.JPEG, 90)).bytes
}
