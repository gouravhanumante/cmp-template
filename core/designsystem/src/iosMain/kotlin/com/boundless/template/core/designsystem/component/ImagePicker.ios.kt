package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraDevice
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

private var activePickerDelegate: PickerDelegate? = null

@Composable
actual fun rememberImagePicker(
    source: ImageSource,
    onImageSelected: (ByteArray, String) -> Unit,
    onError: (String) -> Unit,
): () -> Unit = remember(source, onImageSelected, onError) {
    {
        val presenter = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (presenter == null) {
            onError("We couldn't open the camera.")
            return@remember
        }

        val sourceType = when (source) {
            ImageSource.CAMERA, ImageSource.SELFIE_CAMERA -> {
                // No silent gallery fallback here — same failure shape as Android's
                // permission-denied path, since falling back would defeat requiring a live photo.
                if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
                    onError("Camera not available on this device.")
                    return@remember
                }
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            }
            ImageSource.GALLERY -> UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        }

        val picker = UIImagePickerController()
        picker.sourceType = sourceType
        // Verification's selfie capture only — the subject needs to see themselves while framing
        // the shot, so this opens the front camera by default. Falls back to the device's default
        // (rear) camera on devices with no front camera, since setting cameraDevice there is a
        // silent no-op rather than an error.
        if (source == ImageSource.SELFIE_CAMERA &&
            UIImagePickerController.isCameraDeviceAvailable(UIImagePickerControllerCameraDevice.UIImagePickerControllerCameraDeviceFront)
        ) {
            picker.cameraDevice = UIImagePickerControllerCameraDevice.UIImagePickerControllerCameraDeviceFront
        }
        val delegate = PickerDelegate(onImageSelected, onError)
        activePickerDelegate = delegate
        picker.delegate = delegate
        presenter.presentViewController(picker, animated = true, completion = null)
    }
}

private class PickerDelegate(
    private val onImageSelected: (ByteArray, String) -> Unit,
    private val onError: (String) -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val data: NSData? = image?.let { UIImageJPEGRepresentation(it, 0.9) }
        if (data == null) {
            onError("We couldn't read that photo. Please choose another image.")
        } else {
            onImageSelected(data.toByteArray(), "image/jpeg")
        }
        picker.dismissViewControllerAnimated(true, completion = null)
        activePickerDelegate = null
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        activePickerDelegate = null
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray =
    bytes?.reinterpret<ByteVar>()?.readBytes(length.toInt()) ?: ByteArray(0)
