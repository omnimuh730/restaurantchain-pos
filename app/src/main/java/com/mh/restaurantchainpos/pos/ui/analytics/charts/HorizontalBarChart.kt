package com.mh.restaurantchainpos.pos.ui.analytics.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HorizontalBar(val label: String, val value: Float, val accent: Color)

/**
 * Horizontal-bar list with right-aligned value labels. Mirrors the Customer
 * Analysis "Visit frequency" chart from the React demo (recharts vertical
 * BarChart). Light, lazy-friendly composable: no canvas measurements.
 */
@Composable
fun HorizontalBarChart(
    bars: List<HorizontalBar>,
    modifier: Modifier = Modifier,
    track: Color = Color(0xFFE2E8F0),
    label: Color = Color(0xFF64748B),
    text: Color = Color(0xFF1E293B),
    labelWidth: androidx.compose.ui.unit.Dp = 56.dp,
) {
    if (bars.isEmpty()) return
    val maxV = bars.maxOf { it.value }.let { if (it <= 0f) 1f else it }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        bars.forEach { bar ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    bar.label,
                    color = label,
                    fontSize = 11.sp,
                    modifier = Modifier.width(labelWidth),
                )
                Box(
                    Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(track),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(bar.value / maxV)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bar.accent),
                    )
                }
                Text(
                    valueLabel(bar.value),
                    color = text,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .width(56.dp)
                        .padding(start = 8.dp),
                )
            }
        }
    }
}

private fun valueLabel(v: Float): String =
    if (v >= 1000f) "${(v / 1000f).toInt()}k" else v.toInt().toString()
