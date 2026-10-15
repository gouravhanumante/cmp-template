package com.boundless.template.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// The browser has one thread; the fetch engine never blocks it.
internal actual fun platformIoDispatcher(): CoroutineDispatcher = Dispatchers.Default
