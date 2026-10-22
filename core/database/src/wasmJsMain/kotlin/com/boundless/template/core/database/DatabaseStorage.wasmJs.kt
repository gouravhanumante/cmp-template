@file:OptIn(ExperimentalWasmJsInterop::class)

package com.boundless.template.core.database

// Must match the worker's own test (core/database/worker/worker.js), which runs in the same origin.
actual fun databaseStorage(): DatabaseStorage =
    if (pageCanUseFileStorage()) DatabaseStorage.ON_DEVICE else DatabaseStorage.MEMORY_ONLY

private fun pageCanUseFileStorage(): Boolean =
    js("self.crossOriginIsolated === true && !!(navigator.storage && navigator.storage.getDirectory)")
