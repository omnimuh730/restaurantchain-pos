package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.i18n.historySearchHaystack
import com.mh.restaurantchainpos.pos.ui.i18n.stringTitle

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

    val ctx = LocalContext.current
    val q = search.trim().lowercase()
    val filtered = HistoryData.events
        .filter { it.timestampMs in start..end }
        .filter { tab == null || it.kind == tab }
        .filter { e ->
            if (q.isEmpty()) return@filter true
            historySearchHaystack(ctx, e).lowercase().contains(q)
        }
        .sortedByDescending { it.timestampMs }

    val totalUsd = filtered.filter { it.status != HistoryStatus.Refunded }.sumOf { it.amountUsd }
    val totalKrw = filtered.filter { it.status != HistoryStatus.Refunded }.sumOf { it.amountKrw }
    val noShows = filtered.count { it.kind == HistoryKind.NoShow }

    val counts = mutableMapOf<HistoryKind, Int>()
    HistoryKind.entries.forEach { counts[it] = 0 }
    HistoryData.events.forEach { counts[it.kind] = (counts[it.kind] ?: 0) + 1 }

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AnalyticsCard(card) {
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
            HistKpi(stringResource(R.string.analytics_hist_kpi_events), filtered.size.toString(), null, card, border, text1, text2, Modifier.weight(1f))
            val rev = if (totalKrw > 0) AnalyticsFormat.won(totalKrw) else AnalyticsFormat.usd(totalUsd)
            HistKpi(stringResource(R.string.analytics_hist_kpi_revenue), rev, null, card, border, text1, text2, Modifier.weight(1f))
            HistKpi(stringResource(R.string.analytics_hist_kpi_no_shows), noShows.toString(), null, card, border, text1, text2, Modifier.weight(1f))
        }

        // List card
        AnalyticsCard(card) {
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
                                stringResource(R.string.analytics_hist_search_hint),
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
                        label = stringResource(R.string.analytics_hist_tab_all),
                        count = HistoryData.events.size,
                        active = tab == null,
                        activeColor = activeTab,
                        text2 = text2,
                        chip = chip,
                    ) { tab = null }
                    HistoryKind.entries.forEach { kind ->
                        TabPill(
                            label = kind.stringTitle(),
                            count = counts[kind] ?: 0,
                            active = tab == kind,
                            activeColor = activeTab,
                            text2 = text2,
                            chip = chip,
                        ) { tab = kind }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(stringResource(R.string.analytics_hist_results, filtered.size), color = text2, fontSize = 11.sp)

                Spacer(Modifier.height(8.dp))

                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.analytics_hist_empty), color = muted, fontSize = 13.sp)
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
