package com.boundless.template.core.resources

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Text that a ViewModel can produce but only a composable can render.
 *
 * [stringResource] is `@Composable`, so state holders cannot resolve copy themselves. They carry a
 * UiText instead and the screen resolves it, which keeps error/snackbar wording translatable.
 */
sealed interface UiText {
    data class Resource(
        val id: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText

    /** Already-final text from the backend. Passed through untranslated on purpose. */
    data class Raw(val value: String) : UiText
}

fun StringResource.asText(vararg args: Any): UiText = UiText.Resource(this, args.toList())

fun String.asRawText(): UiText = UiText.Raw(this)

@Composable
fun UiText.resolve(): String =
    when (this) {
        is UiText.Resource -> if (args.isEmpty()) stringResource(id) else stringResource(id, *args.toTypedArray())
        is UiText.Raw -> value
    }

@Composable
fun UiText?.resolveOrNull(): String? = this?.resolve()
