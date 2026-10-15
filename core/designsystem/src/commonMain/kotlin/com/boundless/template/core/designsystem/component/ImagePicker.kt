package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable

/** Where a picked image should come from. */
enum class ImageSource {
    /** Live capture only — no gallery fallback, so a caller can trust the photo is current. */
    CAMERA,

    /**
     * Live capture like [CAMERA], but opens the front-facing camera by default — for identity
     * selfies (e.g. verification), where the subject needs to see themselves while framing the
     * shot. Other camera call sites (profile/package photos) should keep using [CAMERA].
     */
    SELFIE_CAMERA,
    GALLERY,
}

/**
 * Opens the platform image picker. On success, `onImageSelected` receives a re-encoded JPEG and
 * its content type; the caller is responsible for uploading it. Returns a launcher function.
 */
@Composable
expect fun rememberImagePicker(
    source: ImageSource = ImageSource.GALLERY,
    onImageSelected: (ByteArray, String) -> Unit,
    onError: (String) -> Unit,
): () -> Unit
