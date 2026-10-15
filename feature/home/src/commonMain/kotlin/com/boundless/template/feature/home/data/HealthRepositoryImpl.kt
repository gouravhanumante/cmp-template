package com.boundless.template.feature.home.data

import com.boundless.template.core.common.AppResult
import com.boundless.template.core.common.map
import com.boundless.template.feature.home.data.dto.HealthDto
import com.boundless.template.feature.home.domain.HealthRepository
import com.boundless.template.feature.home.domain.model.BackendStatus

class HealthRepositoryImpl(
    private val api: HealthApi,
) : HealthRepository {
    override suspend fun status(): AppResult<BackendStatus> = api.health().map { it.toDomain() }
}

internal fun HealthDto.toDomain(): BackendStatus = BackendStatus(version = version)
