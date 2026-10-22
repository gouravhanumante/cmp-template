package com.boundless.template.feature.devtools.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.boundless.template.core.designsystem.component.*
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.designsystem.theme.AppPalette
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing
import com.boundless.template.core.designsystem.theme.appAccents
import com.boundless.template.core.resources.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

private val icons: List<ImageVector>
    get() =
        listOf(
            AppIcons.Search, AppIcons.Calendar, AppIcons.Person, AppIcons.Wallet, AppIcons.Home, AppIcons.Check,
            AppIcons.Shield, AppIcons.Pin, AppIcons.Clock, AppIcons.Refresh, AppIcons.Chevron, AppIcons.Back,
            AppIcons.Close, AppIcons.Plus, AppIcons.Coffee, AppIcons.Movie, AppIcons.Bike, AppIcons.Car,
            AppIcons.Utensils, AppIcons.Map, AppIcons.Sparkle, AppIcons.Star, AppIcons.ShoppingBag, AppIcons.Walk,
            AppIcons.Gamepad, AppIcons.Palette, AppIcons.Ticket, AppIcons.Heart, AppIcons.HeartFilled,
            AppIcons.ArrowUp, AppIcons.Flag,
        )

/** Every shared component with sample content, so a change to the design system is seen in one place. */
@Composable
fun ComponentsScreen(
    onBack: () -> Unit,
    viewModel: ComponentsViewModel = koinViewModel(),
) {
    val palette by viewModel.palette.collectAsStateWithLifecycle()
    AppScreen(
        title = stringResource(Res.string.devtools_components_title),
        onBack = onBack,
        contentSpacing = Spacing.section,
    ) {
        AppSection(
            title = stringResource(Res.string.devtools_cmp_theme),
            subtitle = stringResource(Res.string.devtools_cmp_theme_body),
        ) {
            AppSegmentedTabs(
                options = paletteOptions(),
                selectedId = palette.name,
                onSelect = { id -> AppPalette.entries.firstOrNull { it.name == id }?.let(viewModel::onSelectPalette) },
            )
        }
        ButtonsSection()
        SelectionSection()
        FieldsSection()
        FeedbackSection()
        CardsSection()
        TilesSection()
        DatesSection()
        StatesSection()
        IconsSection()
    }
}

@Composable
private fun paletteOptions(): List<SelectableOption> =
    AppPalette.entries.map { palette ->
        val label =
            when (palette) {
                AppPalette.INDIGO -> Res.string.devtools_cmp_theme_indigo
                AppPalette.TEAL -> Res.string.devtools_cmp_theme_teal
                AppPalette.TERRACOTTA -> Res.string.devtools_cmp_theme_terracotta
                AppPalette.FOREST -> Res.string.devtools_cmp_theme_forest
            }
        SelectableOption(palette.name, stringResource(label))
    }

@Composable
private fun ButtonsSection() {
    AppSection(title = stringResource(Res.string.devtools_cmp_buttons)) {
        AppPrimaryButton(text = stringResource(Res.string.devtools_cmp_primary), onClick = {})
        AppPrimaryButton(text = stringResource(Res.string.devtools_cmp_loading), onClick = {}, loading = true)
        AppPrimaryButton(text = stringResource(Res.string.devtools_cmp_disabled), onClick = {}, enabled = false)
        AppSecondaryButton(text = stringResource(Res.string.devtools_cmp_secondary), onClick = {})
        AppSecondaryButton(text = stringResource(Res.string.devtools_cmp_destructive), onClick = {}, destructive = true)
        AppTonalButton(text = stringResource(Res.string.devtools_cmp_tonal), onClick = {})
        AppTonalButton(text = stringResource(Res.string.devtools_cmp_caution), onClick = {}, caution = true)
        AppTextButton(text = stringResource(Res.string.devtools_cmp_text_button), onClick = {}, icon = AppIcons.Plus)
        AppChipButton(text = stringResource(Res.string.devtools_cmp_chip_button), onClick = {}, icon = AppIcons.Check)
    }
}

