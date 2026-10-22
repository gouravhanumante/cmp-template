package com.boundless.template.core.database

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

// Priya jots notes in the app; the list shows the newest first and keeps them after she quits.
class NoteDaoTest {
    private fun open(file: File) = Room.databaseBuilder<AppDatabase>(name = file.absolutePath).setDriver(BundledSQLiteDriver()).build()

    private val file = File(Files.createTempDirectory("notes").toFile(), "app.db")

    @Test
    fun theNewestNoteIsListedFirst() =
        runTest {
            val db = open(file)
            db.noteDao().insert(NoteEntity(text = "Buy milk", createdAtMillis = 1_000))
            db.noteDao().insert(NoteEntity(text = "Call Arjun", createdAtMillis = 2_000))

            assertEquals(listOf("Call Arjun", "Buy milk"), db.noteDao().observeAll().first().map { it.text })
            db.close()
        }

    @Test
    fun deletingOneNoteKeepsTheOthers() =
        runTest {
            val db = open(file)
            val milk = db.noteDao().insert(NoteEntity(text = "Buy milk", createdAtMillis = 1_000))
            db.noteDao().insert(NoteEntity(text = "Call Arjun", createdAtMillis = 2_000))

            db.noteDao().delete(milk)

            assertEquals(listOf("Call Arjun"), db.noteDao().observeAll().first().map { it.text })
            db.close()
        }

    @Test
    fun notesAreStillThereAfterTheAppIsReopened() =
        runTest {
            open(file).apply {
                noteDao().insert(NoteEntity(text = "Buy milk", createdAtMillis = 1_000))
                close()
            }

            val reopened = open(file)
            assertEquals(listOf("Buy milk"), reopened.noteDao().observeAll().first().map { it.text })
            reopened.close()
        }
}
