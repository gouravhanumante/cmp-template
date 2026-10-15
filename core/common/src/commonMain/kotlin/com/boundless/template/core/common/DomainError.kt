package com.boundless.template.core.common

/** Every error the UI can be asked to render. Data-layer exceptions never cross this boundary. */
sealed interface DomainError {
    data object NoConnection : DomainError

    data object Timeout : DomainError

    /** Access token is missing/expired and refresh did not recover it. */
    data object Unauthorized : DomainError

    data object AccountSuspended : DomainError

    /** Authenticated but not allowed — wrong role, not the owner. Never a reason to sign out. */
    data object Forbidden : DomainError

    /** Blocked by the entitlement plan; the UI should show an upsell, not an error. */
    data class UpgradeRequired(
        val message: String,
    ) : DomainError

    data object NotFound : DomainError

    /** [retryAfterSeconds] is the server's own countdown; null when it did not say. */
    data class RateLimited(
        val retryAfterSeconds: Int? = null,
    ) : DomainError

    data class Validation(
        val message: String,
        val fieldErrors: Map<String, String> = emptyMap(),
    ) : DomainError

    /**
     * One structured item the backend attached to an error, such as a go-live blocker.
     * [message] is written by the server so wording never has to be duplicated here.
     */
    data class Detail(
        val code: String,
        val message: String,
    )

    /** Business rule rejected by the backend; [code] is the API error code. */
    data class Rejected(
        val code: String,
        val message: String,
        /** Structured extras the backend attached, e.g. `blockers` on go-live. */
        val details: Map<String, List<Detail>> = emptyMap(),
    ) : DomainError

    data object Unexpected : DomainError
}
