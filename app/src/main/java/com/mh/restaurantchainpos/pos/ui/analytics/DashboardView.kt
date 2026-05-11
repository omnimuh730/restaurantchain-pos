package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.analytics.charts.AreaCurveChart
import com.mh.restaurantchainpos.pos.ui.analytics.charts.ColumnBar
import com.mh.restaurantchainpos.pos.ui.analytics.charts.ColumnBarChart
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import kotlin.math.roundToInt

@Composable
fun DashboardView(
    period: Period,
    onPeriodChange: (Period) -> Unit,
    range: DateRange?,
    onRangeChange: (DateRange?) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val card = if (isDark) Color(0xFF1F2937) else Color.White
    val border = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val grid = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val tickColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF94A3B8)
    val mutedTrack = if (isDark) Color(0xFF374151) else Color(0xFFCBD5E1)

    val kpiKrw = DashboardData.domesticKpis.getValue(period)
    val kpiUsd = DashboardData.foreignKpis.getValue(period)
    val data = DashboardData.forPeriod(period)

    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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

        // Total Revenue + payment split
        Card(card, border) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Total Revenue", color = text2, fontSize = 12.sp)
                        ChangeChip(kpiKrw.revChange)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        AnalyticsFormat.won(kpiKrw.totalRev.toLong()),
                        color = Blue600,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (kpiUsd.totalRev > 0) {
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("+ ", color = Color(0xFFEF4444), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                AnalyticsFormat.usd(kpiUsd.totalRev),
                                color = Color(0xFFEF4444),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardData.paymentDomestic.forEachIndexed { i, dom ->
                        val foreign = DashboardData.paymentForeign[i]
                        val krw = (kpiKrw.totalRev * dom.pct / 100.0).toLong()
                        val usd = kpiUsd.totalRev * foreign.pct / 100.0
                        PaymentRow(
                            label = dom.method,
                            accent = Color(dom.accent),
                            krw = krw,
                            usd = usd,
                            text1 = text1,
                            text2 = text2,
                        )
                        if (i < DashboardData.paymentDomestic.lastIndex) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(border))
                        }
                    }
                }
            }
        }

        // Three KPI cards
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard(
                "Total Orders",
                kpiKrw.totalOrders,
                kpiKrw.ordChange,
                card,
                border,
                text1,
                text2,
                Modifier.weight(1f),
            )
            KpiCard(
                "Avg Ticket",
                AnalyticsFormat.won(kpiKrw.avgTicket.toLong()),
                kpiKrw.ticketChange,
                card,
                border,
                text1,
                text2,
                Modifier.weight(1f),
            )
            KpiCard(
                "Cancellations",
                kpiKrw.cancels,
                kpiKrw.cancelChange,
                card,
                border,
                text1,
                text2,
                Modifier.weight(1f),
            )
        }

        // Sales Trend area
        Card(card, border) {
            Column(Modifier.padding(16.dp)) {
                Text("Sales Trend", color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "Tap on the chart to view revenue and order count",
                    color = text2,
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(12.dp))
                AreaCurveChart(
                    points = data.map { (it.revenueKrw / 1000f) },
                    labels = data.map { it.label },
                    formatY = { v -> if (v >= 1000f) "${(v / 1000f).roundToInt()}M" else "${v.toInt()}k" },
                    formatTooltip = { i ->
                        val d = data[i]
                        AnalyticsFormat.won(d.revenueKrw)
                    },
                    accent = Blue600,
                    grid = grid,
                    tickColor = tickColor,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                )
            }
        }

        // Peak revenue bar chart
        Card(card, border) {
            val activeKey = data.map { it.revenueKrw.toFloat() }
            val peakIdx = activeKey.withIndex().maxByOrNull { it.value }?.index ?: 0
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Peak revenue at ", color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(data[peakIdx].label, color = Blue600, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Text("Revenue over time", color = text2, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                ColumnBarChart(
                    bars = data.mapIndexed { i, d ->
                        ColumnBar(
                            label = d.label,
                            value = (d.revenueKrw / 1000f),
                            color = if (i == peakIdx) Blue600 else mutedTrack,
                        )
                    },
                    formatY = { v -> if (v >= 1000f) "${(v / 1000f).roundToInt()}M" else "${v.toInt()}k" },
                    formatTooltip = { i -> AnalyticsFormat.won(data[i].revenueKrw) },
                    grid = grid,
                    tickColor = tickColor,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                )
            }
        }
    }
}

@Composable
private fun PaymentRow(
    label: String,
    accent: Color,
    krw: Long,
    usd: Double,
    text1: Color,
    text2: Color,
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(accent))
        Spacer(Modifier.width(8.dp))
        Text(label, color = text2, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            if (krw > 0) {
                Text(AnalyticsFormat.won(krw), color = Blue600, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            if (usd > 0.01) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (krw > 0) Text("+ ", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(AnalyticsFormat.usd(usd), color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            @Suppress("UNUSED_EXPRESSION") text1
        }
    }
}

@Composable
private fun KpiCard(
    label: String,
    value: String,
    change: String,
    card: Color,
    border: Color,
    text1: Color,
    text2: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(card)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(kpiGlyph(label), color = text2, fontSize = 13.sp)
                ChangeChip(change)
            }
            Spacer(Modifier.height(4.dp))
            Text(value, color = text1, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(label, color = text2, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ChangeChip(change: String) {
    val isUp = !change.startsWith("-")
    val color = if (isUp) Color(0xFF22C55E) else Color(0xFFEF4444)
    val arrow = if (isUp) "↗" else "↘"
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(arrow, color = color, fontSize = 11.sp)
        Spacer(Modifier.width(2.dp))
        Text(change, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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

private fun kpiGlyph(label: String): String = when {
    label.startsWith("Total Orders") -> "🛒"
    label.startsWith("Avg") -> "📈"
    label.startsWith("Cancel") -> "✕"
    else -> "•"
}
