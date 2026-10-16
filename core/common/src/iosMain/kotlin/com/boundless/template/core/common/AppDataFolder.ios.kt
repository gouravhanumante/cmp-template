package com.boundless.template.core.common

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * The app's Application Support folder: backed up, not shown in the Files app. Documents is only
 * for files the user manages (Apple, "Using the file system effectively").
 */
@OptIn(ExperimentalForeignApi::class)
fun applicationSupportPath(): String {
    val url =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )
    return requireNotNull(url?.path) { "Application Support directory unavailable" }
}
