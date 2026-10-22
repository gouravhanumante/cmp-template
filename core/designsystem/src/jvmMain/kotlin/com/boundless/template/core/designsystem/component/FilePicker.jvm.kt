package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun rememberFilePicker(
    mimeTypes: List<String>,
    extensions: List<String>,
    onPicked: (name: String, bytes: ByteArray) -> Unit,
    onFailed: () -> Unit,
): () -> Unit {
    val scope = rememberCoroutineScope()
    return {
        scope.launch {
            val file = chooseFile(extensions) ?: return@launch
            val bytes = withContext(Dispatchers.IO) { runCatching { file.readBytes() }.getOrNull() }
            if (bytes == null) onFailed() else onPicked(file.name, bytes)
        }
    }
}

private fun chooseFile(extensions: List<String>): File? {
    val dialog = FileDialog(null as Frame?, "Choose a file", FileDialog.LOAD)
    dialog.setFilenameFilter { _, name -> name.lowercase().substringAfterLast('.') in extensions }
    dialog.isVisible = true
    val name = dialog.file ?: return null
    return File(dialog.directory, name)
}
