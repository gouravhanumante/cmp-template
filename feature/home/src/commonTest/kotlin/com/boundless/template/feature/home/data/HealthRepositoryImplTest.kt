package com.boundless.template.feature.home.data

import com.boundless.template.core.common.AppResult
import com.boundless.template.core.common.DomainError
import com.boundless.template.core.network.apiJson
import com.boundless.template.feature.home.domain.model.BackendStatus
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HealthRepositoryImplTest {
    private fun repositoryAnswering(status: HttpStatusCode, body: String): HealthRepositoryImpl {
        val engine =
            MockEngine {
                respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
            }
        val client =
            HttpClient(engine) {
                expectSuccess = false
                install(ContentNegotiation) { json(apiJson) }
            }
        return HealthRepositoryImpl(HealthApi(client))
    }

    @Test
    fun successEnvelopeBecomesTheDomainVersion() =
        runTest {
            val repository = repositoryAnswering(HttpStatusCode.OK, """{"success":true,"data":{"status":"UP","version":"1.2.0"}}""")

            val result = repository.status()

            assertEquals(AppResult.Success(BackendStatus(version = "1.2.0")), result)
        }

    // Rule: a backend error arrives as a DomainError, never as an exception crossing into the UI.
    @Test
    fun errorEnvelopeBecomesADomainErrorNotAnException() =
        runTest {
            val repository =
                repositoryAnswering(
                    HttpStatusCode.NotFound,
                    """{"success":false,"error":{"code":"NOT_FOUND","message":"No such route"}}""",
                )

            val result = repository.status()

            assertEquals(DomainError.NotFound, assertIs<AppResult.Failure>(result).error)
        }
}
