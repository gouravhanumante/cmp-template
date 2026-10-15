package com.boundless.template.feature.home.data

import com.boundless.template.core.common.AppResult
import com.boundless.template.core.network.apiRequest
import com.boundless.template.feature.home.data.dto.HealthDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class HealthApi(
    private val client: HttpClient,
) {
    suspend fun health(): AppResult<HealthDto> = apiRequest { client.get("health") }
}
