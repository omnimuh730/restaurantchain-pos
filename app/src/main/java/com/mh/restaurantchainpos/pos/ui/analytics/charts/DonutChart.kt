package com.mh.restaurantchainpos.pos.ui.analytics.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class DonutSlice(val value: Float, val color: Color)

/**
 * Donut chart with optional padding-angle gaps. Used by the Menu Analysis
 * category breakdown and the Customer Analysis returning/new chart.
 */
@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    diameter: Dp = 160.dp,
    thickness: Dp = 28.dp,
    paddingAngleDeg: Float = 3f,
) {
    if (slices.isEmpty()) return
    val total = slices.map { it.value }.sum().let { if (it <= 0f) 1f else it }
    Canvas(modifier.size(diameter)) {
        val stroke = thickness.toPx()
        val outer = size.minDimension
        val arcSize = Size(outer - stroke, outer - stroke)
        val topLeft = Offset(stroke / 2f, stroke / 2f)

        var startAngle = -90f
        slices.forEachIndexed { i, slice ->
            val sweep = slice.value / total * 360f
            val gap = if (slices.size > 1) paddingAngleDeg else 0f
            val drawSweep = (sweep - gap).coerceAtLeast(0.5f)
            drawArc(
                color = slice.color,
                startAngle = startAngle + gap / 2f,
                sweepAngle = drawSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke),
            )
            startAngle += sweep
            // Suppress unused warning for trig helpers we may want later.
            if (false) {
                val a = startAngle * PI / 180.0
                cos(a)
                sin(a)
                i.toString()
            }
        }
    }
}
