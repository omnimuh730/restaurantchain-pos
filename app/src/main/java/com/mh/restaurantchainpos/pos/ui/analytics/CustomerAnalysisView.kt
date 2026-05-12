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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.i18n.customerSegmentTitle
import com.mh.restaurantchainpos.pos.ui.i18n.partySizeRowLabel
import com.mh.restaurantchainpos.pos.ui.i18n.visitFrequencyLabel
import com.mh.restaurantchainpos.pos.ui.analytics.charts.AreaCurveChart
import com.mh.restaurantchainpos.pos.ui.analytics.charts.DonutChart
import com.mh.restaurantchainpos.pos.ui.analytics.charts.DonutSlice
import com.mh.restaurantchainpos.pos.ui.analytics.charts.HorizontalBar
import com.mh.restaurantchainpos.pos.ui.analytics.charts.HorizontalBarChart
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

@Composable
fun CustomerAnalysisView(
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
    val mutedTrack = if (isDark) Color(0xFF374151) else Color(0xFFCBD5E1)
    val freqAccent = if (isDark) Color(0xFF60A5FA) else Color(0xFF3B82F6)

    val kpis = CustomerAnalysisData.kpiByPeriod.getValue(period)
    val segment = CustomerAnalysisData.segmentByPeriod.getValue(period)
    val visitFreq = CustomerAnalysisData.visitFreqByPeriod.getValue(period)
    val peakHour = CustomerAnalysisData.hourlyTraffic.maxBy { it.customers }
    val returningPct = segment.first { it.nameKey == "returning" }.value

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

        // 2x2 KPI grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard(stringResource(R.string.analytics_customer_total_customers), kpis.totalCust, kpis.custChange, "👥", card, border, text1, text2, Modifier.weight(1f))
                KpiCard(stringResource(R.string.analytics_customer_new), kpis.newCust, kpis.newChange, "👤+", card, border, text1, text2, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard(stringResource(R.string.analytics_customer_returning_rate), kpis.returning, kpis.retChange, "↻", card, border, text1, text2, Modifier.weight(1f))
                KpiCard(stringResource(R.string.analytics_customer_satisfaction), kpis.satisfaction, kpis.satChange, "★", card, border, text1, text2, Modifier.weight(1f))
            }
        }

        // Traffic by hour
        AnalyticsCard(card) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.analytics_customer_visitors_prefix), color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("${peakHour.hour}:00", color = Blue600, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(stringResource(R.string.analytics_customer_traffic_sub), color = text2, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                AreaCurveChart(
                    points = CustomerAnalysisData.hourlyTraffic.map { it.customers.toFloat() },
                    labels = CustomerAnalysisData.hourlyTraffic.map { it.hour },
                    formatY = { it.toInt().toString() },
                    formatTooltip = { i ->
                        val r = CustomerAnalysisData.hourlyTraffic[i]
                        "${r.hour}:00 · ${r.customers}"
                    },
                    accent = Blue600,
                    grid = grid,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                )
            }
        }

        // Returning vs New
        AnalyticsCard(card) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$returningPct%", color = Blue600, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.analytics_customer_returning_suffix), color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(stringResource(R.string.analytics_customer_new_vs_returning), color = text2, fontSize = 12.sp)
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    DonutChart(
                        slices = segment.map { DonutSlice(it.value.toFloat(), Color(it.accent)) },
                        diameter = 140.dp,
                        thickness = 24.dp,
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        segment.forEach { seg ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(10.dp).clip(CircleShape).background(Color(seg.accent)))
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(customerSegmentTitle(seg.nameKey), color = text2, fontSize = 13.sp)
                                    Text("${seg.value}%", color = text1, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Visit frequency
        AnalyticsCard(card) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.analytics_customer_visit_frequency), color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                HorizontalBarChart(
                    bars = visitFreq.map { HorizontalBar(visitFrequencyLabel(it.visitsKey), it.customers.toFloat(), freqAccent) },
                    track = mutedTrack.copy(alpha = 0.5f),
                    text = text1,
                    label = text2,
                    labelWidth = 56.dp,
                )
            }
        }

        // Party size
        AnalyticsCard(card) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.analytics_customer_groups_prefix), color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.analytics_customer_groups_highlight), color = Blue600, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.analytics_customer_groups_suffix), color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(stringResource(R.string.analytics_customer_party_sub), color = text2, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CustomerAnalysisData.partySize.forEach { p ->
                        Column {
                            Row {
                                Text(partySizeRowLabel(p.sizeKey), color = text1, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Text("${p.pct}%", color = text1, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(mutedTrack.copy(alpha = 0.5f)),
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth(p.pct / 100f * 2.5f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (p.pct == 38) Blue600 else mutedTrack),
                                )
                            }
                        }
                    }
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
    glyph: String,
    card: Color,
    border: Color,
    text1: Color,
    text2: Color,
    modifier: Modifier = Modifier,
) {
    val isUp = !change.startsWith("-")
    val color = if (isUp) Color(0xFF22C55E) else Color(0xFFEF4444)
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(card)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(glyph, color = text2, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isUp) "↗" else "↘", color = color, fontSize = 11.sp)
                    Spacer(Modifier.width(2.dp))
                    Text(change, color = color, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(value, color = text1, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(label, color = text2, fontSize = 11.sp)
        }
    }
}

