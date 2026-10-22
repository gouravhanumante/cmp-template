package com.boundless.template.feature.devtools.presentation.checks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boundless.template.core.designsystem.component.*
import com.boundless.template.core.designsystem.format.localeFormat
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.designsystem.theme.WidthClass
import com.boundless.template.core.designsystem.theme.windowWidthClass
import com.boundless.template.core.resources.*
import com.boundless.template.feature.devtools.domain.NoteStorage
import com.boundless.template.feature.devtools.presentation.formatTimestamp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Desktop's file dialog filters by extension; an empty list would let nothing through.
private val PICKABLE_EXTENSIONS = listOf("pdf", "txt", "csv", "json", "png", "jpg", "jpeg", "webp")
private const val SAMPLE_INSTANT = "2026-10-22T09:30:00Z"

private data class LocaleSample(val locale: String, val currency: String, val timeZone: String)

private val localeSamples =
    listOf(
        LocaleSample("en-IN", "INR", "Asia/Kolkata"),
        LocaleSample("en-US", "USD", "America/New_York"),
        LocaleSample("ja-JP", "JPY", "Asia/Tokyo"),
    )

@Composable
fun ChecksScreen(
    onBack: () -> Unit,
    viewModel: ChecksViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChecksContent(state = state, onEvent = viewModel::onEvent, onBack = onBack)
}

@Composable
fun ChecksContent(
    state: ChecksUiState,
    onEvent: (ChecksEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScreen(
        title = stringResource(Res.string.devtools_checks_title),
        onBack = onBack,
        modifier = modifier,
        contentSpacing = Spacing.section,
    ) {
        BuildSection(state)

        AppSection(title = null) {
            AppPrimaryButton(text = stringResource(Res.string.devtools_checks_run_all), onClick = { onEvent(ChecksEvent.RunAll) })
            CheckCard(
                title = Res.string.devtools_check_settings_title,
                body = stringResource(Res.string.devtools_check_settings_body),
                status = state.checks.getValue(CheckId.SETTINGS),
                onRun = { onEvent(ChecksEvent.Run(CheckId.SETTINGS)) },
                note =
                    state.lastSettingsValue?.toLongOrNull()?.let { stringResource(Res.string.devtools_check_settings_last, formatTimestamp(it)) }
                        ?: stringResource(Res.string.devtools_check_settings_never),
            )
            CheckCard(
                title = Res.string.devtools_check_secure_title,
                body = stringResource(Res.string.devtools_check_secure_body),
                status = state.checks.getValue(CheckId.SECURE_STORAGE),
                onRun = { onEvent(ChecksEvent.Run(CheckId.SECURE_STORAGE)) },
            )
            CheckCard(
                title = Res.string.devtools_check_database_title,
                body = stringResource(Res.string.devtools_check_database_body),
                status = state.checks.getValue(CheckId.DATABASE),
                onRun = { onEvent(ChecksEvent.Run(CheckId.DATABASE)) },
            )
            CheckCard(
                title = Res.string.devtools_check_backend_title,
                body = stringResource(Res.string.devtools_check_backend_body, state.apiBaseUrl),
                status = state.checks.getValue(CheckId.BACKEND),
                onRun = { onEvent(ChecksEvent.Run(CheckId.BACKEND)) },
                passedDetail = { stringResource(Res.string.devtools_check_backend_version, it) },
            )
        }

        FormatSection()
        PickerSection(state, onEvent)

        AppSection(
            title = stringResource(Res.string.devtools_section_simulate),
            subtitle = stringResource(Res.string.devtools_simulate_body),
        ) {
            AppSecondaryButton(text = stringResource(Res.string.devtools_simulate_maintenance), onClick = { onEvent(ChecksEvent.SimulateMaintenance) })
            AppSecondaryButton(text = stringResource(Res.string.devtools_simulate_session), onClick = { onEvent(ChecksEvent.SimulateSessionEnded) })
        }
    }
}

@Composable
private fun BuildSection(state: ChecksUiState) {
    val width =
        when (windowWidthClass()) {
            WidthClass.COMPACT -> Res.string.devtools_width_compact
            WidthClass.MEDIUM -> Res.string.devtools_width_medium
            WidthClass.EXPANDED -> Res.string.devtools_width_expanded
        }
    AppSection(title = stringResource(Res.string.devtools_section_build)) {
        AppCard {
            AppSummaryRow(stringResource(Res.string.devtools_platform), state.platform.headerValue)
            AppSummaryRow(
                stringResource(Res.string.devtools_build_type),
                stringResource(if (state.isDebugBuild) Res.string.devtools_build_debug else Res.string.devtools_build_release),
            )
            AppSummaryRow(stringResource(Res.string.devtools_app_build), state.appBuild?.toString() ?: stringResource(Res.string.devtools_unknown))
            AppSummaryRow(stringResource(Res.string.devtools_screen_size), stringResource(width))
            AppSummaryRow(
                stringResource(Res.string.devtools_storage),
                stringResource(
                    when (state.storage) {
                        NoteStorage.ON_DEVICE -> Res.string.devtools_storage_device
                        NoteStorage.MEMORY_ONLY -> Res.string.devtools_storage_memory
                    },
                ),
            )
        }
    }
}

@Composable
private fun CheckCard(
    title: StringResource,
    body: String,
    status: CheckStatus,
    onRun: () -> Unit,
    note: String? = null,
    passedDetail: @Composable (String) -> String = { it },
) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            StatusPill(status)
            AppChipButton(text = stringResource(Res.string.devtools_check_run), onClick = onRun, enabled = status != CheckStatus.Running)
        }
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        when (status) {
            is CheckStatus.Passed -> status.detail?.let { Text(passedDetail(it), style = MaterialTheme.typography.bodyMedium) }
            is CheckStatus.Failed -> AppErrorText(message = status.message.resolve())
            CheckStatus.NotRun, CheckStatus.Running -> Unit
        }
        note?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun StatusPill(status: CheckStatus) {
    val (label, tone) =
        when (status) {
            CheckStatus.NotRun -> Res.string.devtools_check_not_run to StatusTone.NEUTRAL
            CheckStatus.Running -> Res.string.devtools_check_running to StatusTone.PENDING
            is CheckStatus.Passed -> Res.string.devtools_check_passed to StatusTone.POSITIVE
            is CheckStatus.Failed -> Res.string.devtools_check_failed to StatusTone.NEGATIVE
        }
    AppStatusPill(text = stringResource(label), tone = tone)
}

