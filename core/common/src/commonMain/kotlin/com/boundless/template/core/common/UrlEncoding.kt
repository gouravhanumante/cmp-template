package com.boundless.template.core.common

/** Percent-encodes a value for use in a URL query string (e.g. a Google Maps search query). */
fun encodeUrlQueryComponent(value: String): String =
    buildString {
        for (byte in value.encodeToByteArray()) {
            val code = byte.toInt() and 0xFF
            val isUnreserved =
                code in 'A'.code..'Z'.code ||
                    code in 'a'.code..'z'.code ||
                    code in '0'.code..'9'.code ||
                    code.toChar() in "-_.~"
            if (isUnreserved) {
                append(code.toChar())
            } else {
                append('%')
                append(code.toString(16).uppercase().padStart(2, '0'))
            }
        }
    }
