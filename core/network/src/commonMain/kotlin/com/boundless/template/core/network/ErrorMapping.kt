package com.boundless.template.core.network

import com.boundless.template.core.common.DomainError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.serialization.ContentConvertException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

fun ApiError?.toDomainError(): DomainError {
    if (this == null) return DomainError.Unexpected
    return when (code) {
        ErrorCodes.VALIDATION_ERROR ->
            DomainError.Validation(message = message, fieldErrors = details.toFieldErrors())

        ErrorCodes.UNAUTHORIZED -> DomainError.Unauthorized
        ErrorCodes.ACCOUNT_SUSPENDED -> DomainError.AccountSuspended
        ErrorCodes.UPGRADE_REQUIRED -> DomainError.UpgradeRequired(message)
        ErrorCodes.FORBIDDEN -> DomainError.Forbidden
        ErrorCodes.NOT_FOUND -> DomainError.NotFound
        ErrorCodes.TOO_MANY_REQUESTS -> DomainError.RateLimited(details.retryAfterSeconds())
        else -> DomainError.Rejected(code = code, message = message, details = details.toDetailMap())
    }
}

private fun JsonElement?.retryAfterSeconds(): Int? =
    ((this as? JsonObject)?.get("retryAfterSeconds") as? JsonPrimitive)?.contentOrNull?.toIntOrNull()

/** Zod issues arrive as `[{ "path": "...", "message": "..." }]`. */
private fun JsonElement?.toFieldErrors(): Map<String, String> {
    val issues = this as? JsonArray ?: return emptyMap()
    return issues
        .mapNotNull { issue ->
            val fields = (issue as? JsonObject) ?: return@mapNotNull null
            val path = fields["path"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val message = fields["message"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            path to message
        }.toMap()
}

/**
 * Business-rule extras arrive as an object of arrays, e.g. `{ "blockers": [{ code, message }] }`.
 * Bare strings are still accepted so older payload shapes keep working.
 */
private fun JsonElement?.toDetailMap(): Map<String, List<DomainError.Detail>> {
    val fields = (this as? JsonObject) ?: return emptyMap()
    return fields
        .mapNotNull { (key, value) ->
            val values =
                when (value) {
                    is JsonArray -> value.mapNotNull { it.toDetail() }
                    is JsonPrimitive -> listOfNotNull(value.toDetail())
                    else -> return@mapNotNull null
                }
            key to values
        }.toMap()
}

private fun JsonElement.toDetail(): DomainError.Detail? =
    when (this) {
        is JsonPrimitive -> contentOrNull?.let { DomainError.Detail(code = it, message = it) }
        is JsonObject -> {
            val code = (this["code"] as? JsonPrimitive)?.contentOrNull
            val message = (this["message"] as? JsonPrimitive)?.contentOrNull
            if (code == null && message == null) null else DomainError.Detail(code.orEmpty(), message ?: code.orEmpty())
        }
        else -> null
    }

fun Throwable.toDomainError(): DomainError =
    when (this) {
        is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> DomainError.Timeout
        is SerializationException, is ContentConvertException -> DomainError.Unexpected
        else -> DomainError.NoConnection
    }
