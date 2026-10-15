package com.boundless.template.core.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** Mirrors the backend envelope: `{ success, data, error, meta }`. */
@Serializable
data class ApiEnvelope<T>(
    val success: Boolean = false,
    val data: T? = null,
    val error: ApiError? = null,
    val meta: ApiMeta? = null,
)

/** Page-based pagination; list endpoints put it in `meta`, never in `data`. */
@Serializable
data class ApiMeta(
    val page: Int = 1,
    val limit: Int = 0,
    val total: Int = 0,
)

@Serializable
data class ApiError(
    val code: String = ErrorCodes.UNEXPECTED,
    val message: String = "",
    // Shape varies by endpoint: a zod issue array for VALIDATION_ERROR, an object like
    // `{"blockers":[...]}` for business rules. Kept raw and interpreted during mapping.
    val details: JsonElement? = null,
)

/** Endpoints that only acknowledge, e.g. `POST /auth/otp/request`. */
@Serializable
data class MessageResponse(
    val message: String = "",
)

object ErrorCodes {
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val BAD_REQUEST = "BAD_REQUEST"
    const val UNAUTHORIZED = "UNAUTHORIZED"
    const val FORBIDDEN = "FORBIDDEN"
    const val ACCOUNT_SUSPENDED = "ACCOUNT_SUSPENDED"
    const val UPGRADE_REQUIRED = "UPGRADE_REQUIRED"
    const val NOT_FOUND = "NOT_FOUND"
    const val TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS"
    const val UNEXPECTED = "UNEXPECTED"
}
