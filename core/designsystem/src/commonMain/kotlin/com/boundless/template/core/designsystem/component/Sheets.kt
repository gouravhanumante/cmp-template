package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * A multi-field form presented as a sheet rather than an `AlertDialog`. Dialogs suit one
 * decision; anything with more than one input field reads better sliding up from the bottom,
 * where there is room to breathe and the keyboard doesn't fight the layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFormSheet(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmLabel: String,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    loading: Boolean = false,
    dismissLabel: String = stringResource(Res.string.ds_cancel),
    autoScrollToBottomKey: Any? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    LaunchedEffect(autoScrollToBottomKey) {
        if (autoScrollToBottomKey != null) {
            withFrameNanos { }
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = Spacing.screen)
                    .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm), content = content)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                AppTextButton(text = dismissLabel, onClick = onDismiss, modifier = Modifier.weight(1f))
                AppPrimaryButton(
                    text = confirmLabel,
                    onClick = onConfirm,
                    enabled = confirmEnabled,
                    loading = loading,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
