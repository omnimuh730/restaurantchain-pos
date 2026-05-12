package com.mh.restaurantchainpos.pos.ui.analytics.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Smooth Catmull–Rom area chart with grid, labels and a tap tooltip. Mirrors
 * the React `<AreaChart>` (recharts) used in SalesDashboardView/CustomerAnalysis.
 */
@Composable
fun AreaCurveChart(
    points: List<Float>,
    labels: List<String>,
    formatY: (Float) -> String,
    formatTooltip: (Int) -> String,
    modifier: Modifier = Modifier,
    accent: Color = Color(0xFF3B82F6),
    grid: Color = Color(0xFFE2E8F0),
    tickColor: Color = Color(0xFF94A3B8),
    tooltipBg: Color = Color(0xFF2563EB),
    tooltipFg: Color = Color.White,
) {
    require(points.size == labels.size)
    if (points.isEmpty()) return
    val density = LocalDensity.current
    var selected by remember(points) { mutableIntStateOf(-1) }
    var canvasW by remember { mutableIntStateOf(0) }
    var canvasH by remember { mutableIntStateOf(0) }

    val maxY = points.max().let { if (it <= 0f) 1f else it }
    val niceMax = niceCeiling(maxY)
    val tickCount = 4
    val ticks = (0..tickCount).map { it.toFloat() / tickCount * niceMax }

    Box(modifier) {
        Canvas(
            Modifier
                .fillMaxSize()
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        if (canvasW <= 0) return@detectTapGestures
                        val left = with(density) { 36.dp.toPx() }
                        val xs = pointXs(canvasW.toFloat(), points.size, left)
                        val nearest = xs.indices.minByOrNull { abs(xs[it] - offset.x) } ?: -1
                        selected = if (selected == nearest) -1 else nearest
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

            val xs = pointXs(size.width, points.size, left)
            val ys = points.map { v -> top + plotH * (1f - v / niceMax) }

            val path = smoothPath(xs, ys)
            val areaPath = Path().apply {
                addPath(path)
                lineTo(xs.last(), top + plotH)
                lineTo(xs.first(), top + plotH)
                close()
            }
            drawPath(
                areaPath,
                brush = Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.30f), accent.copy(alpha = 0f)),
                    startY = top,
                    endY = top + plotH,
                ),
            )
            drawPath(path, color = accent, style = Stroke(width = 3f, cap = StrokeCap.Round))

            xs.forEachIndexed { i, x ->
                drawCircle(accent, radius = 3.5f, center = Offset(x, ys[i]))
                if (i == selected) {
                    drawCircle(Color.White, radius = 7f, center = Offset(x, ys[i]))
                    drawCircle(accent, radius = 5f, center = Offset(x, ys[i]))
                }
            }
        }

        ticks.forEach { v ->
            val py = with(density) { 8.dp.toPx() } +
                (canvasH.toFloat() - with(density) { (8.dp + 22.dp).toPx() }) * (1f - v / niceMax)
            Text(
                text = formatY(v),
                color = tickColor,
                fontSize = 10.sp,
                modifier = Modifier
                    .offset { IntOffset(0, (py - 8f).roundToInt()) }
                    .padding(start = 0.dp),
            )
        }
        labels.forEachIndexed { i, label ->
            val left = with(density) { 36.dp.toPx() }
            val xs = pointXs(canvasW.toFloat(), labels.size, left)
            if (i in xs.indices) {
                val px = xs[i]
                Text(
                    text = label,
                    color = tickColor,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .offset { IntOffset((px - 12f).roundToInt(), canvasH - with(density) { 16.dp.toPx() }.toInt()) },
                )
            }
        }

        if (selected in points.indices && canvasW > 0 && canvasH > 0) {
            val left = with(density) { 36.dp.toPx() }
            val xs = pointXs(canvasW.toFloat(), points.size, left)
            val px = xs[selected]
            val py = with(density) { 8.dp.toPx() } +
                (canvasH.toFloat() - with(density) { (8.dp + 22.dp).toPx() }) * (1f - points[selected] / niceMax)
            ChartClampedTooltip(
                anchorX = px,
                anchorY = py,
                style = ChartTooltipAnchorStyle.AreaPoint,
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

private fun pointXs(width: Float, count: Int, left: Float): List<Float> {
    if (count == 0) return emptyList()
    if (count == 1) return listOf(width / 2f)
    val plotW = width - left - 8f
    val step = plotW / (count - 1)
    return (0 until count).map { left + step * it }
}

private fun smoothPath(xs: List<Float>, ys: List<Float>): Path {
    val p = Path()
    if (xs.isEmpty()) return p
    p.moveTo(xs[0], ys[0])
    for (i in 0 until xs.size - 1) {
        val x0 = if (i == 0) xs[i] else xs[i - 1]
        val y0 = if (i == 0) ys[i] else ys[i - 1]
        val x1 = xs[i]
        val y1 = ys[i]
        val x2 = xs[i + 1]
        val y2 = ys[i + 1]
        val x3 = if (i + 2 < xs.size) xs[i + 2] else xs[i + 1]
        val y3 = if (i + 2 < ys.size) ys[i + 2] else ys[i + 1]
        val cp1x = x1 + (x2 - x0) / 6f
        val cp1y = y1 + (y2 - y0) / 6f
        val cp2x = x2 - (x3 - x1) / 6f
        val cp2y = y2 - (y3 - y1) / 6f
        p.cubicTo(cp1x, cp1y, cp2x, cp2y, x2, y2)
    }
    return p
}

internal fun niceCeiling(value: Float): Float {
    if (value <= 0f) return 1f
    val mag = Math.pow(10.0, Math.floor(Math.log10(value.toDouble()))).toFloat()
    val n = value / mag
    val nice = when {
        n <= 1f -> 1f
        n <= 2f -> 2f
        n <= 2.5f -> 2.5f
        n <= 5f -> 5f
        else -> 10f
    }
    return nice * mag
}
