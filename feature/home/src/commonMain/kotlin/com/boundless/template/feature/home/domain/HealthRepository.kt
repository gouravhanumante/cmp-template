package com.boundless.template.feature.home.domain

import com.boundless.template.core.common.AppResult
import com.boundless.template.feature.home.domain.model.BackendStatus

interface HealthRepository {
    suspend fun status(): AppResult<BackendStatus>
}
