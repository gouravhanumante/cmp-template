package com.boundless.template.core.network

import com.boundless.template.core.datastore.AuthTokens
import com.boundless.template.core.datastore.TokenStorage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

private class StoredTokens(var tokens: AuthTokens?) : TokenStorage {
    override suspend fun read() = tokens

    override suspend fun save(tokens: AuthTokens) {
        this.tokens = tokens
    }

    override suspend fun clear() {
        tokens = null
    }
}

private val json = headersOf(HttpHeaders.ContentType, "application/json")

/** Priya's access token has expired; the server answers 401 until a renewed one is sent. */
class SignInRenewalTest {
    private val sent = mutableListOf<String?>()
    private val engine =
        MockEngine { request ->
            val token = request.headers[HttpHeaders.Authorization]
            sent += token
            val status = if (token == "Bearer renewed") HttpStatusCode.OK else HttpStatusCode.Unauthorized
            respond("""{"success":true,"data":{}}""", status, json)
        }

    private fun renewWith(result: RefreshResult): Pair<StoredTokens, List<SessionEndReason>> {
        val storage = StoredTokens(AuthTokens("expired", "refresh-1"))
        val ended = mutableListOf<SessionEndReason>()
        runTest {
            val events = SessionEvents()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { events.events.collect { ended += it } }
            val client =
                createAuthenticatedClient(
                    engine = engine,
                    config = ApiConfig(baseUrl = "https://api.test/api/v1/", enableLogging = false),
                    tokenStorage = storage,
                    tokenRefresher = { result },
                    sessionEvents = events,
                    appGateEvents = AppGateEvents(),
                )
            client.get("profile")
        }
        return storage to ended
    }

    @Test
    fun `a renewed sign-in is kept and the request goes again with it`() {
        val (storage, ended) = renewWith(RefreshResult.Renewed(AuthTokens("renewed", "refresh-2")))

        assertEquals(AuthTokens("renewed", "refresh-2"), storage.tokens)
        assertEquals(listOf<String?>("Bearer expired", "Bearer renewed"), sent.toList())
        assertEquals(emptyList<SessionEndReason>(), ended)
    }

    // A train through a tunnel must not sign Priya out.
    @Test
    fun `no answer while renewing keeps the sign-in`() {
        val (storage, ended) = renewWith(RefreshResult.Unreachable)

        assertEquals(AuthTokens("expired", "refresh-1"), storage.tokens)
        assertEquals(emptyList<SessionEndReason>(), ended)
    }

    @Test
    fun `a refused renewal signs out and says so`() {
        val (storage, ended) = renewWith(RefreshResult.Refused)

        assertNull(storage.tokens)
        assertEquals(listOf(SessionEndReason.SESSION_EXPIRED), ended)
    }
}

/** Only the server saying no counts as a refusal; anything else is "no answer". */
class ApiTokenRefresherTest {
    private fun refreshAgainst(engine: MockEngine): RefreshResult {
        lateinit var result: RefreshResult
        runTest {
            val client =
                createUnauthenticatedClient(
                    engine,
                    ApiConfig(baseUrl = "https://api.test/api/v1/", enableLogging = false),
                    AppGateEvents(),
                )
            result = ApiTokenRefresher(client).refresh("refresh-1")
        }
        return result
    }

    private fun answering(
        status: HttpStatusCode,
        body: String = """{"success":false,"error":{"code":"UNAUTHORIZED","message":"no"}}""",
    ) = MockEngine { respond(body, status, json) }

    @Test
    fun `new tokens from the server are a renewal`() {
        val result = refreshAgainst(answering(HttpStatusCode.OK, """{"success":true,"data":{"accessToken":"a2","refreshToken":"r2"}}"""))

        assertEquals(RefreshResult.Renewed(AuthTokens("a2", "r2")), result)
    }

    @Test
    fun `the server rejecting the refresh token is a refusal`() {
        assertEquals(RefreshResult.Refused, refreshAgainst(answering(HttpStatusCode.Unauthorized)))
    }

    @Test
    fun `being rate-limited is not a refusal`() {
        assertEquals(RefreshResult.Unreachable, refreshAgainst(answering(HttpStatusCode.TooManyRequests)))
    }

    @Test
    fun `a server error is not a refusal`() {
        assertEquals(RefreshResult.Unreachable, refreshAgainst(MockEngine { respondError(HttpStatusCode.ServiceUnavailable) }))
    }

    @Test
    fun `no connection is not a refusal`() {
        assertEquals(RefreshResult.Unreachable, refreshAgainst(MockEngine { throw IOException("offline") }))
    }

    // Hotel Wi-Fi answers every request with its own sign-in page.
    @Test
    fun `a captive portal page is not a refusal`() {
        val portal = MockEngine { respond("<html>Log in to Wi-Fi</html>", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "text/html")) }

        assertIs<RefreshResult.Unreachable>(refreshAgainst(portal))
    }
}
