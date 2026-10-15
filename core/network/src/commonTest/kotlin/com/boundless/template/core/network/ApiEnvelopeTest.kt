package com.boundless.template.core.network

import com.boundless.template.core.common.DomainError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

class ApiEnvelopeTest {
    @Test
    fun parsesSuccessEnvelope() {
        val raw = """{"success":true,"data":{"message":"OTP sent."},"error":null,"meta":null}"""

        val envelope = json.decodeFromString(ApiEnvelope.serializer(MessageResponse.serializer()), raw)

        assertTrue(envelope.success)
        assertEquals("OTP sent.", envelope.data?.message)
    }

    @Test
    fun parsesErrorEnvelopeWithNullDetails() {
        val raw =
            """{"success":false,"data":null,"error":{"code":"BAD_REQUEST","message":"Invalid code.","details":null}}"""

        val envelope = json.decodeFromString(ApiEnvelope.serializer(String.serializer()), raw)

        assertEquals("BAD_REQUEST", envelope.error?.code)
        assertEquals(DomainError.Rejected("BAD_REQUEST", "Invalid code."), envelope.error.toDomainError())
    }

    @Test
    fun mapsValidationDetailsToFieldErrors() {
        val raw =
            """
            {"success":false,"data":null,"error":{"code":"VALIDATION_ERROR","message":"Invalid request",
            "details":[{"path":"phone","message":"Invalid phone"}]}}
            """.trimIndent()

        val envelope = json.decodeFromString(ApiEnvelope.serializer(String.serializer()), raw)
        val error = envelope.error.toDomainError()

        assertEquals(
            DomainError.Validation("Invalid request", mapOf("phone" to "Invalid phone")),
            error,
        )
    }

    @Test
    fun mapsKnownCodesToTypedErrors() {
        assertEquals(DomainError.Unauthorized, ApiError(ErrorCodes.UNAUTHORIZED, "x").toDomainError())
        assertEquals(DomainError.AccountSuspended, ApiError(ErrorCodes.ACCOUNT_SUSPENDED, "x").toDomainError())
        assertEquals(DomainError.NotFound, ApiError(ErrorCodes.NOT_FOUND, "x").toDomainError())
        assertEquals(DomainError.RateLimited(), ApiError(ErrorCodes.TOO_MANY_REQUESTS, "x").toDomainError())
        assertEquals(DomainError.Unexpected, null.toDomainError())
    }

    @Test
    fun keepsPermissionDenialSeparateFromSuspension() {
        // A role denial must never be mistaken for a suspension, which force-signs the user out.
        assertEquals(DomainError.Forbidden, ApiError(ErrorCodes.FORBIDDEN, "Insufficient permissions").toDomainError())
    }

    @Test
    fun mapsUpgradeRequiredToItsOwnErrorCarryingTheMessage() {
        val error = ApiError(ErrorCodes.UPGRADE_REQUIRED, "Advanced discovery filters require an upgraded plan.")

        assertEquals(
            DomainError.UpgradeRequired("Advanced discovery filters require an upgraded plan."),
            error.toDomainError(),
        )
    }

    @Test
    fun readsPaginationFromMetaNotData() {
        val raw =
            """
            {"success":true,"data":[{"message":"a"},{"message":"b"}],"error":null,
            "meta":{"page":2,"limit":20,"total":45}}
            """.trimIndent()

        val envelope =
            json.decodeFromString(
                ApiEnvelope.serializer(kotlinx.serialization.builtins.ListSerializer(MessageResponse.serializer())),
                raw,
            )

        assertEquals(2, envelope.meta?.page)
        assertEquals(45, envelope.meta?.total)
        assertEquals(2, envelope.data?.size)
    }

    @Test
    fun extractsGoLiveBlockersFromObjectShapedDetails() {
        val raw =
            """
            {"success":false,"data":null,"error":{"code":"BAD_REQUEST",
            "message":"You cannot go live until your profile is complete.",
            "details":{"blockers":[{"code":"VERIFICATION_PENDING","message":"Verify your ID"},
            {"code":"PHOTO_REQUIRED","message":"Add a photo"}]}}}
            """.trimIndent()

        val envelope = json.decodeFromString(ApiEnvelope.serializer(String.serializer()), raw)
        val error = envelope.error.toDomainError()

        assertTrue(error is DomainError.Rejected)
        assertEquals(
            listOf(
                DomainError.Detail("VERIFICATION_PENDING", "Verify your ID"),
                DomainError.Detail("PHOTO_REQUIRED", "Add a photo"),
            ),
            error.details["blockers"],
        )
    }

    @Test
    fun readsRetryAfterFromRateLimitDetails() {
        val error = ApiError(ErrorCodes.TOO_MANY_REQUESTS, "Slow down", details = buildJsonObject { put("retryAfterSeconds", JsonPrimitive(17)) })

        assertEquals(DomainError.RateLimited(17), error.toDomainError())
    }
}
