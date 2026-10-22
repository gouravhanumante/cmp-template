package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable

/**
 * Opens the platform's file chooser for one file of the given [mimeTypes] (Android, iOS
 * document types) or [extensions] (desktop, iOS). [onPicked] gets the file's name and bytes;
 * [onFailed] means the file couldn't be read, and the caller says so in its own words.
 * Cancelling calls neither. Returns the launcher.
 */
@Composable
expect fun rememberFilePicker(
    mimeTypes: List<String>,
    extensions: List<String>,
    onPicked: (name: String, bytes: ByteArray) -> Unit,
    onFailed: () -> Unit,
): () -> Unit
