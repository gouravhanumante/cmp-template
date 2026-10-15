package com.boundless.template.core.network

import com.boundless.template.core.common.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable

@Serializable
private data class TestItemDto(val id: String, val name: String)

private fun clientReturning(body: String): HttpClient {
    val engine =
        MockEngine { _ ->
            respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
    return HttpClient(engine) { install(ContentNegotiation) { json(apiJson) } }
}

class ApiRequestPagedTest {
    @Test
    fun skipsMalformedItemsButKeepsWellFormedOnes() =
        runTest {
            // Middle item is missing the required "name" field — the exact shape of the
            // production bug (one bad record in the list) this test guards against.
            val body =
                """
                {"success":true,"data":[
                    {"id":"1","name":"Alice"},
                    {"id":"2"},
                    {"id":"3","name":"Charlie"}
                ],"error":null,"meta":{"page":1,"limit":3,"total":3}}
                """.trimIndent()
            val client = clientReturning(body)

            val result = apiRequestPaged<TestItemDto> { client.get("items") }

            check(result is AppResult.Success)
            assertEquals(listOf("1", "3"), result.data.items.map { it.id })
            assertEquals(3, result.data.total)
        }

    @Test
    fun succeedsNormallyWhenEveryItemIsWellFormed() =
        runTest {
            val body =
                """
                {"success":true,"data":[{"id":"1","name":"Alice"},{"id":"2","name":"Bob"}],
                "error":null,"meta":{"page":1,"limit":2,"total":2}}
                """.trimIndent()
            val client = clientReturning(body)

            val result = apiRequestPaged<TestItemDto> { client.get("items") }

            check(result is AppResult.Success)
            assertEquals(listOf("1", "2"), result.data.items.map { it.id })
        }
}
