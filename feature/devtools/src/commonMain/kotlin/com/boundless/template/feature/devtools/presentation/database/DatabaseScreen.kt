package com.boundless.template.feature.devtools.presentation.database

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boundless.template.core.designsystem.component.AppCard
import com.boundless.template.core.designsystem.component.AppDivider
import com.boundless.template.core.designsystem.component.AppEmptyState
import com.boundless.template.core.designsystem.component.AppErrorText
import com.boundless.template.core.designsystem.component.AppListRow
import com.boundless.template.core.designsystem.component.AppNoticeBanner
import com.boundless.template.core.designsystem.component.AppPrimaryButton
import com.boundless.template.core.designsystem.component.AppScreen
import com.boundless.template.core.designsystem.component.AppSecondaryButton
import com.boundless.template.core.designsystem.component.AppSection
import com.boundless.template.core.designsystem.component.AppTextButton
import com.boundless.template.core.designsystem.component.AppTextField
import com.boundless.template.core.designsystem.component.NoticeTone
import com.boundless.template.core.resources.Res
import com.boundless.template.core.resources.devtools_database_title
import com.boundless.template.core.resources.devtools_db_add
import com.boundless.template.core.resources.devtools_db_count
import com.boundless.template.core.resources.devtools_db_delete
import com.boundless.template.core.resources.devtools_db_delete_all
import com.boundless.template.core.resources.devtools_db_empty_body
import com.boundless.template.core.resources.devtools_db_empty_title
import com.boundless.template.core.resources.devtools_db_memory_body
import com.boundless.template.core.resources.devtools_db_memory_title
import com.boundless.template.core.resources.devtools_db_note_label
import com.boundless.template.core.resources.devtools_db_subtitle
import com.boundless.template.core.resources.resolveOrNull
import com.boundless.template.feature.devtools.domain.NoteStorage
import com.boundless.template.feature.devtools.presentation.formatTimestamp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DatabaseScreen(
    onBack: () -> Unit,
    viewModel: DatabaseViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DatabaseContent(state = state, onEvent = viewModel::onEvent, onBack = onBack)
}

@Composable
fun DatabaseContent(
    state: DatabaseUiState,
    onEvent: (DatabaseEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScreen(
        title = stringResource(Res.string.devtools_database_title),
        subtitle = stringResource(Res.string.devtools_db_subtitle),
        onBack = onBack,
        modifier = modifier,
    ) {
        if (state.storage == NoteStorage.MEMORY_ONLY) {
            AppNoticeBanner(
                title = stringResource(Res.string.devtools_db_memory_title),
                message = stringResource(Res.string.devtools_db_memory_body),
                tone = NoticeTone.WARNING,
            )
        }

        AppCard {
            AppTextField(
                value = state.draft,
                onValueChange = { onEvent(DatabaseEvent.DraftChanged(it)) },
                label = stringResource(Res.string.devtools_db_note_label),
            )
            AppErrorText(message = state.errorMessage.resolveOrNull())
            AppPrimaryButton(
                text = stringResource(Res.string.devtools_db_add),
                onClick = { onEvent(DatabaseEvent.Save) },
                enabled = state.canSave,
                loading = state.isSaving,
            )
        }

        when {
            state.isLoading -> CircularProgressIndicator()
            state.notes.isEmpty() ->
                AppEmptyState(
                    title = stringResource(Res.string.devtools_db_empty_title),
                    body = stringResource(Res.string.devtools_db_empty_body),
                )
            else ->
                AppSection(title = stringResource(Res.string.devtools_db_count, state.notes.size.toString())) {
                    AppCard {
                        state.notes.forEachIndexed { index, note ->
                            if (index > 0) AppDivider()
                            AppListRow(
                                title = note.text,
                                subtitle = formatTimestamp(note.createdAtMillis),
                                trailing = {
                                    AppTextButton(
                                        text = stringResource(Res.string.devtools_db_delete),
                                        onClick = { onEvent(DatabaseEvent.Delete(note.id)) },
                                    )
                                },
                            )
                        }
                    }
                    AppSecondaryButton(
                        text = stringResource(Res.string.devtools_db_delete_all),
                        onClick = { onEvent(DatabaseEvent.DeleteAll) },
                        destructive = true,
                    )
                }
        }
    }
}
