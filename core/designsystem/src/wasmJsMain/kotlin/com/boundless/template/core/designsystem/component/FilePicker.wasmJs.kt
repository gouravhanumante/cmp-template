@file:OptIn(ExperimentalWasmJsInterop::class)

package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberFilePicker(
    mimeTypes: List<String>,
    extensions: List<String>,
    onPicked: (name: String, bytes: ByteArray) -> Unit,
    onFailed: () -> Unit,
): () -> Unit =
    remember(mimeTypes, extensions, onPicked, onFailed) {
        {
            openFileChooser(
                accept = (mimeTypes + extensions.map { ".$it" }).joinToString(","),
                onPicked = { name, bytes -> onPicked(name, bytes.copyToByteArray()) },
                onFailed = onFailed,
            )
        }
    }

private fun JsAny.copyToByteArray(): ByteArray = ByteArray(lengthOf(this)) { valueAt(this, it).toByte() }

private fun lengthOf(array: JsAny): Int = js("array.length")

private fun valueAt(
    array: JsAny,
    index: Int,
): Int = js("array[index]")

// iOS Safari drops `change` on a detached input, so it stays in the document until the pick ends.
private fun openFileChooser(
    accept: String,
    onPicked: (String, JsAny) -> Unit,
    onFailed: () -> Unit,
): Unit =
    js(
        """(() => {
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = accept;
            input.style.display = 'none';
            document.body.appendChild(input);
            input.addEventListener('cancel', () => input.remove());
            input.addEventListener('change', () => {
                const file = input.files && input.files[0];
                input.remove();
                if (!file) return;
                file.arrayBuffer().then(
                    (buffer) => onPicked(file.name, new Uint8Array(buffer)),
                    () => onFailed());
            });
            input.click();
        })()""",
    )
