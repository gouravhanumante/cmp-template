package com.boundless.template.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AppOtpField(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier,
    length: Int,
    isError: Boolean,
    supportingText: String?,
    enabled: Boolean,
) = ComposeOtpField(code, onCodeChange, modifier, length, isError, supportingText, enabled)
