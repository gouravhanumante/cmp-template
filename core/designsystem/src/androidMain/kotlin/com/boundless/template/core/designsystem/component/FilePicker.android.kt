package com.boundless.template.core.designsystem.component

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberFilePicker(
    mimeTypes: List<String>,
    extensions: List<String>,
    onPicked: (name: String, bytes: ByteArray) -> Unit,
    onFailed: () -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            scope.launch {
                val file = withContext(Dispatchers.IO) { runCatching { readFile(context, uri) }.getOrNull() }
                if (file == null) onFailed() else onPicked(file.first, file.second)
            }
        }
    return { launcher.launch(mimeTypes.toTypedArray()) }
}

private fun readFile(
    context: Context,
    uri: Uri,
): Pair<String, ByteArray> {
    val name =
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        } ?: uri.lastPathSegment.orEmpty()
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: error("No stream")
    return name to bytes
}
