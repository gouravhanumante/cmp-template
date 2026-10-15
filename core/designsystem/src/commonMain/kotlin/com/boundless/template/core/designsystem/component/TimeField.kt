package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.boundless.template.core.common.model.TimeOfDay
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource

/** Which way [AppTimeField] moves a time that lands between two steps. */
enum class TimeSnap {
    NEAREST,

    /** For the opening end of a range: 09:40 becomes 10:00, never 09:30. */
    UP,

    /** For the closing end: 17:20 becomes 17:00, never 17:30. */
    DOWN,
}

/**
 * Start/end-of-day time picker. Reads/writes the 24-hour `HH:mm` string the availability API
 * expects, but lets people dial it in on a clock face instead of typing digits and doing the
 * 24-hour math themselves.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    /** Snaps the dialled time to a multiple of this. A clock face makes :02 as easy to land on as
     *  :00, and the field then shows what was actually chosen. */
    stepMinutes: Int = 1,
    /** Must match how the server treats the same value, or the field shows one time and another
     *  gets stored. Availability windows are narrowed to the slot grid, so their ends snap
     *  inward. */
    snap: TimeSnap = TimeSnap.NEAREST,
) {
    var showPicker by remember { mutableStateOf(false) }
    val minutes = TimeOfDay.parse(value)

    Box(modifier = modifier.fillMaxWidth().clickable(enabled = enabled) { showPicker = true }) {
        OutlinedTextField(
            value = minutes?.let { TimeOfDay.formatDisplay(it) } ?: value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            // The wrapper owns interaction; the field is display-only. Keep its text normal even
            // though disabled prevents the child from consuming the wrapper's click.
            enabled = false,
            readOnly = true,
            singleLine = true,
            colors =
                OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                ),
        )
    }

    if (showPicker) {
        val initialMinutes = minutes ?: 0
        val state =
            rememberTimePickerState(
                initialHour = initialMinutes / 60,
                initialMinute = initialMinutes % 60,
                is24Hour = false,
            )
        TimePickerDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(label) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val snapped = snap.applyTo(state.hour * 60 + state.minute, stepMinutes)
                        onValueChange(TimeOfDay.format(snapped.coerceIn(0, MINUTES_IN_DAY - 1)))
                        showPicker = false
                    },
                ) { Text(stringResource(Res.string.ds_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text(stringResource(Res.string.ds_cancel)) }
            },
        ) {
            TimePicker(state = state)
        }
    }
}

private const val MINUTES_IN_DAY = 24 * 60

internal fun TimeSnap.applyTo(minuteOfDay: Int, stepMinutes: Int): Int =
    when (this) {
        TimeSnap.NEAREST -> ((minuteOfDay + stepMinutes / 2) / stepMinutes) * stepMinutes
        TimeSnap.UP -> ((minuteOfDay + stepMinutes - 1) / stepMinutes) * stepMinutes
        TimeSnap.DOWN -> (minuteOfDay / stepMinutes) * stepMinutes
    }
