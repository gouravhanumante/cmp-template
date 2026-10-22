package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTType
import platform.darwin.NSObject

private var activeDocumentDelegate: DocumentDelegate? = null

@Composable
actual fun rememberFilePicker(
    mimeTypes: List<String>,
    extensions: List<String>,
    onPicked: (name: String, bytes: ByteArray) -> Unit,
    onFailed: () -> Unit,
): () -> Unit =
    remember(mimeTypes, extensions, onPicked, onFailed) {
        {
            val presenter = UIApplication.sharedApplication.keyWindow?.rootViewController
            if (presenter == null) {
                onFailed()
            } else {
                val types = (extensions.mapNotNull { UTType.typeWithFilenameExtension(it) } + mimeTypes.mapNotNull { UTType.typeWithMIMEType(it) }).distinct()
                // asCopy: the app gets its own copy, so no security-scoped access is needed.
                val picker = UIDocumentPickerViewController(forOpeningContentTypes = types, asCopy = true)
                val delegate = DocumentDelegate(onPicked, onFailed)
                activeDocumentDelegate = delegate
                picker.delegate = delegate
                presenter.presentViewController(picker, animated = true, completion = null)
            }
        }
    }

private class DocumentDelegate(
    private val onPicked: (String, ByteArray) -> Unit,
    private val onFailed: () -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        val data = url?.let { NSData.dataWithContentsOfURL(it) }
        if (url == null || data == null) onFailed() else onPicked(url.lastPathComponent.orEmpty(), data.toByteArray())
        activeDocumentDelegate = null
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        activeDocumentDelegate = null
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = bytes?.reinterpret<ByteVar>()?.readBytes(length.toInt()) ?: ByteArray(0)
