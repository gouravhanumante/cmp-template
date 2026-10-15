package com.boundless.template.feature.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthDto(
    val status: String = "",
    val version: String = "",
)
