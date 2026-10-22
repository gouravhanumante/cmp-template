package com.boundless.template.feature.devtools.presentation.checks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boundless.template.core.common.AppBuildInfo
import com.boundless.template.core.common.AppPlatform
import com.boundless.template.core.common.currentPlatform
import com.boundless.template.core.designsystem.error.toUserMessage
import com.boundless.template.core.network.ApiConfig
import com.boundless.template.core.network.AppGateEvents
import com.boundless.template.core.network.AppGateSignal
import com.boundless.template.core.network.SessionEndReason
import com.boundless.template.core.network.SessionEvents
import com.boundless.template.core.resources.Res
import com.boundless.template.core.resources.UiText
import com.boundless.template.core.resources.asText
import com.boundless.template.core.resources.devtools_check_mismatch
import com.boundless.template.core.resources.devtools_pick_failed
import com.boundless.template.feature.devtools.domain.CheckOutcome
import com.boundless.template.feature.devtools.domain.CoreChecksRepository
import com.boundless.template.feature.devtools.domain.NoteStorage
import com.boundless.template.feature.devtools.domain.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CheckId { SETTINGS, SECURE_STORAGE, DATABASE, BACKEND }

sealed interface CheckStatus {
    data object NotRun : CheckStatus

    data object Running : CheckStatus

    data class Passed(val detail: String? = null) : CheckStatus

    data class Failed(val message: UiText) : CheckStatus
}

data class PickedFile(
    val name: String,
    val sizeBytes: Int,
)

/** Kept out of [ChecksUiState.equals]: a photo's bytes are compared by identity, which is what changed. */
class PickedImage(
    val bytes: ByteArray,
)

data class ChecksUiState(
    val checks: Map<CheckId, CheckStatus> = CheckId.entries.associateWith { CheckStatus.NotRun },
    val lastSettingsValue: String? = null,
    val platform: AppPlatform = currentPlatform,
    val isDebugBuild: Boolean = false,
    val appBuild: Int? = null,
    val apiBaseUrl: String = "",
    val storage: NoteStorage = NoteStorage.ON_DEVICE,
    val pickedFile: PickedFile? = null,
    val pickedImage: PickedImage? = null,
    val pickError: UiText? = null,
)

sealed interface ChecksEvent {
    data object RunAll : ChecksEvent

    data class Run(val id: CheckId) : ChecksEvent

    data class FilePicked(val name: String, val bytes: ByteArray) : ChecksEvent

    data class ImagePicked(val bytes: ByteArray) : ChecksEvent

    data object PickFailed : ChecksEvent

    data object SimulateMaintenance : ChecksEvent

    data object SimulateSessionEnded : ChecksEvent
}

class ChecksViewModel(
    private val repository: CoreChecksRepository,
    private val appGateEvents: AppGateEvents,
    private val sessionEvents: SessionEvents,
    buildInfo: AppBuildInfo,
    apiConfig: ApiConfig,
    notes: NotesRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            ChecksUiState(
                isDebugBuild = buildInfo.isDebug,
                appBuild = apiConfig.appBuild,
                apiBaseUrl = apiConfig.baseUrl,
                storage = notes.storage,
            ),
        )
    val uiState: StateFlow<ChecksUiState> = _uiState.asStateFlow()

    init {
        // Read before any run overwrites it, so a restart shows what the previous session saved.
        viewModelScope.launch {
            val last = repository.lastSettingsValue()
            _uiState.update { it.copy(lastSettingsValue = last) }
        }
    }

    fun onEvent(event: ChecksEvent) {
        when (event) {
            ChecksEvent.RunAll -> CheckId.entries.forEach(::run)
            is ChecksEvent.Run -> run(event.id)
            is ChecksEvent.FilePicked -> _uiState.update { it.copy(pickedFile = PickedFile(event.name, event.bytes.size), pickError = null) }
            is ChecksEvent.ImagePicked -> _uiState.update { it.copy(pickedImage = PickedImage(event.bytes), pickError = null) }
            ChecksEvent.PickFailed -> _uiState.update { it.copy(pickError = Res.string.devtools_pick_failed.asText()) }
            ChecksEvent.SimulateMaintenance -> appGateEvents.notify(AppGateSignal.MAINTENANCE)
            ChecksEvent.SimulateSessionEnded -> sessionEvents.notifySessionEnded(SessionEndReason.SESSION_EXPIRED)
        }
    }

    private fun run(id: CheckId) {
        setStatus(id, CheckStatus.Running)
        viewModelScope.launch {
            val outcome =
                when (id) {
                    CheckId.SETTINGS -> repository.checkSettings()
                    CheckId.SECURE_STORAGE -> repository.checkSecureStorage()
                    CheckId.DATABASE -> repository.checkDatabase()
                    CheckId.BACKEND -> repository.checkBackend()
                }
            setStatus(id, outcome.toStatus())
        }
    }

    private fun setStatus(
        id: CheckId,
        status: CheckStatus,
    ) = _uiState.update { it.copy(checks = it.checks + (id to status)) }

    private fun CheckOutcome.toStatus(): CheckStatus =
        when (this) {
            is CheckOutcome.Passed -> CheckStatus.Passed(detail)
            CheckOutcome.Mismatch -> CheckStatus.Failed(Res.string.devtools_check_mismatch.asText())
            is CheckOutcome.Failed -> CheckStatus.Failed(error.toUserMessage())
        }
}
