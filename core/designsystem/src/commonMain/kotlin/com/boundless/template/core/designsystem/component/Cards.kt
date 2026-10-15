package com.boundless.template.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.boundless.template.core.designsystem.icon.AppIcons
import com.boundless.template.core.designsystem.theme.AppAccent
import com.boundless.template.core.designsystem.theme.AppRadius
import com.boundless.template.core.designsystem.theme.AppSize
import com.boundless.template.core.designsystem.theme.Spacing

typealias ColumnScope = androidx.compose.foundation.layout.ColumnScope

/** The one internal rhythm every card follows, so no card invents its own padding. */
internal object CardMetrics {
    val padding = Spacing.md
    val mediaGap = Spacing.smd
    val lineGap = Spacing.xs
    val actionSize = AppSize.minTouch
}

enum class AppCardVariant {
    /** Surface with a soft lift. The default for anything in a list. */
    OUTLINED,

    /** Tonal fill from [AppTone], flat. Use for a selected or highlighted item. */
    FILLED,

    /** Same as [OUTLINED]; kept so photo-forward surfaces can state their intent. */
    ELEVATED,
}

/**
 * The single card container. A soft shadow rather than a hairline box — a border makes a list
 * read as a table, which is what made every screen here feel flat.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    variant: AppCardVariant = AppCardVariant.OUTLINED,
    tone: AppTone = AppTone.NEUTRAL,
    selected: Boolean = false,
    padded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val filled = variant == AppCardVariant.FILLED
    val container =
        when {
            filled ->
                when (tone) {
                    AppTone.NEUTRAL -> colors.surfaceContainer
                    AppTone.PRIMARY -> colors.primaryContainer
                    AppTone.SECONDARY -> colors.secondaryContainer
                    AppTone.TERTIARY -> colors.tertiaryContainer
                    AppTone.CRITICAL -> colors.errorContainer
                }

            else -> colors.surface
        }

    Card(
        modifier = modifier.fillMaxWidth().let { if (onClick != null) it.clickable(onClick = onClick) else it },
        shape = AppRadius.card,
        colors = CardDefaults.cardColors(containerColor = container),
        border = if (selected) BorderStroke(2.dp, colors.primary) else null,
        elevation =
            if (filled) {
                CardDefaults.cardElevation(defaultElevation = 0.dp)
            } else {
                CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
            },
    ) {
        Column(
            modifier = if (padded) Modifier.padding(CardMetrics.padding) else Modifier,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            content = content,
        )
    }
}

/** Circular person image with a neutral placeholder when there is no photo. */
@Composable
fun AppAvatar(
    url: String?,
    modifier: Modifier = Modifier,
    size: Dp = AppSize.avatarMd,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        if (url != null) {
            val context = LocalPlatformContext.current
            AsyncImage(
                // At this size (AppSize.avatarMd/Lg — a few dozen dp), a crossfade is barely
                // visible but still runs its own per-frame transition; the global ImageLoader
                // default (see AppImageLoader.kt) is worth it for a large hero photo, not here —
                // and an avatar tends to load right as its screen becomes the one being pushed
                // away from (e.g. Profile, opened then immediately navigated on from), so skipping
                // it avoids stacking a second animation onto the nav transition for no visible gain.
                model = remember(url) { ImageRequest.Builder(context).data(url).crossfade(false).build() },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = AppIcons.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(size / 2),
            )
        }
    }
}

/** Label on the left, value on the right. Used for summaries and confirmations. */
@Composable
fun AppSummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasise: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = if (emphasise) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (emphasise) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Read-only category marker. Not selectable — a chip that does nothing on tap is a lie. */
@Composable
fun AppTagChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, AppRadius.pill)
                .padding(horizontal = Spacing.smd, vertical = Spacing.sm),
    )
}

data class AppFact(
    val icon: ImageVector,
    val label: String,
)
/**
 * One panel of short facts, split into sections. Each section is as wide as its own value and the
 * row scrolls, so a long value is never truncated to fit an equal share.
 */
@Composable
fun AppFactRow(
    facts: List<AppFact>,
    modifier: Modifier = Modifier,
) {
    if (facts.isEmpty()) return
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppRadius.card,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            facts.forEachIndexed { index, fact ->
                if (index > 0) {
                    VerticalDivider(
                        modifier = Modifier.height(AppSize.iconLg),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
                Row(
                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.smd),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = fact.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(AppSize.iconMd),
                    )
                    Text(text = fact.label, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                }
            }
        }
    }
}

/**
 * The shared shell for the bold "concept" family below (ticket stub, receipt, itinerary): a
 * plain white surface — same as every other card in the app — cut with a row of small scalloped
 * notches along the top and bottom edges, the same die-cut language [TicketPerforation] uses at
 * the seam, so the whole card silhouette reads as "torn from a ticket" instead of another rounded
 * rectangle wearing a loud accent border.
 */
