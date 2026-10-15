package com.boundless.template.core.network

import com.boundless.template.core.common.AppResult
import com.boundless.template.core.common.Paged
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.serializer

/**
 * Runs a request and unwraps the API envelope. Every transport/parse failure is
 * converted to a [com.boundless.template.core.common.DomainError] — exceptions never escape the data layer.
 */
suspend inline fun <reified T> apiRequest(crossinline request: suspend () -> HttpResponse): AppResult<T> {
    var response: HttpResponse? = null
    return try {
        response = request()
        val envelope: ApiEnvelope<T> = response.body()
        val payload = envelope.data
        if (envelope.success && payload != null) {
            AppResult.Success(payload)
        } else {
            val error = envelope.error
            networkLog("API rejected ${response.callDescription()} -> ${error?.code}: ${error?.message}")
            AppResult.Failure(error.toDomainError())
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        networkLog("API failed ${response.callDescription()} -> ${throwable::class.simpleName}: ${throwable.message}")
        // A parse failure hides the payload that broke, which is the one thing needed to fix it.
        response?.let { runCatching { networkLog("Raw body: ${it.bodyAsText()}") } }
        AppResult.Failure(throwable.toDomainError())
    }
}

@PublishedApi
internal fun HttpResponse?.callDescription(): String =
    if (this == null) "(request not sent)" else "${request.method.value} ${request.url} [${status.value}]"

/**
 * For endpoints where a null `data` is a valid answer ("nothing submitted yet") rather than
 * a failure, which is what [apiRequest] would treat it as.
 */
suspend inline fun <reified T> apiRequestNullable(crossinline request: suspend () -> HttpResponse): AppResult<T?> {
    var response: HttpResponse? = null
    return try {
        response = request()
        val envelope: ApiEnvelope<T> = response.body()
        if (envelope.success) {
            AppResult.Success(envelope.data)
        } else {
            val error = envelope.error
            networkLog("API rejected ${response.callDescription()} -> ${error?.code}: ${error?.message}")
            AppResult.Failure(error.toDomainError())
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        networkLog("API failed ${response.callDescription()} -> ${throwable::class.simpleName}: ${throwable.message}")
        response?.let { runCatching { networkLog("Raw body: ${it.bodyAsText()}") } }
        AppResult.Failure(throwable.toDomainError())
    }
}

/**
 * Same as [apiRequest] for list endpoints, which return the items in `data` and the
 * page counters in `meta`. Each element of `data` is decoded independently, so one
 * malformed item (a schema mismatch on a single record) is skipped and logged instead
 * of failing the whole page for every caller.
 */
suspend inline fun <reified T> apiRequestPaged(crossinline request: suspend () -> HttpResponse): AppResult<Paged<T>> {
    var response: HttpResponse? = null
    return try {
        response = request()
        val envelope: ApiEnvelope<JsonElement> = response.body()
        val payload = envelope.data
        if (envelope.success && payload != null) {
            val items = payload.decodeElementsOrSkip<T>(response)
            val meta = envelope.meta
            AppResult.Success(
                Paged(
                    items = items,
                    page = meta?.page ?: 1,
                    limit = meta?.limit ?: items.size,
                    total = meta?.total ?: items.size,
                ),
            )
        } else {
            val error = envelope.error
            networkLog("API rejected ${response.callDescription()} -> ${error?.code}: ${error?.message}")
            AppResult.Failure(error.toDomainError())
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        networkLog("API failed ${response.callDescription()} -> ${throwable::class.simpleName}: ${throwable.message}")
        response?.let { runCatching { networkLog("Raw body: ${it.bodyAsText()}") } }
        AppResult.Failure(throwable.toDomainError())
    }
}

@PublishedApi
internal inline fun <reified T> JsonElement.decodeElementsOrSkip(response: HttpResponse?): List<T> {
    val array = this as? JsonArray ?: return emptyList()
    return array.mapNotNull { element ->
        runCatching { apiJson.decodeFromJsonElement(serializer<T>(), element) }
            .onFailure {
                networkLog(
                    "Skipped malformed item in ${response.callDescription()} -> " +
                        "${it::class.simpleName}: ${it.message} | item: $element",
                )
            }
            .getOrNull()
    }
}