@Composable
private fun FormatSection() {
    AppSection(
        title = stringResource(Res.string.devtools_section_format),
        subtitle = stringResource(Res.string.devtools_format_body),
    ) {
        AppCard {
            localeSamples.forEachIndexed { index, sample ->
                if (index > 0) AppDivider()
                val format = remember(sample) { localeFormat(sample.locale, sample.currency, sample.timeZone) }
                Text("${sample.locale} · ${sample.currency} · ${sample.timeZone}", style = MaterialTheme.typography.labelLarge)
                Text(
                    "${format.number(12_345_678)}   ${format.money(123_456_750)}   ${format.dateTime(SAMPLE_INSTANT)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun PickerSection(
    state: ChecksUiState,
    onEvent: (ChecksEvent) -> Unit,
) {
    val pickFile =
        rememberFilePicker(
            mimeTypes = listOf("*/*"),
            extensions = PICKABLE_EXTENSIONS,
            onPicked = { name, bytes -> onEvent(ChecksEvent.FilePicked(name, bytes)) },
            onFailed = { onEvent(ChecksEvent.PickFailed) },
        )
    val pickImage =
        rememberImagePicker(
            source = ImageSource.GALLERY,
            onImageSelected = { bytes, _ -> onEvent(ChecksEvent.ImagePicked(bytes)) },
            onError = { onEvent(ChecksEvent.PickFailed) },
        )
    AppSection(title = stringResource(Res.string.devtools_section_pickers)) {
        AppCard {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                AppChipButton(text = stringResource(Res.string.devtools_pick_file), onClick = pickFile)
                AppChipButton(text = stringResource(Res.string.devtools_pick_image), onClick = pickImage)
            }
            state.pickedFile?.let {
                Text(stringResource(Res.string.devtools_picked_file, it.name, it.sizeBytes.toString()), style = MaterialTheme.typography.bodyMedium)
            }
            state.pickedImage?.let { image ->
                val bitmap = remember(image) { image.bytes.decodeToImageBitmapOrNull() }
                bitmap?.let { Image(bitmap = it, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(AppSize.photoMd)) }
            }
            AppErrorText(message = state.pickError.resolveOrNull())
        }
    }
}
