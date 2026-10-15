package com.boundless.template.core.network

import android.util.Log

private const val TAG = "AppNetwork"

actual fun networkLog(message: String) {
    // android.util.Log is unmocked on the JVM host-test classpath and throws there.
    runCatching { Log.d(TAG, message) }.onFailure { println("$TAG: $message") }
}
