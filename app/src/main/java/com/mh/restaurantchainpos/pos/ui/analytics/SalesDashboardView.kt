package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.analytics.charts.AreaCurveChart
import com.mh.restaurantchainpos.pos.ui.analytics.charts.ColumnBar
import com.mh.restaurantchainpos.pos.ui.analytics.charts.ColumnBarChart
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import kotlin.math.roundToInt

@Composable
fun SalesDashboardView(
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

    val kpiKrw = SalesDashboardData.domesticKpis.getValue(period)
    val kpiUsd = SalesDashboardData.foreignKpis.getValue(period)
    val data = SalesDashboardData.forPeriod(period)

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
            BoxWithConstraints(Modifier.fillMaxWidth().padding(16.dp)) {
                val compact = maxWidth < 360.dp
                val revenueSummary: @Composable () -> Unit = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Blue600.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Outlined.Payments, contentDescription = null, tint = Blue600, modifier = Modifier.size(16.dp))
                            }
                            Text("Total Revenue", color = text2, fontSize = 12.sp)
                            ChangeChip(kpiKrw.revChange)
                        }
                        Spacer(Modifier.height(8.dp))
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
                }
                val paymentSummary: @Composable () -> Unit = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SalesDashboardData.paymentDomestic.forEachIndexed { i, dom ->
                            val foreign = SalesDashboardData.paymentForeign[i]
                            val krw = (kpiKrw.totalRev * dom.pct / 100.0).toLong()
                            val usd = kpiUsd.totalRev * foreign.pct / 100.0
                            PaymentRow(
                                label = dom.method,
                                icon = if (dom.method == "Credit") Icons.Outlined.CreditCard else Icons.Outlined.Payments,
                                accent = Color(dom.accent),
                                krw = krw,
                                usd = usd,
                                text2 = text2,
                            )
                            if (i < SalesDashboardData.paymentDomestic.lastIndex) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(border))
                            }
                        }
                    }
                }
                if (compact) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        revenueSummary()
                        paymentSummary()
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Box(Modifier.weight(1f)) { revenueSummary() }
                        Box(Modifier.weight(1f)) { paymentSummary() }
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
                Icons.Outlined.ShoppingCart,
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
                Icons.AutoMirrored.Outlined.TrendingUp,
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
                Icons.Outlined.Cancel,
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
    icon: ImageVector,
    accent: Color,
    krw: Long,
    usd: Double,
    text2: Color,
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
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
        }
    }
}

@Composable
private fun KpiCard(
    label: String,
    value: String,
    change: String,
    icon: ImageVector,
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
                Icon(icon, contentDescription = null, tint = text2, modifier = Modifier.size(16.dp))
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
