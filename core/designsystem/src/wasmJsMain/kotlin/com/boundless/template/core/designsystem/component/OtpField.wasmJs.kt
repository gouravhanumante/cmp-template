package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.HtmlElementView
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.resources.*
import kotlinx.browser.document
import org.jetbrains.compose.resources.stringResource
import org.w3c.dom.HTMLInputElement

// A real DOM input takes the tap itself, so the browser opens the keyboard; Compose's backing input misses it on Safari.
@Composable
actual fun AppOtpField(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier,
    length: Int,
    isError: Boolean,
    supportingText: String?,
    enabled: Boolean,
) {
    val currentOnCodeChange by rememberUpdatedState(onCodeChange)
    val currentLength by rememberUpdatedState(length)
    val label = stringResource(Res.string.ds_otp_code)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box {
            OtpDigitRow(code = code, length = length, isError = isError, enabled = enabled)
            HtmlElementView(
                factory = {
                    val input = document.createElement("input") as HTMLInputElement
                    input.type = "text"
                    input.setAttribute("inputmode", "numeric")
                    input.setAttribute("pattern", "[0-9]*")
                    input.setAttribute("autocomplete", "one-time-code")
                    input.setAttribute("enterkeyhint", "done")
                    // 16px stops iOS zooming the page on focus; the digits are drawn by OtpDigitRow underneath.
                    input.setAttribute(
                        "style",
                        "width:100%;height:100%;margin:0;padding:0;border:0;outline:none;font-size:16px;" +
                            "background:transparent;color:transparent;-webkit-text-fill-color:transparent;caret-color:transparent;",
                    )
                    input.addEventListener("input", {
                        val digits = input.value.filter { it.isDigit() }.take(currentLength)
                        if (input.value != digits) input.value = digits
                        currentOnCodeChange(digits)
                    })
                    input
                },
                modifier = Modifier.matchParentSize(),
                update = { input ->
                    if (input.value != code) input.value = code
                    input.disabled = !enabled
                    // Web resources load asynchronously, so the label can be blank on the first update.
                    input.setAttribute("aria-label", label)
                },
            )
        }
        OtpSupportingText(supportingText)
    }
}
