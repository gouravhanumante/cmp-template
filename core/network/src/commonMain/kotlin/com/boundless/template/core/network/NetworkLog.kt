package com.boundless.template.core.network

/** Routes Ktor + data-layer diagnostics to the platform log under a single filterable tag. */
expect fun networkLog(message: String)
