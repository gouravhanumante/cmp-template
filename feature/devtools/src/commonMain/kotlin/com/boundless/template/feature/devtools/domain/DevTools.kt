package com.boundless.template.feature.devtools.domain

import com.boundless.template.core.common.AppResult
import com.boundless.template.core.common.DomainError
import kotlinx.coroutines.flow.Flow

data class Note(
    val id: Long,
    val text: String,
    val createdAtMillis: Long,
)

enum class NoteStorage {
    ON_DEVICE,

    /** Web served without the COOP/COEP headers: notes are gone on reload. */
    MEMORY_ONLY,
}

interface NotesRepository {
    val storage: NoteStorage

    fun observeNotes(): Flow<List<Note>>

    suspend fun add(text: String): AppResult<Unit>

    suspend fun delete(id: Long): AppResult<Unit>

    suspend fun deleteAll(): AppResult<Unit>
}

/** One core check's result. [Passed.detail] is data worth showing, such as the backend's version. */
sealed interface CheckOutcome {
    data class Passed(val detail: String? = null) : CheckOutcome

    /** Wrote one value and read back another. */
    data object Mismatch : CheckOutcome

    data class Failed(val error: DomainError) : CheckOutcome
}

/** Round trips through each core building block, for the Core checks screen. */
interface CoreChecksRepository {
    /** The value the settings check saved last time, or null if it never ran on this device. */
    suspend fun lastSettingsValue(): String?

    suspend fun checkSettings(): CheckOutcome

    /** Saves and reads a throwaway sign-in, then puts back whatever was stored before. */
    suspend fun checkSecureStorage(): CheckOutcome

    suspend fun checkBackend(): CheckOutcome

    suspend fun checkDatabase(): CheckOutcome
}
