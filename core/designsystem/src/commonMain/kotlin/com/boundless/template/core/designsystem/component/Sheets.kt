package com.boundless.template.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.boundless.template.core.designsystem.theme.Breakpoints
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.designsystem.theme.WidthClass
import com.boundless.template.core.designsystem.theme.windowWidthClass
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * A multi-field form presented as a sheet rather than an `AlertDialog`. Dialogs suit one
 * decision; anything with more than one input field reads better sliding up from the bottom,
 * where there is room to breathe and the keyboard doesn't fight the layout.
 * On wide screens it slides in from the right instead, so the list behind stays in view.
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
    val scrollState = rememberScrollState()

    LaunchedEffect(autoScrollToBottomKey) {
        if (autoScrollToBottomKey != null) {
            withFrameNanos { }
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val body: @Composable () -> Unit = {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    // Enter saves, as in any desktop form; every field in a form sheet is one line.
                    .onPreviewKeyEvent { event ->
                        val enter = event.type == KeyEventType.KeyDown && (event.key == Key.Enter || event.key == Key.NumPadEnter) && !event.isShiftPressed
                        if (enter && confirmEnabled && !loading) onConfirm()
                        enter
                    }
                    .imePadding()
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

    // Tablets and desktops (from 600 dp) get the panel from the right.
    if (windowWidthClass() != WidthClass.COMPACT) {
        AppSidePanel(onDismiss = onDismiss, modifier = modifier, content = body)
    } else {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, modifier = modifier) { body() }
    }
}

/** Full-height panel that slides in from the right; clicking the dimmed area closes it. */
@Composable
internal fun AppSidePanel(
    onDismiss: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            // Taps only: a clickable here would also fire on Space or Enter typed into the panel's fields.
            Modifier.fillMaxSize().pointerInput(onDismiss) { detectTapGestures { onDismiss() } },
            contentAlignment = Alignment.CenterEnd,
        ) {
            val shown = remember { MutableTransitionState(false).apply { targetState = true } }
            AnimatedVisibility(visibleState = shown, enter = slideInHorizontally { it }) {
                Surface(
                    modifier = modifier.width(Breakpoints.sidePanel).fillMaxHeight().pointerInput(Unit) { detectTapGestures { } },
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Box(Modifier.safeDrawingPadding().padding(top = Spacing.lg)) { content() }
                }
            }
        }
    }
}
