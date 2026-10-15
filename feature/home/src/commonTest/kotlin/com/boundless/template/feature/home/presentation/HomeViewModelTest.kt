package com.boundless.template.feature.home.presentation

import com.boundless.template.core.common.AppResult
import com.boundless.template.core.common.DomainError
import com.boundless.template.feature.home.domain.HealthRepository
import com.boundless.template.feature.home.domain.model.BackendStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private class FakeHealthRepository(
    var next: AppResult<BackendStatus>,
) : HealthRepository {
    override suspend fun status(): AppResult<BackendStatus> = next
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    // Rule: an unreachable backend ends in an error the user can retry, never an endless spinner.
    @Test
    fun unreachableBackendShowsRetryableErrorInsteadOfLoading() =
        runTest(dispatcher) {
            val viewModel = HomeViewModel(FakeHealthRepository(AppResult.Failure(DomainError.NoConnection)))
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertNotNull(state.errorMessage)
            assertNull(state.version)
        }

    // Rule: once the backend is back, retrying replaces the old error with the content.
    @Test
    fun retryAfterFailureClearsTheErrorAndShowsTheVersion() =
        runTest(dispatcher) {
            val repository = FakeHealthRepository(AppResult.Failure(DomainError.Timeout))
            val viewModel = HomeViewModel(repository)
            advanceUntilIdle()

            repository.next = AppResult.Success(BackendStatus(version = "1.2.0"))
            viewModel.onEvent(HomeEvent.Retry)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNull(state.errorMessage)
            assertEquals("1.2.0", state.version)
        }
}