@Composable
private fun AppAccentFillCard(
    modifier: Modifier = Modifier,
    padded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    // 16.dp mirrors AppRadius.card's own corner size; a custom Shape can't just delegate to it.
    val shape = remember { AppTicketEdgeShape(cornerRadius = 16.dp) }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp),
    ) {
        Column(
            modifier = if (padded) Modifier.padding(CardMetrics.padding) else Modifier,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            content = content,
        )
    }
}

/**
 * A rounded rect with a row of small semicircular bites punched out of its top and bottom edges —
 * [Card] clips its background and strokes its border to this outline automatically, so both the
 * fill and the hairline border come out looking die-cut with no extra drawing.
 */
private class AppTicketEdgeShape(
    private val cornerRadius: Dp,
    private val notchRadius: Dp = 5.dp,
    private val notchGap: Dp = 16.dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val corner = with(density) { cornerRadius.toPx() }
        val notchR = with(density) { notchRadius.toPx() }
        val gap = with(density) { notchGap.toPx() }

        val base = Path().apply { addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(corner, corner))) }

        val count = (size.width / gap).toInt().coerceAtLeast(1)
        val startX = (size.width - (count - 1) * gap) / 2f
        val notches =
            Path().apply {
                for (i in 0 until count) {
                    val cx = startX + i * gap
                    addOval(Rect(center = Offset(cx, 0f), radius = notchR))
                    addOval(Rect(center = Offset(cx, size.height), radius = notchR))
                }
            }

        val result = Path()
        result.op(base, notches, PathOperation.Difference)
        return Outline.Generic(result)
    }
}

/**
 * A torn-ticket silhouette: a card split by a dashed perforation line, with a small semicircular
 * notch bitten out of each side edge at that seam — this app's one shape that isn't just another
 * rounded rectangle, reserved for "here's what you're buying / here's what you bought" moments
 * so that treatment stays rare enough to still read as the one loud element on the screen. A
 * separate component from [AppTicketCard] on purpose — same family, different silhouette — rather
 * than replacing it, so existing callers of the original keep working unchanged.
 */
@Composable
fun AppTicketStubCard(
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit,
    stub: @Composable ColumnScope.() -> Unit,
) {
    AppAccentFillCard(modifier = modifier, padded = false) {
        Column(
            modifier = Modifier.padding(CardMetrics.padding),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            content = header,
        )
        TicketPerforation()
        Column(
            modifier = Modifier.padding(CardMetrics.padding),
            verticalArrangement = Arrangement.spacedBy(Spacing.smd),
            content = stub,
        )
    }
}

/**
 * A boarding pass: one accent-filled card torn down its right edge by a dashed seam, with a
 * narrow [stub] beside it. The vertical seam (rather than [AppTicketStubCard]'s horizontal one)
 * keeps the whole thing one row tall, so it can sit above a feed without pushing content away.
 */
@Composable
fun AppBoardingPassCard(
    accent: AppAccent,
    stub: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val notchDiameter = 18.dp
    val stubWidth = 74.dp
    val notchColor = MaterialTheme.colorScheme.background
    val seam = Color.White.copy(alpha = 0.45f)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(AppRadius.card)
                .background(accent.brush)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        // IntrinsicSize.Min lets the stub fillMaxHeight() against the taller left column; without
        // it the stub would only be as tall as its own text and could not centre against the card.
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Column(
                modifier = Modifier.weight(1f).padding(CardMetrics.padding),
                verticalArrangement = Arrangement.spacedBy(Spacing.smd),
                content = content,
            )
            Column(
                modifier =
                    Modifier
                        .width(stubWidth)
                        .fillMaxHeight()
                        .background(Color.Black.copy(alpha = 0.16f))
                        .padding(vertical = CardMetrics.padding, horizontal = Spacing.xs),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterVertically),
                content = stub,
            )
        }

        // Drawn over the row so the seam sits exactly on the stub boundary at any card height.
        Canvas(modifier = Modifier.matchParentSize()) {
            val x = size.width - stubWidth.toPx()
            val inset = notchDiameter.toPx() / 2f
            drawLine(
                color = seam,
                start = Offset(x, inset),
                end = Offset(x, size.height - inset),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()), 0f),
            )
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = -(stubWidth - notchDiameter / 2), y = -(notchDiameter / 2))
                    .size(notchDiameter)
                    .clip(CircleShape)
                    .background(notchColor),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = -(stubWidth - notchDiameter / 2), y = notchDiameter / 2)
                    .size(notchDiameter)
                    .clip(CircleShape)
                    .background(notchColor),
        )
    }
}

/**
 * The dashed seam plus its two notches. The notch fill matches the screen background (not the
 * card's own surface) so it reads as a bite punched clean through the card; [AppCard]'s own
 * rounded-rect clip then trims each circle to exactly a semicircle at the card's true edge —
 * no manual clip math needed here.
 */
