package com.boundless.template.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boundless.template.core.common.AppResult
import com.boundless.template.core.designsystem.error.toUserMessage
import com.boundless.template.feature.home.domain.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HealthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Retry -> load()
        }
    }

    private fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repository.status()) {
                is AppResult.Success ->
                    _uiState.update { HomeUiState(isLoading = false, version = result.data.version) }
                is AppResult.Failure ->
                    _uiState.update { HomeUiState(isLoading = false, errorMessage = result.error.toUserMessage()) }
            }
        }
    }
}
