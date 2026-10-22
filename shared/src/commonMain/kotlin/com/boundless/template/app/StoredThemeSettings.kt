package com.boundless.template.app

import com.boundless.template.core.datastore.KeyValueStore
import com.boundless.template.core.designsystem.theme.AppPalette
import com.boundless.template.core.designsystem.theme.ThemeSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal const val THEME_KEY = "app.theme"

/** Saved by name, so reordering [AppPalette] never changes anyone's theme; an unknown name falls back to the default. */
class StoredThemeSettings(
    private val store: KeyValueStore,
    private val scope: CoroutineScope,
) : ThemeSettings {
    private val _palette = MutableStateFlow(AppPalette.Default)
    override val palette: StateFlow<AppPalette> = _palette.asStateFlow()

    init {
        scope.launch {
            val saved = runCatching { store.read(THEME_KEY) }.getOrNull()
            AppPalette.entries.firstOrNull { it.name == saved }?.let { _palette.value = it }
        }
    }

    override fun select(palette: AppPalette) {
        _palette.value = palette
        scope.launch { runCatching { store.write(THEME_KEY, palette.name) } }
    }
}
