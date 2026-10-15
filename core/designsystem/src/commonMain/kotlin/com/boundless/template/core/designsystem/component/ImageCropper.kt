package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min
import kotlin.math.roundToInt

/** Decodes an encoded (JPEG/PNG/etc.) image into a drawable bitmap, or null if the bytes are corrupt. */
expect fun ByteArray.decodeToImageBitmapOrNull(): ImageBitmap?

/**
 * Crops the [size] x [size] square starting at ([left], [top]) — pixel coordinates into the image
 * [source] decodes to — scales it to [outputSize] x [outputSize], and re-encodes as JPEG.
 */
expect fun cropSquareToJpeg(
    source: ByteArray,
    left: Int,
    top: Int,
    size: Int,
    outputSize: Int,
): ByteArray

private const val MAX_ZOOM = 4f
private const val CROP_OUTPUT_SIZE = 1024
private val CROP_VIEWPORT = 320.dp

/**
 * A picker wrapper that inserts a crop-to-square step between the OS picker returning an image and
 * [onImageSelected] firing: [rememberImagePicker] opens as normal, but its bytes go into a
 * full-screen [ImageCropScreen] overlay first, and only the user's confirmed square crop reaches
 * [onImageSelected]. Cancelling the crop discards the pick — [onImageSelected] never fires.
 *
 * Verification's camera/gallery call sites (ID photos, selfies) should keep using
 * [rememberImagePicker] directly instead — those need the original framing, not a forced 1:1 crop.
 */
@Composable
fun rememberCroppingImagePicker(
    source: ImageSource = ImageSource.GALLERY,
    onImageSelected: (ByteArray, String) -> Unit,
    onError: (String) -> Unit,
): () -> Unit {
    var pendingBytes by remember { mutableStateOf<ByteArray?>(null) }

    val pick = rememberImagePicker(
        source = source,
        onImageSelected = { bytes, _ -> pendingBytes = bytes },
        onError = onError,
    )

    pendingBytes?.let { bytes ->
        // A Dialog, not a plain conditional composable — this must render on top of whatever
        // screen requested the picker regardless of where in that screen's tree this call sits,
        // which sibling z-order alone can't guarantee.
        Dialog(
            onDismissRequest = { pendingBytes = null },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            ImageCropScreen(
                imageBytes = bytes,
                onDone = { cropped ->
                    pendingBytes = null
                    onImageSelected(cropped, "image/jpeg")
                },
                onCancel = { pendingBytes = null },
            )
        }
    }

    return pick
}

/**
 * Pan/zoom [imageBytes] within a square viewport — auto-framed to cover it, same starting point as
 * [androidx.compose.ui.layout.ContentScale.Crop] — then bakes the visible region into a square JPEG
 * on [onDone]. [onCancel] discards the pick entirely without calling [onDone].
 */
@Composable
fun ImageCropScreen(
    imageBytes: ByteArray,
    onDone: (ByteArray) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bitmap = remember(imageBytes) { imageBytes.decodeToImageBitmapOrNull() }
    if (bitmap == null) {
        LaunchedEffect(imageBytes) { onCancel() }
        return
    }

    val density = LocalDensity.current
    val viewportPx = with(density) { CROP_VIEWPORT.toPx() }
    val imgW = bitmap.width.toFloat()
    val imgH = bitmap.height.toFloat()
    // Scale at which the shorter image dimension exactly covers the square viewport — the same
    // framing ContentScale.Crop would pick, and this composable's own zoomed-out limit.
    val baseScale = viewportPx / min(imgW, imgH)

    var scale by remember(imageBytes) { mutableStateOf(1f) }
    var offset by remember(imageBytes) { mutableStateOf(Offset.Zero) }
    var finished by remember(imageBytes) { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onCancel) { Text(stringResource(Res.string.ds_cancel), color = Color.White) }
            Text(text = stringResource(Res.string.ds_crop_title), color = Color.White, style = MaterialTheme.typography.titleMedium)
            // Taps queued while the crop runs would otherwise upload the same photo again.
            TextButton(enabled = !finished, onClick = {
                if (finished) return@TextButton
                finished = true
                val totalScale = baseScale * scale
                val cropSidePx = viewportPx / totalScale
                val centerX = imgW / 2f - offset.x / totalScale
                val centerY = imgH / 2f - offset.y / totalScale
                val left = (centerX - cropSidePx / 2f).coerceIn(0f, (imgW - cropSidePx).coerceAtLeast(0f))
                val top = (centerY - cropSidePx / 2f).coerceIn(0f, (imgH - cropSidePx).coerceAtLeast(0f))
                onDone(
                    cropSquareToJpeg(
                        source = imageBytes,
                        left = left.roundToInt(),
                        top = top.roundToInt(),
                        size = cropSidePx.roundToInt().coerceAtLeast(1),
                        outputSize = CROP_OUTPUT_SIZE,
                    ),
                )
            }) { Text(stringResource(Res.string.ds_done), color = Color.White) }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .size(CROP_VIEWPORT)
                        .clipToBounds()
                        .border(2.dp, Color.White.copy(alpha = 0.9f))
                        .pointerInput(imageBytes) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(1f, MAX_ZOOM)
                                val maxOffsetX = (((imgW * baseScale * newScale) - viewportPx) / 2f).coerceAtLeast(0f)
                                val maxOffsetY = (((imgH * baseScale * newScale) - viewportPx) / 2f).coerceAtLeast(0f)
                                offset =
                                    Offset(
                                        (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                                        (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY),
                                    )
                                scale = newScale
                            }
                        },
                contentAlignment = Alignment.Center,
            ) {
                val displayWidthPx = imgW * baseScale
                val displayHeightPx = imgH * baseScale
                val displayWidthDp = with(density) { displayWidthPx.toDp() }
                val displayHeightDp = with(density) { displayHeightPx.toDp() }
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    // Laid out at its own natural "cover" size (>= the viewport in both
                    // dimensions) rather than fillMaxSize() — a viewport-sized Image never draws
                    // anything beyond its own bounds, so contentScale.Crop alone would bake in one
                    // fixed window with nothing extra to reveal when panned. Sizing the Image
                    // itself larger and letting the outer Box's clipToBounds() do the clipping
                    // keeps the rest of the bitmap available to pan into.
                    modifier =
                        Modifier
                            .requiredSize(displayWidthDp, displayHeightDp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            },
                )
            }
        }

        Text(
            text = stringResource(Res.string.ds_crop_hint),
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            textAlign = TextAlign.Center,
        )
    }
}
