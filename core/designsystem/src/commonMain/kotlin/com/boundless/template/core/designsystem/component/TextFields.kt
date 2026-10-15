package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.designsystem.theme.AppRadius
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = supportingText?.let { { Text(it) } },
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
    )
}

/**
 * A single-select dropdown styled like every other field on the form (outlined, labelled) rather
 * than a picker widget that looks imported from somewhere else — the field itself shows the
 * current choice and opens a proper Material3 exposed menu on tap, not a bare native select.
 *
 * [dotColor], when given, draws a small colour dot before each option's label (in both the field
 * and the menu) — pass the app's own restrained accent set here rather than inventing new hues
 * per option.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDropdownField(
    label: String,
    options: List<SelectableOption>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
    dotColor: ((String) -> Color?)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = options.firstOrNull { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selectedOption?.label.orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            leadingIcon = (dotColor?.invoke(selectedOption?.id.orEmpty()))?.let { color -> { OptionDot(color) } },
            supportingText = supportingText?.let { { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled).fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                val selected = option.id == selectedId
                DropdownMenuItem(
                    text = { Text(option.label, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal) },
                    onClick = {
                        onSelect(option.id)
                        expanded = false
                    },
                    leadingIcon = dotColor?.invoke(option.id)?.let { color -> { OptionDot(color) } },
                    trailingIcon = if (selected) {
                        { Icon(imageVector = AppIcons.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                    } else {
                        null
                    },
                    colors =
                        MenuDefaults.itemColors(
                            textColor =
                                if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        ),
                    modifier =
                        if (selected) {
                            Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                        } else {
                            Modifier
                        },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

/**
 * A free-typed field with a real anchored dropdown of suggestions — unlike a plain list rendered
 * below the field in normal document flow, this stays visible regardless of scroll position and
 * never ends up hidden behind the keyboard. Opens automatically whenever [suggestions] is
 * non-empty; the caller owns clearing it (on selection, a short-enough query, or [onDismissRequest]).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAutocompleteField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suggestions: List<SelectableOption>,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onDismissRequest: () -> Unit = {},
) {
    val expanded = suggestions.isNotEmpty()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (!it) onDismissRequest() },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            supportingText = supportingText?.let { { Text(it) } },
            isError = isError,
            enabled = enabled,
            singleLine = true,
            trailingIcon =
                if (isLoading) {
                    { CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) }
                } else {
                    null
                },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled).fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
        ) {
            suggestions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = { onSuggestionSelected(option.id) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
private fun OptionDot(color: Color) {
    Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
}

/** Digits-only phone entry with a fixed country prefix; the caller keeps the local part only. */
@Composable
fun AppPhoneField(
    localNumber: String,
    onLocalNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    dialCode: String = "+91",
    maxDigits: Int = 10,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = localNumber,
        onValueChange = { input -> onLocalNumberChange(input.stripDialCode(dialCode, maxDigits)) },
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(Res.string.ds_phone_number)) },
        prefix = { Text(dialCode, style = MaterialTheme.typography.bodyLarge) },
        supportingText = supportingText?.let { { Text(it) } },
        isError = isError,
        enabled = enabled,
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            ),
    )
}

/**
 * Autofill (and pasting a copied number) commonly delivers the dial code baked into the value,
 * e.g. "+919876543210" — since the field already renders "+91" as its own prefix, blindly keeping
 * the first [maxDigits] digits of that would silently drop the last two real digits instead of the
 * duplicated country code. Only strips when the digit count actually overflows [maxDigits]; a
 * number a caller is still typing by hand never reaches this length, so it's never touched.
 */
private fun String.stripDialCode(
    dialCode: String,
    maxDigits: Int,
): String {
    val digits = filter { it.isDigit() }
    val dialDigits = dialCode.filter { it.isDigit() }
    val withoutDialCode =
        if (digits.length > maxDigits && dialDigits.isNotEmpty() && digits.startsWith(dialDigits)) {
            digits.removePrefix(dialDigits)
        } else {
            digits
        }
    return withoutDialCode.take(maxDigits)
}

/** One cell per digit over an invisible input. Android and iOS share the Compose field; web uses a native input. */
@Composable
expect fun AppOtpField(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
)

@Composable
internal fun ComposeOtpField(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier,
    length: Int,
    isError: Boolean,
    supportingText: String?,
    enabled: Boolean,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        BasicTextField(
            value = code,
            onValueChange = { input -> onCodeChange(input.filter { it.isDigit() }.take(length)) },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            enabled = enabled,
            cursorBrush = SolidColor(Color.Transparent),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                ),
            decorationBox = { innerTextField ->
                Box {
                    OtpDigitRow(code = code, length = length, isError = isError, enabled = enabled)
                    Box(modifier = Modifier.matchParentSize().alpha(0f)) { innerTextField() }
                }
            },
        )

        OtpSupportingText(supportingText)
    }
}

@Composable
internal fun OtpDigitRow(
    code: String,
    length: Int,
    isError: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        repeat(length) { index ->
            OtpDigitCell(
                digit = code.getOrNull(index),
                isActive = enabled && index == code.length && code.length < length,
                isError = isError,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
internal fun OtpSupportingText(supportingText: String?) {
    if (supportingText != null) {
        Text(
            text = supportingText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun OtpDigitCell(
    digit: Char?,
    isActive: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor =
        when {
            isError -> MaterialTheme.colorScheme.error
            isActive -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outlineVariant
        }

    Box(
        modifier =
            modifier
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surface, AppRadius.field)
                .border(if (isActive || isError) 2.dp else 1.dp, borderColor, AppRadius.field),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = digit?.toString().orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
    }
}
