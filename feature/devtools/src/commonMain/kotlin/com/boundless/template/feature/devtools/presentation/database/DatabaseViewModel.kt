package com.boundless.template.feature.devtools.presentation.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boundless.template.core.common.AppResult
import com.boundless.template.core.common.DomainError
import com.boundless.template.core.designsystem.error.toUserMessage
import com.boundless.template.core.resources.UiText
import com.boundless.template.feature.devtools.domain.Note
import com.boundless.template.feature.devtools.domain.NoteStorage
import com.boundless.template.feature.devtools.domain.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DatabaseUiState(
    val notes: List<Note> = emptyList(),
    val draft: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val storage: NoteStorage = NoteStorage.ON_DEVICE,
    val errorMessage: UiText? = null,
) {
    val canSave: Boolean get() = draft.isNotBlank() && !isSaving
}

sealed interface DatabaseEvent {
    data class DraftChanged(val text: String) : DatabaseEvent

    data object Save : DatabaseEvent

    data class Delete(val id: Long) : DatabaseEvent

    data object DeleteAll : DatabaseEvent
}

class DatabaseViewModel(
    private val repository: NotesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DatabaseUiState(storage = repository.storage))
    val uiState: StateFlow<DatabaseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeNotes()
                .catch { _uiState.update { it.copy(isLoading = false, errorMessage = DomainError.Unexpected.toUserMessage()) } }
                .collect { notes -> _uiState.update { it.copy(notes = notes, isLoading = false) } }
        }
    }

    fun onEvent(event: DatabaseEvent) {
        when (event) {
            is DatabaseEvent.DraftChanged -> _uiState.update { it.copy(draft = event.text, errorMessage = null) }
            DatabaseEvent.Save -> save()
            is DatabaseEvent.Delete -> run { repository.delete(event.id) }
            DatabaseEvent.DeleteAll -> run { repository.deleteAll() }
        }
    }

    private fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repository.add(state.draft)) {
                is AppResult.Success -> _uiState.update { it.copy(draft = "", isSaving = false) }
                is AppResult.Failure -> _uiState.update { it.copy(isSaving = false, errorMessage = result.error.toUserMessage()) }
            }
        }
    }

    private fun run(write: suspend () -> AppResult<Unit>) {
        viewModelScope.launch {
            val result = write()
            if (result is AppResult.Failure) _uiState.update { it.copy(errorMessage = result.error.toUserMessage()) }
        }
    }
}
