package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryView(
    period: Period,
    onPeriodChange: (Period) -> Unit,
    range: DateRange?,
    onRangeChange: (DateRange?) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    var tab by remember { mutableStateOf<HistoryKind?>(null) } // null = All
    var search by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<String?>(null) }

    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val muted = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
    val card = if (isDark) Color(0xFF1F2937) else Color.White
    val border = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val chip = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
    val activeTab = if (isDark) Color(0xFF1E293B) else Color(0xFF1E293B)

    val now = System.currentTimeMillis()
    val (start, end) = when {
        range != null -> range.startMs to range.endMs
        period == Period.Today -> (now - HistoryData.DAY) to now
        period == Period.Week -> (now - 7 * HistoryData.DAY) to now
        period == Period.Month -> (now - 30 * HistoryData.DAY) to now
        period == Period.Quarter -> (now - 90 * HistoryData.DAY) to now
        else -> 0L to Long.MAX_VALUE
    }

    val q = search.trim().lowercase()
    val filtered = HistoryData.events
        .filter { it.timestampMs in start..end }
        .filter { tab == null || it.kind == tab }
        .filter { e ->
            if (q.isEmpty()) return@filter true
            val text = listOfNotNull(
                e.id, e.guest, e.tableLabel, e.tableNum.toString(), e.payment, e.notes,
            ).joinToString(" ") + " " + e.items.joinToString(" ") { it.name }
            text.lowercase().contains(q)
        }
        .sortedByDescending { it.timestampMs }

    val totalUsd = filtered.filter { it.status != HistoryStatus.Refunded }.sumOf { it.amountUsd }
    val totalKrw = filtered.filter { it.status != HistoryStatus.Refunded }.sumOf { it.amountKrw }
    val noShows = filtered.count { it.kind == HistoryKind.NoShow }

    val counts = mutableMapOf<HistoryKind, Int>()
    HistoryKind.entries.forEach { counts[it] = 0 }
    HistoryData.events.forEach { counts[it.kind] = (counts[it.kind] ?: 0) + 1 }

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(card, border) {
            DateFilterBar(
                period = period,
                onPeriodChange = onPeriodChange,
                range = range,
                onRangeChange = onRangeChange,
                isDark = isDark,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        // KPI strip
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HistKpi("Events", filtered.size.toString(), null, card, border, text1, text2, Modifier.weight(1f))
            val rev = if (totalKrw > 0) AnalyticsFormat.won(totalKrw) else AnalyticsFormat.usd(totalUsd)
            HistKpi("Revenue", rev, null, card, border, text1, text2, Modifier.weight(1f))
            HistKpi("No-Shows", noShows.toString(), null, card, border, text1, text2, Modifier.weight(1f))
        }

        // List card
        Card(card, border) {
            Column(Modifier.padding(12.dp)) {
                // Search box
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(chip)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("🔍", color = muted, fontSize = 12.sp)
                    Spacer(Modifier.width(6.dp))
                    Box(Modifier.weight(1f)) {
                        if (search.isEmpty()) {
                            Text(
                                "Search by order ID, menu item, payer name, table…",
                                color = muted,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        BasicTextField(
                            value = search,
                            onValueChange = { search = it },
                            singleLine = true,
                            cursorBrush = SolidColor(text1),
                            textStyle = TextStyle(color = text1, fontSize = 13.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        )
                    }
                    if (search.isNotEmpty()) {
                        Text(
                            "✕",
                            color = muted,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { search = "" },
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Tabs row
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TabPill(
                        label = "All",
                        count = HistoryData.events.size,
                        active = tab == null,
                        activeColor = activeTab,
                        text2 = text2,
                        chip = chip,
                    ) { tab = null }
                    HistoryKind.entries.forEach { kind ->
                        TabPill(
                            label = kind.label,
                            count = counts[kind] ?: 0,
                            active = tab == kind,
                            activeColor = activeTab,
                            text2 = text2,
                            chip = chip,
                        ) { tab = kind }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text("${filtered.size} results", color = text2, fontSize = 11.sp)

                Spacer(Modifier.height(8.dp))

                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No matching events", color = muted, fontSize = 13.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filtered.forEach { e ->
                            ReceiptCard(
                                event = e,
                                isDark = isDark,
                                onOpen = { selectedId = e.id },
                                card = card, border = border,
                                text1 = text1, text2 = text2, muted = muted,
                            )
                        }
                    }
                }
            }
        }
    }

    // Mobile detail bottom sheet
    val sel = filtered.find { it.id == selectedId } ?: HistoryData.events.find { it.id == selectedId }
    if (sel != null) {
        HistoryDetailSheet(event = sel, isDark = isDark, onClose = { selectedId = null })
    }
}

@Composable
private fun HistKpi(
    label: String,
    value: String,
    sub: String?,
    card: Color,
    border: Color,
    text1: Color,
    text2: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(card)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Column {
            Text(label, color = text2, fontSize = 11.sp)
            Spacer(Modifier.height(2.dp))
            Text(value, color = text1, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            if (sub != null) {
                Text(sub, color = text2, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun TabPill(
    label: String,
    count: Int,
    active: Boolean,
    activeColor: Color,
    text2: Color,
    chip: Color,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) activeColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = if (active) Color.White else text2, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(6.dp))
        Box(
            Modifier
                .clip(CircleShape)
                .background(if (active) Color.White.copy(alpha = 0.18f) else chip)
                .padding(horizontal = 6.dp, vertical = 1.dp),
        ) {
            Text(count.toString(), color = if (active) Color.White else text2, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ReceiptCard(
    event: HistoryEvent,
    isDark: Boolean,
    onOpen: () -> Unit,
    card: Color,
    border: Color,
    text1: Color,
    text2: Color,
    muted: Color,
) {
    val (icon, accent) = kindIconAccent(event.kind, isDark)
    val statusBg = statusBadgeBg(event.status, isDark)
    val statusFg = statusBadgeFg(event.status, isDark)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(card)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable(onClick = onOpen),
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) { Text(icon, color = accent, fontSize = 14.sp) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(event.guest, color = text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) { Text(event.status.label, color = statusFg, fontSize = 10.sp, fontWeight = FontWeight.Medium) }
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("# ${event.id}", color = text2, fontSize = 11.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("⌖ ${event.tableLabel}", color = text2, fontSize = 11.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("⌚ ${relDay(event.timestampMs)} · ${fmtTime(event.timestampMs)}", color = text2, fontSize = 11.sp)
                }
                if (!event.payment.isNullOrEmpty()) {
                    Text("⌥ ${event.payment}", color = text2, fontSize = 11.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                if (event.amountKrw > 0) {
                    Text(AnalyticsFormat.won(event.amountKrw), color = Blue600, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                if (event.amountUsd > 0.01) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (event.amountKrw > 0) Text("+ ", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(AnalyticsFormat.usd(event.amountUsd), color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (event.items.isNotEmpty()) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(border))
            Column(Modifier.padding(12.dp)) {
                event.items.forEach { it ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(it.name, color = text1, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(it.qty.toString(), color = text1, fontSize = 12.sp, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
                        Text(formatLine(it.price, it.currency), color = text2, fontSize = 12.sp, modifier = Modifier.width(64.dp), textAlign = TextAlign.End)
                        Text(formatLine(it.qty * it.price, it.currency), color = text1, fontSize = 12.sp, modifier = Modifier.width(72.dp), textAlign = TextAlign.End)
                    }
                }
            }
        } else {
            Text(
                when (event.kind) {
                    HistoryKind.NoShow -> "Released after grace period."
                    HistoryKind.Reservation -> "No receipt — reservation only."
                    else -> event.notes ?: "Receipt summary unavailable."
                },
                color = muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun HistoryDetailSheet(event: HistoryEvent, isDark: Boolean, onClose: () -> Unit) {
    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val card = if (isDark) Color(0xFF1F2937) else Color.White
    val border = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val tile = if (isDark) Color(0xFF111827) else Color(0xFFF8FAFC)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose,
                    ),
            )
        }
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight * 0.75f)
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(card),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) { Text("←", color = text1, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(8.dp))
                    Text("Event details", color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(border))
                val (icon, accent) = kindIconAccent(event.kind, isDark)
                val statusBg = statusBadgeBg(event.status, isDark)
                val statusFg = statusBadgeFg(event.status, isDark)
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) { Text(icon, color = accent, fontSize = 16.sp) }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(event.guest, color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${relDay(event.timestampMs)} · ${fmtTime(event.timestampMs)}",
                            color = text2,
                            fontSize = 12.sp,
                        )
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(event.status.label, color = statusFg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailTile("Table", event.tableLabel, "📍", tile, text1, text2, Modifier.weight(1f))
                        DetailTile("Party", "${event.partySize ?: "—"} guests", "👥", tile, text1, text2, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailTile("Paid at", event.paidAt ?: "—", "💳", tile, text1, text2, Modifier.weight(1f))
                        DetailTile("Method", event.payment ?: "—", "💵", tile, text1, text2, Modifier.weight(1f))
                    }
                    DetailTile("# ID", event.id, "#", tile, text1, text2, Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(12.dp))
                if (event.items.isNotEmpty()) {
                    Text("Receipt", color = text2, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(tile)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text("Item", color = text2, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text("Qty", color = text2, fontSize = 11.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
                        Text("Price", color = text2, fontSize = 11.sp, modifier = Modifier.width(64.dp), textAlign = TextAlign.End)
                        Text("Total", color = text2, fontSize = 11.sp, modifier = Modifier.width(72.dp), textAlign = TextAlign.End)
                    }
                    event.items.forEach { it ->
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            Text(it.name, color = text1, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text(it.qty.toString(), color = text1, fontSize = 13.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
                            Text(formatLine(it.price, it.currency), color = text1, fontSize = 13.sp, modifier = Modifier.width(64.dp), textAlign = TextAlign.End)
                            Text(formatLine(it.qty * it.price, it.currency), color = text1, fontSize = 13.sp, modifier = Modifier.width(72.dp), textAlign = TextAlign.End)
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(border))
                    val totalUsd = event.items.filter { it.currency == AnalyticsCurrency.Foreign }.sumOf { it.qty * it.price }
                    val totalKrw = event.items.filter { it.currency == AnalyticsCurrency.Domestic }.sumOf { (it.qty * it.price).toLong() }
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Total", color = text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            if (totalKrw > 0) {
                                Text(AnalyticsFormat.won(totalKrw), color = Blue600, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            if (totalUsd > 0.01) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (totalKrw > 0) Text("+ ", color = Color(0xFFEF4444), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(AnalyticsFormat.usd(totalUsd), color = Color(0xFFEF4444), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
                if (!event.notes.isNullOrEmpty()) {
                    Box(
                        Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tile)
                            .padding(12.dp),
                    ) {
                        Column {
                            Text("Notes", color = text2, fontSize = 11.sp)
                            Text(event.notes, color = text1, fontSize = 13.sp)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DetailTile(
    label: String,
    value: String,
    icon: String,
    tile: Color,
    text1: Color,
    text2: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tile)
            .padding(10.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, color = text2, fontSize = 11.sp)
                Spacer(Modifier.width(4.dp))
                Text(label, color = text2, fontSize = 11.sp)
            }
            Spacer(Modifier.height(2.dp))
            Text(value, color = text1, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun Card(card: Color, border: Color, content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(card)
            .border(1.dp, border, RoundedCornerShape(14.dp)),
    ) { content() }
}

private fun kindIconAccent(kind: HistoryKind, isDark: Boolean): Pair<String, Color> = when (kind) {
    HistoryKind.Order -> "🧾" to (if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB))
    HistoryKind.Reservation -> "📅" to (if (isDark) Color(0xFF34D399) else Color(0xFF059669))
    HistoryKind.Payment -> "💳" to (if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED))
    HistoryKind.NoShow -> "✕" to (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
    HistoryKind.WalkIn -> "👤" to (if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7))
}

private fun statusBadgeBg(status: HistoryStatus, isDark: Boolean): Color = when (status) {
    HistoryStatus.Completed -> if (isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5)
    HistoryStatus.Paid -> if (isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)
    HistoryStatus.NoShow -> if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    HistoryStatus.Refunded -> if (isDark) Color(0xFF78350F) else Color(0xFFFEF3C7)
}

private fun statusBadgeFg(status: HistoryStatus, isDark: Boolean): Color = when (status) {
    HistoryStatus.Completed -> if (isDark) Color(0xFF6EE7B7) else Color(0xFF047857)
    HistoryStatus.Paid -> if (isDark) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
    HistoryStatus.NoShow -> if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    HistoryStatus.Refunded -> if (isDark) Color(0xFFFCD34D) else Color(0xFFB45309)
}

private fun formatLine(value: Double, currency: AnalyticsCurrency): String =
    when (currency) {
        AnalyticsCurrency.Domestic -> AnalyticsFormat.won(value.toLong())
        AnalyticsCurrency.Foreign -> AnalyticsFormat.usd(value)
    }

private val timeFmt = SimpleDateFormat("hh:mm a", Locale.US)
private fun fmtTime(ms: Long): String = timeFmt.format(Date(ms))
private fun relDay(ms: Long): String {
    val now = System.currentTimeMillis()
    val days = ((now - ms) / HistoryData.DAY).toInt()
    return when {
        days <= 0 -> "Today"
        days == 1 -> "Yesterday"
        else -> "${days}d ago"
    }
}
