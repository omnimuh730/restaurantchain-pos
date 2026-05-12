package com.mh.restaurantchainpos.pos.ui.analytics.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class ColumnBar(val label: String, val value: Float, val color: Color)

/**
 * Vertical-bar chart with rounded tops, dashed gridlines and a tap tooltip.
 * Mirrors the React `<BarChart>` (recharts) used in SalesDashboardView /
 * MenuAnalysisView.
 */
@Composable
fun ColumnBarChart(
    bars: List<ColumnBar>,
    formatY: (Float) -> String,
    formatTooltip: (Int) -> String,
    modifier: Modifier = Modifier,
    grid: Color = Color(0xFFE2E8F0),
    tickColor: Color = Color(0xFF94A3B8),
    tooltipBg: Color = Color(0xFF2563EB),
    tooltipFg: Color = Color.White,
) {
    if (bars.isEmpty()) return
    val density = LocalDensity.current
    var canvasW by remember { mutableIntStateOf(0) }
    var canvasH by remember { mutableIntStateOf(0) }
    var selected by remember(bars) { mutableIntStateOf(-1) }

    val maxV = bars.maxOf { it.value }.let { if (it <= 0f) 1f else it }
    val niceMax = niceCeiling(maxV)
    val tickCount = 4
    val ticks = (0..tickCount).map { it.toFloat() / tickCount * niceMax }

    Box(modifier) {
        Canvas(
            Modifier
                .fillMaxSize()
                .pointerInput(bars) {
                    detectTapGestures { offset ->
                        if (canvasW <= 0) return@detectTapGestures
                        val left = with(density) { 36.dp.toPx() }
                        val right = with(density) { 8.dp.toPx() }
                        val plotW = canvasW.toFloat() - left - right
                        val gap = plotW / bars.size
                        val idx = ((offset.x - left) / gap).toInt().coerceIn(0, bars.size - 1)
                        selected = if (selected == idx) -1 else idx
                    }
                },
        ) {
            canvasW = size.width.toInt()
            canvasH = size.height.toInt()
            val left = with(density) { 36.dp.toPx() }
            val right = with(density) { 8.dp.toPx() }
            val top = with(density) { 8.dp.toPx() }
            val bottom = with(density) { 22.dp.toPx() }
            val plotW = size.width - left - right
            val plotH = size.height - top - bottom

            ticks.forEach { v ->
                val y = top + plotH * (1f - v / niceMax)
                drawLine(
                    color = grid,
                    start = Offset(left, y),
                    end = Offset(left + plotW, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
                )
            }

            val gap = plotW / bars.size
            val barW = gap * 0.55f
            bars.forEachIndexed { i, bar ->
                val cx = left + gap * (i + 0.5f)
                val h = plotH * (bar.value / niceMax)
                val y0 = top + plotH - h
                drawRoundRect(
                    color = bar.color,
                    topLeft = Offset(cx - barW / 2f, y0),
                    size = Size(barW, h),
                    cornerRadius = CornerRadius(6f, 6f),
                )
            }
        }

        ticks.forEach { v ->
            val py = with(density) { 8.dp.toPx() } +
                (canvasH.toFloat() - with(density) { (8.dp + 22.dp).toPx() }) * (1f - v / niceMax)
            Text(
                text = formatY(v),
                color = tickColor,
                fontSize = 10.sp,
                modifier = Modifier.offset { IntOffset(0, (py - 8f).roundToInt()) },
            )
        }

        bars.forEachIndexed { i, bar ->
            val left = with(density) { 36.dp.toPx() }
            val right = with(density) { 8.dp.toPx() }
            val plotW = canvasW.toFloat() - left - right
            val gap = plotW / bars.size
            val cx = left + gap * (i + 0.5f)
            Text(
                text = bar.label,
                color = tickColor,
                fontSize = 10.sp,
                modifier = Modifier.offset {
                    IntOffset((cx - 14f).roundToInt(), canvasH - with(density) { 16.dp.toPx() }.toInt())
                },
            )
        }

        if (selected in bars.indices && canvasW > 0 && canvasH > 0) {
            val left = with(density) { 36.dp.toPx() }
            val right = with(density) { 8.dp.toPx() }
            val top = with(density) { 8.dp.toPx() }
            val bottom = with(density) { 22.dp.toPx() }
            val plotW = canvasW.toFloat() - left - right
            val plotH = canvasH.toFloat() - top - bottom
            val gap = plotW / bars.size
            val cx = left + gap * (selected + 0.5f)
            val v = bars[selected].value
            val py = top + plotH * (1f - v / niceMax)
            ChartClampedTooltip(
                anchorX = cx,
                anchorY = py,
                style = ChartTooltipAnchorStyle.ColumnBarTop,
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tooltipBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(formatTooltip(selected), color = tooltipFg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
