package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.boundless.template.core.resources.*
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

private const val MILLIS_PER_DAY = 86_400_000L

/**
 * Date-of-birth picker. Reads/writes a [LocalDate] and converts via epoch days so no
 * time zone can shift the stored birth date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateField(
    label: String,
    value: LocalDate?,
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    maximumDate: LocalDate? = null,
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value?.toString().orEmpty(),
        onValueChange = {},
        modifier = modifier.fillMaxWidth().clickable(enabled = enabled) { showPicker = true },
        label = { Text(label) },
        placeholder = { Text(stringResource(Res.string.ds_date_placeholder)) },
        supportingText = supportingText?.let { { Text(it) } },
        isError = isError,
        // The field itself stays read-only; the dialog is the only way to change the value.
        enabled = false,
        readOnly = true,
        singleLine = true,
    )

    if (showPicker) {
        val state =
            rememberDatePickerState(
                initialSelectedDateMillis = value?.let { it.toEpochDays() * MILLIS_PER_DAY },
                initialDisplayedMonthMillis =
                    (value ?: maximumDate)?.let { it.toEpochDays() * MILLIS_PER_DAY },
                selectableDates =
                    object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                            maximumDate?.let { utcTimeMillis / MILLIS_PER_DAY <= it.toEpochDays() } ?: true
                    },
            )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let {
                            onValueChange(LocalDate.fromEpochDays(it / MILLIS_PER_DAY))
                        }
                        showPicker = false
                    },
                ) { Text(stringResource(Res.string.ds_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text(stringResource(Res.string.ds_cancel)) }
            },
        ) {
            DatePicker(state = state)
        }
    }
}
