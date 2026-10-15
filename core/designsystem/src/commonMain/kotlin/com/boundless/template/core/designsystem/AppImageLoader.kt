package com.boundless.template.core.designsystem

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger

/**
 * Coil3's default singleton [ImageLoader] has no network fetcher registered — `AsyncImage` with a
 * remote URL silently renders nothing, no crash, no log (confirmed against coil3's own source:
 * `DefaultSingletonImageLoaderFactory` builds a bare `ImageLoader` with no components). The
 * `coil-network-ktor3` dependency only provides the fetcher factory; something has to actually
 * install it. Call this once at app start, before any `AsyncImage` composes.
 *
 * The memory and disk caches are coil3's own defaults (25% of the app heap, 2% of the cache dir)
 * — deliberately not overridden, since nothing here knows better than those heuristics do.
 */
fun installAppImageLoader(enableLogging: Boolean = false) {
    SingletonImageLoader.setSafe { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            // Without this, a photo that finishes decoding mid-navigation-slide (common on a
            // freshly-pushed detail screen — see NavTransitions.android.kt) just snaps into place,
            // which reads as a stutter even though the slide animation itself never dropped a frame.
            .crossfade(true)
            // Debug builds only. DebugLogger writes a line per request, and photo URLs are signed
            // storage links — neither the log spam during a scroll nor the URLs belong in release.
            .apply { if (enableLogging) logger(DebugLogger()) }
            .build()
    }
}
