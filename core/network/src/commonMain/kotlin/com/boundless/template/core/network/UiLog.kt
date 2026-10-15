package com.boundless.template.core.network

/**
 * Debug-only breadcrumb of what the user just did, read alongside the network log so a request can
 * be tied back to the tap that caused it.
 *
 * Only an event's type name is ever written. The events themselves carry OTP codes, phone numbers
 * and message text, and a log is not the place for any of that — the type is what makes the trace
 * readable, the values only make it a liability. Navigation arguments are different: they are ids,
 * and they are the whole point of knowing what was handed to the next screen.
 *
 * [userId] is a mutable global, which is a smell worth naming: it is set in one place when the
 * session settles, and the alternative was threading a provider through twenty view model
 * constructors for something that exists purely to be read by a human during testing.
 */
object UiLog {
    /** Mirrors `ApiConfig.enableLogging`, so this is silent in a release build. */
    var enabled: Boolean = false

    /** Who the trace belongs to, so a log spanning two accounts can be told apart. */
    var userId: String? = null

    private val who: String get() = userId?.takeLast(6) ?: "signed-out"

    fun action(
        screen: String,
        event: Any,
    ) {
        if (!enabled) return
        networkLog("UI  [$who] $screen: ${event::class.simpleName}")
    }

    /** Where the user landed and what was carried there. */
    fun screen(
        route: String?,
        args: Map<String, String?> = emptyMap(),
    ) {
        if (!enabled) return
        val carried = args.entries.filter { it.value != null }.joinToString { "${it.key}=${it.value}" }
        networkLog("NAV [$who] ${route ?: "unknown"}${if (carried.isEmpty()) "" else "  <- $carried"}")
    }

    /**
     * The values a flow is actually carrying, for the places where "something happened" is not
     * enough and you need to see *what* — ids, dates, times, amounts, statuses.
     *
     * Use it where the values are business data. Auth deliberately does not: a code and a phone
     * number are the two things worth least in a log and cost most if one is shared.
     */
    fun detail(
        screen: String,
        what: String,
        vararg values: Pair<String, Any?>,
    ) {
        if (!enabled) return
        val shown = values.joinToString { "${it.first}=${it.second}" }
        networkLog("UI  [$who] $screen: $what  $shown")
    }
}
