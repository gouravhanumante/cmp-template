package com.boundless.template.core.designsystem.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private const val MAX_DECODE_DIMENSION = 2048

@Composable
actual fun rememberImagePicker(
    source: ImageSource,
    onImageSelected: (ByteArray, String) -> Unit,
    onError: (String) -> Unit,
): () -> Unit {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        readImage(context, uri.toString(), onImageSelected, onError)
    }

    // TakePicturePreview (returns a Bitmap directly) instead of TakePicture, so this needs no
    // FileProvider/content:// URI — the image is downscaled server-side regardless.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap -> readBitmap(bitmap, onImageSelected, onError) }

    // Selfie capture (verification) only — TakePicturePreview has no way to request a facing, so
    // this wraps the same ACTION_IMAGE_CAPTURE intent with the front-camera extras instead. These
    // extras are an unofficial/legacy convention, honored by AOSP camera and most OEM cameras but
    // not guaranteed on every device; where a camera app ignores them it just opens on the rear
    // camera as before, so this is a best-effort default rather than a hard guarantee.
    val selfieCameraLauncher = rememberLauncherForActivityResult(
        contract = TakeFrontFacingCameraPicturePreview(),
    ) { bitmap -> readBitmap(bitmap, onImageSelected, onError) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            when (source) {
                ImageSource.SELFIE_CAMERA -> selfieCameraLauncher.launch(null)
                else -> cameraLauncher.launch(null)
            }
        } else {
            onError("Camera permission is needed to take this photo.")
        }
    }

    return remember(source) {
        {
            when (source) {
                ImageSource.GALLERY ->
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                ImageSource.CAMERA, ImageSource.SELFIE_CAMERA -> {
                    val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
                    when {
                        granted && source == ImageSource.SELFIE_CAMERA -> selfieCameraLauncher.launch(null)
                        granted -> cameraLauncher.launch(null)
                        else -> permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }
        }
    }
}

/**
 * Same contract as [ActivityResultContracts.TakePicturePreview], except the launched intent asks
 * for the front camera. Verification's selfie call site only — [ImagePicker] keeps
 * [ActivityResultContracts.TakePicturePreview] itself for the plain [ImageSource.CAMERA] path.
 */
private class TakeFrontFacingCameraPicturePreview : ActivityResultContract<Void?, Bitmap?>() {
    override fun createIntent(context: Context, input: Void?): Intent =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // No single extra is honored everywhere, so all three known conventions are set —
            // each is a no-op on devices/camera apps that don't recognize it.
            .putExtra("android.intent.extras.CAMERA_FACING", 1)
            .putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            .putExtra("android.intent.extra.USE_FRONT_CAMERA", true)

    override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
        if (resultCode != Activity.RESULT_OK) return null
        @Suppress("DEPRECATION")
        return intent?.getParcelableExtra("data")
    }
}

private fun readBitmap(
    bitmap: Bitmap?,
    onImageSelected: (ByteArray, String) -> Unit,
    onError: (String) -> Unit,
) {
    if (bitmap == null) {
        onError("We couldn't take that photo. Please try again.")
        return
    }
    runCatching {
        val output = ByteArrayOutputStream()
        try {
            check(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)) {
                "We couldn't encode that photo."
            }
        } finally {
            bitmap.recycle()
        }
        onImageSelected(output.toByteArray(), "image/jpeg")
    }.onFailure { onError("We couldn't take that photo. Please try again.") }
}

private fun readImage(
    context: Context,
    uriString: String,
    onImageSelected: (ByteArray, String) -> Unit,
    onError: (String) -> Unit,
) {
    runCatching {
        val uri = android.net.Uri.parse(uriString)
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("We couldn't read that photo.")
        // Shared iPhone photos can remain HEIF or arrive with a generic MIME type.
        // Decode every selection and upload one format the backend and image clients support.
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        check(bounds.outWidth > 0 && bounds.outHeight > 0) { "We couldn't decode that photo." }
        val sampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight)
        val bitmap = BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            BitmapFactory.Options().apply { inSampleSize = sampleSize },
        )
            ?: error("We couldn't decode that photo.")
        val output = ByteArrayOutputStream()
        try {
            check(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)) {
                "We couldn't encode that photo."
            }
        } finally {
            bitmap.recycle()
        }
        onImageSelected(output.toByteArray(), "image/jpeg")
    }.onFailure { onError("We couldn't read that photo. Please choose another image.") }
}

private fun calculateSampleSize(width: Int, height: Int): Int {
    var sampleSize = 1
    while (maxOf(width, height) / (sampleSize * 2) >= MAX_DECODE_DIMENSION) {
        sampleSize *= 2
    }
    return sampleSize
}
