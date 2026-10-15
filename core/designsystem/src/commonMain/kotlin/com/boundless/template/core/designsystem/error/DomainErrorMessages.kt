package com.boundless.template.core.designsystem.error

import com.boundless.template.core.common.DomainError
import com.boundless.template.core.resources.*

/**
 * The one place a [DomainError] becomes user-facing copy, so wording stays consistent
 * and raw backend messages are never dumped into the UI unchecked.
 */
fun DomainError.toUserMessage(): UiText =
    when (this) {
        DomainError.NoConnection -> Res.string.error_no_connection.asText()
        DomainError.Timeout -> Res.string.error_timeout.asText()
        DomainError.Unauthorized -> Res.string.error_unauthorized.asText()
        DomainError.AccountSuspended -> Res.string.error_account_suspended.asText()
        DomainError.Forbidden -> Res.string.error_forbidden.asText()
        DomainError.NotFound -> Res.string.error_not_found.asText()
        DomainError.Unexpected -> Res.string.error_unexpected.asText()
        is DomainError.RateLimited ->
            retryAfterSeconds
                ?.let { Res.string.error_rate_limited_seconds.asText(it) }
                ?: Res.string.error_rate_limited.asText()
        is DomainError.UpgradeRequired ->
            message.ifBlank { null }?.asRawText() ?: Res.string.error_upgrade_required.asText()
        is DomainError.Validation ->
            message.ifBlank { null }?.asRawText() ?: Res.string.error_validation.asText()
        is DomainError.Rejected ->
            message.ifBlank { null }?.asRawText() ?: Res.string.error_rejected.asText()
    }
