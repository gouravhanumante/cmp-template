package com.boundless.template.feature.devtools.data

import com.boundless.template.core.common.AppResult
import com.boundless.template.core.common.DispatcherProvider
import com.boundless.template.core.common.DomainError
import com.boundless.template.core.common.WallClock
import com.boundless.template.core.database.DatabaseStorage
import com.boundless.template.core.database.NoteDao
import com.boundless.template.core.database.NoteEntity
import com.boundless.template.feature.devtools.domain.Note
import com.boundless.template.feature.devtools.domain.NoteStorage
import com.boundless.template.feature.devtools.domain.NotesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class RoomNotesRepository(
    private val dao: NoteDao,
    private val clock: WallClock,
    private val dispatchers: DispatcherProvider,
    databaseStorage: DatabaseStorage,
) : NotesRepository {
    override val storage: NoteStorage =
        when (databaseStorage) {
            DatabaseStorage.ON_DEVICE -> NoteStorage.ON_DEVICE
            DatabaseStorage.MEMORY_ONLY -> NoteStorage.MEMORY_ONLY
        }

    override fun observeNotes(): Flow<List<Note>> =
        dao.observeAll().map { rows -> rows.map { Note(it.id, it.text, it.createdAtMillis) } }

    override suspend fun add(text: String): AppResult<Unit> =
        write { dao.insert(NoteEntity(text = text.trim(), createdAtMillis = clock.nowMillis())) }

    override suspend fun delete(id: Long): AppResult<Unit> = write { dao.delete(id) }

    override suspend fun deleteAll(): AppResult<Unit> = write { dao.deleteAll() }

    private suspend fun write(block: suspend () -> Unit): AppResult<Unit> =
        try {
            withContext(dispatchers.io) { block() }
            AppResult.Success(Unit)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Throwable) {
            AppResult.Failure(DomainError.Unexpected)
        }
}
