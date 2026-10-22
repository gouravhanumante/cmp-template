package com.boundless.template.core.network

import com.boundless.template.core.common.currentPlatform
import com.boundless.template.core.datastore.AuthTokens
import com.boundless.template.core.datastore.TokenStorage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private fun engine(
    status: HttpStatusCode = HttpStatusCode.OK,
    gateHeader: String? = null,
    seen: MutableList<Headers> = mutableListOf(),
) = MockEngine { request ->
    seen += request.headers
    respond(
        content = """{"success":false,"data":null,"error":{"code":"X","message":"x"},"meta":null}""",
        status = status,
        headers =
            if (gateHeader == null) {
                headersOf("Content-Type", "application/json")
            } else {
                headersOf("Content-Type" to listOf("application/json"), APP_GATE_HEADER to listOf(gateHeader))
            },
    )
}

private fun config(appBuild: Int?) = ApiConfig(baseUrl = "https://api.test/api/v1/", enableLogging = false, appBuild = appBuild)

private object NoTokens : TokenStorage {
    override suspend fun read(): AuthTokens? = null

    override suspend fun save(tokens: AuthTokens) = Unit

    override suspend fun clear() = Unit
}

private val NoRefresh = TokenRefresher { RefreshResult.Refused }

class AppGateEventsTest {
    @Test
    fun `tells the server which build this is so a retired one can be refused`() =
        runTest {
            val seen = mutableListOf<Headers>()
            val client = createUnauthenticatedClient(engine(seen = seen), config(appBuild = 57), AppGateEvents())

            client.get("bookings")

            assertEquals("57", seen.single()[APP_BUILD_HEADER])
            assertEquals(currentPlatform.headerValue, seen.single()[APP_PLATFORM_HEADER])
        }

    @Test
    fun `still names the platform when the build number is unknown, so the website gets its cookie`() =
        runTest {
            val seen = mutableListOf<Headers>()
            val client = createUnauthenticatedClient(engine(seen = seen), config(appBuild = null), AppGateEvents())

            client.get("bookings")

            assertNull(seen.single()[APP_BUILD_HEADER])
            assertEquals(currentPlatform.headerValue, seen.single()[APP_PLATFORM_HEADER])
        }

    @Test
    fun `raises maintenance when the server refuses for maintenance`() = assertSignal("maintenance", HttpStatusCode.ServiceUnavailable, AppGateSignal.MAINTENANCE)

    @Test
    fun `raises update required when the server retires this build`() = assertSignal("update-required", HttpStatusCode.UpgradeRequired, AppGateSignal.UPDATE_REQUIRED)

    @Test
    fun `stays quiet for an ordinary failure`() = assertSignal(null, HttpStatusCode.ServiceUnavailable, null)

    @Test
    fun `stays quiet for a gate value it does not recognise`() = assertSignal("something-new", HttpStatusCode.ServiceUnavailable, null)

    @Test
    fun `the signed-in client raises the signal too`() =
        runTest {
            val events = AppGateEvents()
            val received = mutableListOf<AppGateSignal>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { events.events.collect { received += it } }
            val client =
                createAuthenticatedClient(
                    engine = engine(HttpStatusCode.ServiceUnavailable, "maintenance"),
                    config = config(appBuild = 57),
                    tokenStorage = NoTokens,
                    tokenRefresher = NoRefresh,
                    sessionEvents = SessionEvents(),
                    appGateEvents = events,
                )

            client.get("bookings")

            assertEquals(listOf(AppGateSignal.MAINTENANCE), received)
        }

    private fun assertSignal(
        header: String?,
        status: HttpStatusCode,
        expected: AppGateSignal?,
    ) = runTest {
        val events = AppGateEvents()
        val received = mutableListOf<AppGateSignal>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { events.events.collect { received += it } }
        val client = createUnauthenticatedClient(engine(status, header), config(appBuild = 57), events)

        client.get("bookings")

        assertEquals(listOfNotNull(expected), received)
    }
}
