package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.analytics.charts.ColumnBar
import com.mh.restaurantchainpos.pos.ui.analytics.charts.ColumnBarChart
import com.mh.restaurantchainpos.pos.ui.analytics.charts.DonutChart
import com.mh.restaurantchainpos.pos.ui.analytics.charts.DonutSlice
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

private enum class SortBy { Revenue, Volume }

@Composable
fun MenuAnalysisView(
    period: Period,
    onPeriodChange: (Period) -> Unit,
    range: DateRange?,
    onRangeChange: (DateRange?) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    var sortBy by remember { mutableStateOf(SortBy.Revenue) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val card = if (isDark) Color(0xFF1F2937) else Color.White
    val border = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val grid = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val mutedTrack = if (isDark) Color(0xFF374151) else Color(0xFFCBD5E1)
    val rowBg = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
    val highlightRowBg = if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.18f) else Color(0xFFEFF6FF)
    val tabActive = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    val mult = MenuAnalysisData.multiplier(period)
    // Use the domestic pool to match the React demo's primary view (KRW).
    val pool = MenuAnalysisData.items.filter { it.currency == AnalyticsCurrency.Domestic }
    val byCategory = pool.groupBy { it.categoryKey to it.category }
    val categoryTotals = byCategory.map { (key, items) ->
        val rev = items.sumOf { (it.basePrice * it.baseQty * mult) }
        val orders = items.sumOf { (it.baseQty * mult).toInt() }
        Triple(key, rev, orders)
    }.sortedByDescending { it.second }
    val totalRev = categoryTotals.sumOf { it.second }.coerceAtLeast(1.0)

    val topCategoryName = categoryTotals.firstOrNull()?.first?.second ?: ""

    val filteredItems = (selectedCategory?.let { sel -> pool.filter { it.categoryKey == sel } } ?: pool)
        .map {
            val qty = (it.baseQty * mult).toInt()
            val rev = (it.basePrice * it.baseQty * mult).toLong()
            it to (qty to rev)
        }
        .sortedByDescending { (_, m) -> if (sortBy == SortBy.Revenue) m.second.toDouble() else m.first.toDouble() }

    val bestSeller = pool.firstOrNull { it.weeklyBest != null } ?: pool.first()
    val bestSellerScaled = bestSeller.weeklyBest?.map { (it * mult).toInt() } ?: emptyList()
    val peak = bestSellerScaled.maxOrNull() ?: 0

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

        Card(card, border) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("The ", color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(topCategoryName, color = Blue600, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(" category is loved the most in ₩!", color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Text("domestic (₩) — category breakdown", color = text2, fontSize = 12.sp)
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    val slices = categoryTotals.mapIndexed { i, (_, rev, _) ->
                        DonutSlice(rev.toFloat(), Color(MenuAnalysisData.categoryColors[i % MenuAnalysisData.categoryColors.size]))
                    }
                    DonutChart(slices = slices, diameter = 160.dp, thickness = 28.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        categoryTotals.forEachIndexed { i, (key, rev, _) ->
                            val accent = Color(MenuAnalysisData.categoryColors[i % MenuAnalysisData.categoryColors.size])
                            CategoryRow(
                                color = accent,
                                name = key.second,
                                pct = ((rev / totalRev) * 100).toFloat(),
                                revenue = rev.toLong(),
                                isSelected = selectedCategory == key.first,
                                onClick = {
                                    selectedCategory = if (selectedCategory == key.first) null else key.first
                                },
                                text1 = text1,
                                text2 = text2,
                                rowBg = rowBg,
                                highlightRowBg = highlightRowBg,
                            )
                        }
                    }
                }
            }
        }

        // All items table
        Card(card, border) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (selectedCategory != null) "Items — ${categoryTotals.first { it.first.first == selectedCategory }.first.second}" else "All domestic items",
                            color = text1,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (selectedCategory != null) {
                            Text(
                                "Show all categories",
                                color = Blue600,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { selectedCategory = null },
                            )
                        }
                    }
                    SortToggle(sortBy = sortBy, onChange = { sortBy = it }, tabActive = tabActive, text2 = text2)
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                ) {
                    Text("Item", color = text2, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text("Category", color = text2, fontSize = 11.sp, modifier = Modifier.width(96.dp))
                    Text("Sold", color = text2, fontSize = 11.sp, modifier = Modifier.width(48.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    Text("Revenue", color = text2, fontSize = 11.sp, modifier = Modifier.width(96.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(border))
                filteredItems.forEachIndexed { index, (item, m) ->
                    val isTop = index == 0
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isTop) highlightRowBg else Color.Transparent)
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTop) Blue600 else Color(0xFF3B82F6).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "${index + 1}",
                                color = if (isTop) Color.White else Blue600,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(item.name, color = text1, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text(item.category, color = text2, fontSize = 12.sp, modifier = Modifier.width(96.dp))
                        Text(AnalyticsFormat.int(m.first), color = text1, fontSize = 13.sp, modifier = Modifier.width(48.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                        Text(AnalyticsFormat.won(m.second), color = text1, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(96.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    }
                }
            }
        }

        if (bestSeller.weeklyBest != null) {
            Card(card, border) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(bestSeller.name, color = Blue600, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(" — Weekly sales trend", color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text("Top domestic (₩) seller this week", color = text2, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    ColumnBarChart(
                        bars = MenuAnalysisData.weekAxis.mapIndexed { i, day ->
                            val v = bestSellerScaled.getOrNull(i)?.toFloat() ?: 0f
                            ColumnBar(day, v, if (v.toInt() == peak) Blue600 else mutedTrack)
                        },
                        formatY = { v -> v.toInt().toString() },
                        formatTooltip = { i -> "${MenuAnalysisData.weekAxis[i]} · ${bestSellerScaled.getOrNull(i) ?: 0}" },
                        grid = grid,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    color: Color,
    name: String,
    pct: Float,
    revenue: Long,
    isSelected: Boolean,
    onClick: () -> Unit,
    text1: Color,
    text2: Color,
    rowBg: Color,
    highlightRowBg: Color,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) highlightRowBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(name, color = text1, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text("%.1f%%".format(pct), color = text2, fontSize = 12.sp)
        Spacer(Modifier.width(12.dp))
        Text(AnalyticsFormat.won(revenue), color = text1, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        @Suppress("UNUSED_EXPRESSION") rowBg
    }
}

@Composable
private fun SortToggle(
    sortBy: SortBy,
    onChange: (SortBy) -> Unit,
    tabActive: Color,
    text2: Color,
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tabActive.copy(alpha = 0.4f))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        SortPill("Revenue", sortBy == SortBy.Revenue, text2) { onChange(SortBy.Revenue) }
        SortPill("Volume", sortBy == SortBy.Volume, text2) { onChange(SortBy.Volume) }
    }
}

@Composable
private fun SortPill(label: String, active: Boolean, text2: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (active) Blue600 else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(label, color = if (active) Color.White else text2, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
