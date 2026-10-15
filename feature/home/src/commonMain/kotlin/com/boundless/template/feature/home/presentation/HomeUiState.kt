package com.boundless.template.feature.home.presentation

import com.boundless.template.core.resources.UiText

data class HomeUiState(
    val isLoading: Boolean = true,
    val version: String? = null,
    val errorMessage: UiText? = null,
)

sealed interface HomeEvent {
    data object Retry : HomeEvent
}