@Composable
private fun timeOfDayOptions(): List<SelectableOption> =
    listOf(
        SelectableOption("morning", stringResource(Res.string.devtools_cmp_option_morning)),
        SelectableOption("afternoon", stringResource(Res.string.devtools_cmp_option_afternoon)),
        SelectableOption("evening", stringResource(Res.string.devtools_cmp_option_evening)),
    )

@Composable
private fun SelectionSection() {
    val options = timeOfDayOptions()
    var tab by remember { mutableStateOf("morning") }
    var many by remember { mutableStateOf(setOf("afternoon")) }
    var one by remember { mutableStateOf<String?>("evening") }
    var switchedOn by remember { mutableStateOf(true) }
    AppSection(title = stringResource(Res.string.devtools_cmp_selection)) {
        AppSegmentedTabs(options = options, selectedId = tab, onSelect = { tab = it })
        AppChipGroup(
            title = stringResource(Res.string.devtools_cmp_chip_group),
            options = options,
            selectedIds = many,
            onToggle = { id -> many = if (id in many) many - id else many + id },
        )
        AppSingleChoiceGroup(title = stringResource(Res.string.devtools_cmp_single_choice), options = options, selectedId = one, onSelect = { one = it })
        AppCard {
            AppSwitchRow(title = stringResource(Res.string.devtools_cmp_switch), checked = switchedOn, onCheckedChange = { switchedOn = it })
        }
    }
}

@Composable
private fun FieldsSection() {
    var name by remember { mutableStateOf("") }
    var broken by remember { mutableStateOf("x") }
    var choice by remember { mutableStateOf<String?>(null) }
    var phone by remember { mutableStateOf("") }
    AppSection(title = stringResource(Res.string.devtools_cmp_fields)) {
        AppTextField(
            value = name,
            onValueChange = { name = it },
            label = stringResource(Res.string.devtools_cmp_field_label),
            supportingText = stringResource(Res.string.devtools_cmp_field_hint),
        )
        AppTextField(
            value = broken,
            onValueChange = { broken = it },
            label = stringResource(Res.string.devtools_cmp_field_label),
            supportingText = stringResource(Res.string.devtools_cmp_field_error),
            isError = true,
        )
        AppDropdownField(label = stringResource(Res.string.devtools_cmp_dropdown), options = timeOfDayOptions(), selectedId = choice, onSelect = { choice = it })
        AppPhoneField(localNumber = phone, onLocalNumberChange = { phone = it })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FeedbackSection() {
    AppSection(title = stringResource(Res.string.devtools_cmp_feedback)) {
        AppNoticeBanner(
            title = stringResource(Res.string.devtools_cmp_notice_title),
            message = stringResource(Res.string.devtools_cmp_notice_body),
            tone = NoticeTone.INFO,
        )
        AppNoticeBanner(
            title = stringResource(Res.string.devtools_cmp_notice_title),
            message = stringResource(Res.string.devtools_cmp_notice_body),
            tone = NoticeTone.WARNING,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            AppStatusPill(text = stringResource(Res.string.devtools_cmp_status_confirmed), tone = StatusTone.POSITIVE)
            AppStatusPill(text = stringResource(Res.string.devtools_cmp_status_pending), tone = StatusTone.PENDING)
            AppStatusPill(text = stringResource(Res.string.devtools_cmp_status_draft), tone = StatusTone.NEUTRAL)
            AppStatusPill(text = stringResource(Res.string.devtools_cmp_status_cancelled), tone = StatusTone.NEGATIVE)
            AppTrustBadge(text = stringResource(Res.string.devtools_cmp_badge))
            AppTagChip(text = stringResource(Res.string.devtools_cmp_tag))
        }
        AppProgressRing(percent = 64)
        ShimmerHost {
            AppSkeletonLine(width = AppSize.hero)
            AppSkeletonLine(width = AppSize.photoMd)
        }
    }
}

@Composable
private fun CardsSection() {
    AppSection(title = stringResource(Res.string.devtools_cmp_cards)) {
        AppActionCard(
            title = stringResource(Res.string.devtools_cmp_card_title),
            body = stringResource(Res.string.devtools_cmp_card_body),
            icon = AppIcons.Sparkle,
            action = { AppChipButton(text = stringResource(Res.string.devtools_cmp_chip_button), onClick = {}) },
        )
        AppCard {
            AppListRow(
                title = stringResource(Res.string.devtools_cmp_card_title),
                subtitle = stringResource(Res.string.devtools_cmp_card_body),
                leading = { AppIconTile(icon = AppIcons.Calendar) },
                trailing = { Icon(AppIcons.Chevron, contentDescription = null) },
                onClick = {},
            )
            AppDivider()
            AppSummaryRow(label = stringResource(Res.string.devtools_cmp_summary_label), value = stringResource(Res.string.devtools_cmp_summary_value))
            AppSummaryRow(label = stringResource(Res.string.devtools_cmp_summary_label), value = stringResource(Res.string.devtools_cmp_summary_value), emphasise = true)
        }
    }
}

@Composable
private fun TilesSection() {
    val accents = appAccents()
    AppSection(title = stringResource(Res.string.devtools_cmp_tiles)) {
        AppHeroStat(value = stringResource(Res.string.devtools_cmp_stat_value), label = stringResource(Res.string.devtools_cmp_stat_label), accent = accents.first())
        AppStatTile(
            value = stringResource(Res.string.devtools_cmp_stat_value),
            label = stringResource(Res.string.devtools_cmp_stat_label),
            icon = AppIcons.Star,
            accent = accents.last(),
        )
    }
}

@Composable
private fun DatesSection() {
    var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 22)) }
    var time by remember { mutableStateOf("09:30") }
    var distance by remember { mutableStateOf(5f) }
    AppSection(title = stringResource(Res.string.devtools_cmp_dates)) {
        AppDateField(label = stringResource(Res.string.devtools_cmp_date), value = date, onValueChange = { date = it })
        AppTimeField(label = stringResource(Res.string.devtools_cmp_time), value = time, onValueChange = { time = it })
        AppSlider(title = stringResource(Res.string.devtools_cmp_slider), value = distance, onValueChange = { distance = it }, valueRange = 1f..20f)
    }
}

