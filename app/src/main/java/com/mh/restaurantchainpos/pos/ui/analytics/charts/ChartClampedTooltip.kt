package com.mh.restaurantchainpos.pos.ui.analytics.charts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Positions chart tooltip text near an anchor while keeping the bubble inside
 * the chart box (avoids clipping at screen / card edges).
 */
internal enum class ChartTooltipAnchorStyle {
    /** Offset to the right of the point; flip left if needed. Prefer above, then below. */
    AreaPoint,

    /** Horizontally centered on [anchorX]; prefer above bar top, else below. */
    ColumnBarTop,
}

@Composable
internal fun ChartClampedTooltip(
    anchorX: Float,
    anchorY: Float,
    style: ChartTooltipAnchorStyle,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val marginPx = with(density) { MarginDp.toPx() }
    val gapPx = with(density) { GapDp.toPx() }

    SubcomposeLayout(modifier) { constraints ->
        val maxW = constraints.maxWidth
        val maxH = constraints.maxHeight
        if (maxW <= 0 || maxH <= 0) {
            return@SubcomposeLayout layout(0, 0) {}
        }
        val marginI = marginPx.roundToInt()
        val innerMaxW = (maxW - 2 * marginI).coerceAtLeast(0)
        val placeable = subcompose("chart_tooltip", content).first().measure(
            Constraints(
                maxWidth = innerMaxW,
                maxHeight = maxH,
            ),
        )
        val tw = placeable.width
        val th = placeable.height
        val m = marginPx

        fun clampX(x: Float): Float {
            val minX = m
            val maxX = ((maxW - tw).toFloat() - m).coerceAtLeast(minX)
            return x.coerceIn(minX, maxX)
        }
        fun clampY(y: Float): Float {
            val minY = m
            val maxY = ((maxH - th).toFloat() - m).coerceAtLeast(minY)
            return y.coerceIn(minY, maxY)
        }

        val (x, y) = when (style) {
            ChartTooltipAnchorStyle.AreaPoint -> {
                var tx = anchorX + gapPx
                var ty = anchorY - th - gapPx
                if (tx + tw > maxW - m) {
                    tx = anchorX - gapPx - tw
                }
                if (ty < m) {
                    ty = anchorY + gapPx
                }
                if (ty + th > maxH - m) {
                    ty = maxH - th - m
                }
                clampX(tx) to clampY(ty)
            }
            ChartTooltipAnchorStyle.ColumnBarTop -> {
                var tx = anchorX - tw / 2f
                var ty = anchorY - th - gapPx
                if (ty < m) {
                    ty = anchorY + gapPx
                }
                clampX(tx) to clampY(ty)
            }
        }

        layout(maxW, maxH) {
            placeable.place(x.roundToInt(), y.roundToInt())
        }
    }
}

private val MarginDp: Dp = 6.dp
private val GapDp: Dp = 8.dp
