@file:OptIn(ExperimentalWasmJsInterop::class)

package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePicker(
    source: ImageSource,
    onImageSelected: (ByteArray, String) -> Unit,
    onError: (String) -> Unit,
): () -> Unit =
    remember(source, onImageSelected, onError) {
        {
            val capture =
                when (source) {
                    ImageSource.CAMERA -> "environment"
                    ImageSource.SELFIE_CAMERA -> "user"
                    ImageSource.GALLERY -> null
                }
            // Desktop browsers ignore `capture` and open a file dialog, which would defeat requiring a live photo.
            if (capture != null && !hasTouchCamera()) {
                onError("Camera not available on this device.")
            } else {
                openFilePicker(
                    capture = capture,
                    maxDimension = MAX_PICKED_DIMENSION,
                    onPicked = { bytes, contentType -> onImageSelected(bytes.toByteArray(), contentType) },
                    onFailed = { onError("We couldn't read that photo. Please choose another image.") },
                )
            }
        }
    }

// Above both server re-encode limits (photos 1080, documents 1600), so nothing kept is lost.
private const val MAX_PICKED_DIMENSION = 2048

private fun JsAny.toByteArray(): ByteArray = ByteArray(byteLength(this)) { byteAt(this, it).toByte() }

private fun byteLength(array: JsAny): Int = js("array.length")

private fun byteAt(
    array: JsAny,
    index: Int,
): Int = js("array[index]")

private fun hasTouchCamera(): Boolean = js("window.matchMedia('(pointer: coarse)').matches")

// Listing JPEG explicitly makes iPhone Safari convert HEIC photos before handing them over.
// iOS Safari drops `change` on a detached input, so it stays in the document until the pick ends.
// The browser shrinks the photo natively; a 12 MP original is far too slow to copy and decode in Wasm.
private fun openFilePicker(
    capture: String?,
    maxDimension: Int,
    onPicked: (JsAny, String) -> Unit,
    onFailed: () -> Unit,
): Unit =
    js(
        """(() => {
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = 'image/jpeg,image/png,image/webp';
            if (capture) input.setAttribute('capture', capture);
            input.style.display = 'none';
            document.body.appendChild(input);
            input.addEventListener('cancel', () => input.remove());
            input.addEventListener('change', () => {
                const file = input.files && input.files[0];
                input.remove();
                if (!file) return;
                createImageBitmap(file).then((bitmap) => {
                    const scale = Math.min(1, maxDimension / Math.max(bitmap.width, bitmap.height));
                    const canvas = document.createElement('canvas');
                    canvas.width = Math.max(1, Math.round(bitmap.width * scale));
                    canvas.height = Math.max(1, Math.round(bitmap.height * scale));
                    canvas.getContext('2d').drawImage(bitmap, 0, 0, canvas.width, canvas.height);
                    bitmap.close();
                    canvas.toBlob((blob) => {
                        if (!blob) { onFailed(); return; }
                        blob.arrayBuffer().then(
                            (buffer) => onPicked(new Uint8Array(buffer), 'image/jpeg'),
                            () => onFailed());
                    }, 'image/jpeg', 0.9);
                }, () => onFailed());
            });
            input.click();
        })()""",
    )
