package com.boundless.template.core.designsystem.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

actual fun ByteArray.decodeToImageBitmapOrNull(): ImageBitmap? =
    BitmapFactory.decodeByteArray(this, 0, size)?.asImageBitmap()

actual fun cropSquareToJpeg(
    source: ByteArray,
    left: Int,
    top: Int,
    size: Int,
    outputSize: Int,
): ByteArray {
    val original = BitmapFactory.decodeByteArray(source, 0, source.size)
        ?: error("We couldn't read that photo.")
    try {
        // Clamp defensively — the caller's crop math should already keep this in bounds, but a
        // Bitmap.createBitmap() out-of-range region throws rather than clamping itself.
        val safeLeft = left.coerceIn(0, (original.width - 1).coerceAtLeast(0))
        val safeTop = top.coerceIn(0, (original.height - 1).coerceAtLeast(0))
        val safeSize = size.coerceIn(1, minOf(original.width - safeLeft, original.height - safeTop))
        val cropped = Bitmap.createBitmap(original, safeLeft, safeTop, safeSize, safeSize)
        val scaled =
            if (safeSize == outputSize) {
                cropped
            } else {
                Bitmap.createScaledBitmap(cropped, outputSize, outputSize, true).also {
                    if (it !== cropped) cropped.recycle()
                }
            }
        try {
            val output = ByteArrayOutputStream()
            check(scaled.compress(Bitmap.CompressFormat.JPEG, 90, output)) {
                "We couldn't encode that photo."
            }
            return output.toByteArray()
        } finally {
            scaled.recycle()
        }
    } finally {
        original.recycle()
    }
}
