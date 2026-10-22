package com.boundless.template.feature.devtools.data

import com.boundless.template.core.common.AppResult
import com.boundless.template.core.common.DispatcherProvider
import com.boundless.template.core.common.DomainError
import com.boundless.template.core.common.WallClock
import com.boundless.template.core.database.NoteDao
import com.boundless.template.core.database.NoteEntity
import com.boundless.template.core.datastore.AuthTokens
import com.boundless.template.core.datastore.KeyValueStore
import com.boundless.template.core.datastore.TokenStorage
import com.boundless.template.core.network.apiRequest
import com.boundless.template.feature.devtools.domain.CheckOutcome
import com.boundless.template.feature.devtools.domain.CoreChecksRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

internal const val SETTINGS_CHECK_KEY = "devtools.settingsCheck"
private const val PROBE_ACCESS_TOKEN = "devtools-check-access"
private const val PROBE_REFRESH_TOKEN = "devtools-check-refresh"

@Serializable
internal data class HealthDto(
    val status: String = "",
    val version: String = "",
)

internal class DevHealthApi(
    private val client: HttpClient,
) {
    suspend fun health(): AppResult<HealthDto> = apiRequest { client.get("health") }
}

internal class CoreChecksRepositoryImpl(
    private val settings: KeyValueStore,
    private val tokens: TokenStorage,
    private val healthApi: DevHealthApi,
    private val noteDao: NoteDao,
    private val clock: WallClock,
    private val dispatchers: DispatcherProvider,
) : CoreChecksRepository {
    override suspend fun lastSettingsValue(): String? = safely { settings.read(SETTINGS_CHECK_KEY) }.getOrNullValue()

    override suspend fun checkSettings(): CheckOutcome =
        check {
            val value = clock.nowMillis().toString()
            settings.write(SETTINGS_CHECK_KEY, value)
            if (settings.read(SETTINGS_CHECK_KEY) == value) CheckOutcome.Passed(value) else CheckOutcome.Mismatch
        }

    override suspend fun checkSecureStorage(): CheckOutcome =
        check {
            val before = tokens.read()
            try {
                tokens.save(AuthTokens(PROBE_ACCESS_TOKEN, PROBE_REFRESH_TOKEN))
                // The web keeps the refresh token in an HttpOnly cookie, so only the access token reads back everywhere.
                if (tokens.read()?.accessToken == PROBE_ACCESS_TOKEN) CheckOutcome.Passed() else CheckOutcome.Mismatch
            } finally {
                if (before == null) tokens.clear() else tokens.save(before)
            }
        }

    override suspend fun checkBackend(): CheckOutcome =
        when (val result = healthApi.health()) {
            is AppResult.Success -> CheckOutcome.Passed(result.data.version.ifBlank { null })
            is AppResult.Failure -> CheckOutcome.Failed(result.error)
        }

    override suspend fun checkDatabase(): CheckOutcome =
        check {
            val text = "devtools check ${clock.nowMillis()}"
            val id = noteDao.insert(NoteEntity(text = text, createdAtMillis = clock.nowMillis()))
            val found = noteDao.observeAll().first().any { it.id == id && it.text == text }
            noteDao.delete(id)
            if (found) CheckOutcome.Passed() else CheckOutcome.Mismatch
        }

    private suspend fun check(block: suspend () -> CheckOutcome): CheckOutcome =
        when (val result = safely(block)) {
            is AppResult.Success -> result.data
            is AppResult.Failure -> CheckOutcome.Failed(result.error)
        }

    private suspend fun <T> safely(block: suspend () -> T): AppResult<T> =
        try {
            AppResult.Success(withContext(dispatchers.io) { block() })
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Throwable) {
            AppResult.Failure(DomainError.Unexpected)
        }

    private fun <T> AppResult<T?>.getOrNullValue(): T? = (this as? AppResult.Success)?.data
}
