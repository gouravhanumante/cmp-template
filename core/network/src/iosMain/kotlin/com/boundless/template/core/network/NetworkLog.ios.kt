package com.boundless.template.core.network

import platform.Foundation.NSLog
import platform.Foundation.NSString

actual fun networkLog(message: String) {
    // NSLog is variadic; Kotlin String isn't auto-bridged to NSString in vararg position, so an
    // explicit cast is required or "%@" dereferences the raw Kotlin object as a pointer and crashes.
    NSLog("AppNetwork: %@", message as NSString)
}
