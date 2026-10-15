package com.boundless.template.core.designsystem.component

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface

actual fun ByteArray.decodeToImageBitmapOrNull(): ImageBitmap? =
    runCatching { Image.makeFromEncoded(this).toComposeImageBitmap() }.getOrNull()

actual fun cropSquareToJpeg(
    source: ByteArray,
    left: Int,
    top: Int,
    size: Int,
    outputSize: Int,
): ByteArray {
    val original = Image.makeFromEncoded(source)
    // Clamp defensively — the caller's crop math should already keep this in bounds.
    val safeLeft = left.coerceIn(0, (original.width - 1).coerceAtLeast(0))
    val safeTop = top.coerceIn(0, (original.height - 1).coerceAtLeast(0))
    val safeSize = size.coerceIn(1, minOf(original.width - safeLeft, original.height - safeTop))

    val surface = Surface.makeRasterN32Premul(outputSize, outputSize)
    surface.canvas.drawImageRect(
        image = original,
        src = Rect.makeXYWH(safeLeft.toFloat(), safeTop.toFloat(), safeSize.toFloat(), safeSize.toFloat()),
        dst = Rect.makeWH(outputSize.toFloat(), outputSize.toFloat()),
    )
    val data = surface.makeImageSnapshot().encodeToData(EncodedImageFormat.JPEG, 90)
        ?: error("We couldn't encode that photo.")
    return data.bytes
}
