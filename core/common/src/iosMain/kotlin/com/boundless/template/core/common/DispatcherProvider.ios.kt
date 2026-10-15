package com.boundless.template.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Darwin's HTTP engine does its own IO off-thread, so Default is the right pool here.
internal actual fun platformIoDispatcher(): CoroutineDispatcher = Dispatchers.Default
