package com.boundless.template.core.common

import kotlin.random.Random

/**
 * Client-generated key so a retried payment request cannot charge twice.
 * The backend requires 8–100 characters.
 */
object IdempotencyKeys {
    fun random(): String =
        buildString {
            repeat(4) { append(Random.nextInt(0, Int.MAX_VALUE).toString(HEX_RADIX)) }
        }.take(64)

    private const val HEX_RADIX = 16
}
