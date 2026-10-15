package com.boundless.template.core.common

/** A page of results plus enough information to know whether another page exists. */
data class Paged<T>(
    val items: List<T>,
    val page: Int,
    val limit: Int,
    val total: Int,
) {
    val hasMore: Boolean get() = page * limit < total

    fun <R> map(transform: (T) -> R): Paged<R> =
        Paged(items = items.map(transform), page = page, limit = limit, total = total)
}