@Composable
private fun TicketPerforation(modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.outline
    val notchColor = MaterialTheme.colorScheme.background
    val notchDiameter = 20.dp
    Box(modifier = modifier.fillMaxWidth().height(notchDiameter)) {
        Canvas(modifier = Modifier.fillMaxWidth().align(Alignment.Center)) {
            drawLine(
                color = lineColor,
                start = Offset(0f, size.height / 2f),
                end = Offset(size.width, size.height / 2f),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()), 0f),
            )
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = -(notchDiameter / 2))
                    .size(notchDiameter)
                    .clip(CircleShape)
                    .background(notchColor),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = notchDiameter / 2)
                    .size(notchDiameter)
                    .clip(CircleShape)
                    .background(notchColor),
        )
    }
}

/** Used by both [AppTicketCard] (a plain white surface) and [AppTicketStubCard] (a dark-green
 *  fill), so its colors derive from [LocalContentColor] — whatever that card already resolved —
 *  rather than a color pinned to one of the two backgrounds. */
@Composable
fun AppTicketFactRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = LocalContentColor.current,
) {
    val mutedColor = LocalContentColor.current.copy(alpha = 0.7f)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = mutedColor,
            modifier = Modifier.size(AppSize.iconSm),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = mutedColor,
            modifier = Modifier.weight(1f),
        )
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = valueColor)
    }
}

/**
 * The total as the headline, and every fact below it as a plain row with a hairline divider — on
 * the same dark-green [AppAccentFillCard] fill as [AppTicketStubCard] and [AppItineraryTimeline],
 * no shadow, no separate row cards. Reach for it when the total itself should read as the answer
 * to the screen, not one line among several equals.
 */
@Composable
fun AppReceiptSummary(
    totalLabel: String,
    totalValue: String,
    modifier: Modifier = Modifier,
    facts: @Composable ColumnScope.() -> Unit,
) {
    AppAccentFillCard(modifier = modifier) {
        // Read inside the card's own content, not before it — LocalContentColor only becomes the
        // card's white after Material3's Card provides it, which happens around this lambda, not
        // around the call to AppAccentFillCard itself.
        val accent = MaterialTheme.colorScheme.primary
        Text(
            text = totalLabel,
            style = MaterialTheme.typography.bodySmall,
            color = accent.copy(alpha = 0.75f),
            fontWeight = FontWeight.Bold,
        )
        // The one number worth a second glance gets the full-strength accent — everything else in
        // this card is the same green at a lighter weight.
        Text(text = totalValue, style = MaterialTheme.typography.headlineMedium, color = accent)
        HorizontalDivider(color = accent.copy(alpha = 0.25f))
        facts()
    }
}

/** One row for [AppReceiptSummary] — self-bordered (a trailing divider, not a wrapping card),
 *  so any number of them can be dropped into the `facts` slot with no extra spacing to manage.
 *  Colors derive from [LocalContentColor], matching whatever [AppAccentFillCard] resolved. */
@Composable
fun AppReceiptFactRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val onFill = LocalContentColor.current
    val accent = MaterialTheme.colorScheme.primary
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.smd),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(AppSize.iconSm),
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = accent.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(text = value, style = MaterialTheme.typography.titleSmall, color = onFill)
        }
        HorizontalDivider(color = accent.copy(alpha = 0.25f))
    }
}

/**
 * A vertical dot-and-line list on the same [AppAccentFillCard] fill as [AppReceiptSummary] and
 * [AppTicketStubCard]. The connecting line carries the "these belong together" meaning.
 */
@Composable
fun AppItineraryTimeline(
    modifier: Modifier = Modifier,
    items: @Composable ColumnScope.() -> Unit,
) {
    AppAccentFillCard(modifier = modifier, padded = false) {
        Column(modifier = Modifier.padding(CardMetrics.padding).fillMaxWidth(), content = items)
    }
}

/** One entry in a [AppItineraryTimeline]. Set [showConnector] to false on the last entry — it
 *  has nothing below it to connect to — and [emphasise] on whichever one is the answer (a total,
 *  a final status), which renders it in a larger style than the rest. Colors derive from
 *  [LocalContentColor], matching whatever [AppAccentFillCard] resolved. */
@Composable
fun AppItineraryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    showConnector: Boolean = true,
    emphasise: Boolean = false,
) {
    val onFill = LocalContentColor.current
    val accent = MaterialTheme.colorScheme.primary
    Row(modifier = modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.dp)) {
            Box(
                modifier =
                    Modifier
                        .padding(top = 6.dp)
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(if (emphasise) accent else onFill.copy(alpha = 0.5f)),
            )
            if (showConnector) {
                Box(
                    modifier =
                        Modifier
                            .width(2.dp)
                            .height(28.dp)
                            .background(onFill.copy(alpha = 0.25f)),
                )
            }
        }
        Column(
            modifier = Modifier.padding(start = Spacing.smd, bottom = if (showConnector) Spacing.sm else 0.dp),
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = onFill.copy(alpha = 0.75f))
            Text(
                text = value,
                style = if (emphasise) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleSmall,
                color = if (emphasise) accent else onFill,
            )
        }
    }
}
