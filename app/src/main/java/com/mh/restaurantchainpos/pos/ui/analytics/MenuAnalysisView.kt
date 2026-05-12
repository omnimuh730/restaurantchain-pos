package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.i18n.axisTickLabel
import com.mh.restaurantchainpos.pos.ui.i18n.menuCategoryTitle
import com.mh.restaurantchainpos.pos.ui.i18n.menuItemTitle
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
    val card = if (isDark) Color(0xFF283548) else Color.White
    val border = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val grid = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val mutedTrack = if (isDark) Color(0xFF374151) else Color(0xFFCBD5E1)
    val highlightRowBg = if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.18f) else Color(0xFFEFF6FF)
    val tabActive = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    val mult = MenuAnalysisData.multiplier(period)
    val pool = MenuAnalysisData.items.filter { it.currency == AnalyticsCurrency.Domestic }
    val categoryTotals = pool
        .groupBy { it.categoryKey }
        .map { (catKey, items) ->
            val rev = items.sumOf { it.basePrice * it.baseQty * mult }
            val orders = items.sumOf { (it.baseQty * mult).toInt() }
            Triple(catKey, rev, orders)
        }
        .sortedByDescending { it.second }
    val totalRev = categoryTotals.sumOf { it.second }.coerceAtLeast(1.0)
    val topCategoryKey = categoryTotals.firstOrNull()?.first.orEmpty()
    val topCategoryName = menuCategoryTitle(topCategoryKey)

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
        AnalyticsCard(card, isDark) {
            DateFilterBar(
                period = period,
                onPeriodChange = onPeriodChange,
                range = range,
                onRangeChange = onRangeChange,
                isDark = isDark,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        AnalyticsCard(card, isDark) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.analytics_menu_top_category_loved, topCategoryName),
                    color = text1,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(stringResource(R.string.analytics_menu_domestic_breakdown), color = text2, fontSize = 12.sp)
                Spacer(Modifier.height(16.dp))

                val slices = categoryTotals.mapIndexed { i, (_, rev, _) ->
                    DonutSlice(rev.toFloat(), Color(MenuAnalysisData.categoryColors[i % MenuAnalysisData.categoryColors.size]))
                }
                val legend: @Composable () -> Unit = {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        categoryTotals.forEachIndexed { i, (catKey, rev, _) ->
                            val accent = Color(MenuAnalysisData.categoryColors[i % MenuAnalysisData.categoryColors.size])
                            CategoryRow(
                                color = accent,
                                name = menuCategoryTitle(catKey),
                                pct = ((rev / totalRev) * 100).toFloat(),
                                revenue = rev.toLong(),
                                isSelected = selectedCategory == catKey,
                                onClick = {
                                    selectedCategory = if (selectedCategory == catKey) null else catKey
                                },
                                text1 = text1,
                                text2 = text2,
                                highlightRowBg = highlightRowBg,
                            )
                        }
                    }
                }

                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    if (maxWidth < 520.dp) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            DonutChart(slices = slices, diameter = 170.dp, thickness = 28.dp)
                            legend()
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            DonutChart(slices = slices, diameter = 160.dp, thickness = 28.dp)
                            Box(Modifier.weight(1f)) { legend() }
                        }
                    }
                }
            }
        }

        AnalyticsCard(card, isDark) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (selectedCategory != null) {
                                stringResource(
                                    R.string.analytics_menu_items_for_category,
                                    menuCategoryTitle(categoryTotals.first { it.first == selectedCategory }.first),
                                )
                            } else {
                                stringResource(R.string.analytics_menu_all_items)
                            },
                            color = text1,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (selectedCategory != null) {
                            Text(
                                stringResource(R.string.analytics_menu_show_all),
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
                    Text(stringResource(R.string.analytics_col_item), color = text2, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text(stringResource(R.string.analytics_col_category), color = text2, fontSize = 11.sp, modifier = Modifier.width(96.dp))
                    Text(stringResource(R.string.analytics_col_sold), color = text2, fontSize = 11.sp, modifier = Modifier.width(48.dp), textAlign = TextAlign.End)
                    Text(stringResource(R.string.analytics_col_revenue), color = text2, fontSize = 11.sp, modifier = Modifier.width(96.dp), textAlign = TextAlign.End)
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
                        Text(menuItemTitle(item.nameKey), color = text1, fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(menuCategoryTitle(item.categoryKey), color = text2, fontSize = 12.sp, modifier = Modifier.width(96.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(AnalyticsFormat.int(m.first), color = text1, fontSize = 13.sp, modifier = Modifier.width(48.dp), textAlign = TextAlign.End)
                        Text(AnalyticsFormat.won(m.second), color = text1, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(96.dp), textAlign = TextAlign.End)
                    }
                }
            }
        }

        if (bestSeller.weeklyBest != null) {
            val weekTrendLabels = buildList {
                for (k in MenuAnalysisData.weekAxis) add(axisTickLabel(k))
            }
            AnalyticsCard(card, isDark) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(menuItemTitle(bestSeller.nameKey), color = Blue600, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(stringResource(R.string.analytics_menu_weekly_trend_suffix), color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text(stringResource(R.string.analytics_menu_top_seller_sub), color = text2, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    ColumnBarChart(
                        bars = MenuAnalysisData.weekAxis.mapIndexed { i, day ->
                            val v = bestSellerScaled.getOrNull(i)?.toFloat() ?: 0f
                            ColumnBar(weekTrendLabels[i], v, if (v.toInt() == peak) Blue600 else mutedTrack)
                        },
                        formatY = { v -> v.toInt().toString() },
                        formatTooltip = { i -> "${weekTrendLabels[i]} — ${bestSellerScaled.getOrNull(i) ?: 0}" },
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
        Text(name, color = text1, fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text("%.1f%%".format(pct), color = text2, fontSize = 12.sp, modifier = Modifier.width(54.dp), textAlign = TextAlign.End)
        Spacer(Modifier.width(8.dp))
        Text(AnalyticsFormat.won(revenue), color = text1, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(104.dp), textAlign = TextAlign.End)
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
        SortPill(stringResource(R.string.analytics_sort_revenue), sortBy == SortBy.Revenue, text2) { onChange(SortBy.Revenue) }
        SortPill(stringResource(R.string.analytics_sort_volume), sortBy == SortBy.Volume, text2) { onChange(SortBy.Volume) }
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