@Composable
private fun StatesSection() {
    var sheetOpen by remember { mutableStateOf(false) }
    var sheetName by remember { mutableStateOf("") }
    var confettiRun by remember { mutableIntStateOf(0) }
    AppSection(title = stringResource(Res.string.devtools_cmp_states)) {
        AppSecondaryButton(text = stringResource(Res.string.devtools_cmp_open_sheet), onClick = { sheetOpen = true })
        AppSecondaryButton(text = stringResource(Res.string.devtools_cmp_confetti), onClick = { confettiRun++ })
        AppEmptyState(title = stringResource(Res.string.devtools_cmp_empty_title), body = stringResource(Res.string.devtools_cmp_empty_body))
        Box(Modifier.fillMaxWidth().height(AppSize.hero)) {
            AppFullScreenError(message = stringResource(Res.string.devtools_cmp_error), onRetry = {})
        }
    }
    if (confettiRun > 0) key(confettiRun) { AppConfettiBurst() }
    if (sheetOpen) {
        AppFormSheet(
            title = stringResource(Res.string.devtools_cmp_sheet_title),
            onDismiss = { sheetOpen = false },
            onConfirm = { sheetOpen = false },
            confirmLabel = stringResource(Res.string.devtools_cmp_sheet_confirm),
        ) {
            AppTextField(value = sheetName, onValueChange = { sheetName = it }, label = stringResource(Res.string.devtools_cmp_field_label))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconsSection() {
    AppSection(title = stringResource(Res.string.devtools_cmp_icons)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            icons.forEach { Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface) }
        }
    }
}
