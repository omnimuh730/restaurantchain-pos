package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.ui.geometry.Offset
import com.mh.restaurantchainpos.pos.data.FloorMetrics
import com.mh.restaurantchainpos.pos.data.FloorTable
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal data class FloorContentBoundsDp(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
) {
    val width: Int get() = (right - left).coerceAtLeast(1)
    val height: Int get() = (bottom - top).coerceAtLeast(1)
}

internal data class FloorContentBoundsPx(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = (right - left).coerceAtLeast(1f)
    val height: Float get() = (bottom - top).coerceAtLeast(1f)
}

internal fun calculateTableContentBoundsDp(tables: List<FloorTable>): FloorContentBoundsDp? {
    if (tables.isEmpty()) return null
    return FloorContentBoundsDp(
        left = tables.minOf { it.x },
        top = tables.minOf { it.y },
        right = tables.maxOf { it.x + it.width },
        bottom = tables.maxOf { it.y + it.height },
    )
}

internal fun calculateFitContentZoom(
    viewportW: Float,
    viewportH: Float,
    contentWidth: Float,
    contentHeight: Float,
    viewportPadding: Float = 0f,
): Float {
    if (viewportW <= 0f || viewportH <= 0f || contentWidth <= 0f || contentHeight <= 0f) return 1f
    val fitViewportW = (viewportW - viewportPadding * 2f).coerceAtLeast(1f)
    val fitViewportH = (viewportH - viewportPadding * 2f).coerceAtLeast(1f)
    val fitW = fitViewportW / contentWidth
    val fitH = fitViewportH / contentHeight
    return min(1f, max(0.1f, min(fitW, fitH)))
}

internal fun clampPanToContentBounds(
    proposedX: Float,
    proposedY: Float,
    zoom: Float,
    viewportW: Float,
    viewportH: Float,
    canvasW: Float,
    canvasH: Float,
    contentBounds: FloorContentBoundsPx?,
    viewportPadding: Float = 0f,
): Offset {
    val bounds = contentBounds ?: FloorContentBoundsPx(left = 0f, top = 0f, right = canvasW, bottom = canvasH)
    val horizontalPadding = viewportPadding.coerceAtMost(viewportW / 2f)
    val verticalPadding = viewportPadding.coerceAtMost(viewportH / 2f)
    val leftAligned = horizontalPadding - bounds.left * zoom
    val rightAligned = viewportW - horizontalPadding - bounds.right * zoom
    val topAligned = verticalPadding - bounds.top * zoom
    val bottomAligned = viewportH - verticalPadding - bounds.bottom * zoom
    val minX = min(rightAligned, leftAligned)
    val maxX = max(rightAligned, leftAligned)
    val minY = min(bottomAligned, topAligned)
    val maxY = max(bottomAligned, topAligned)
    return Offset(
        proposedX.coerceIn(minX, maxX),
        proposedY.coerceIn(minY, maxY),
    )
}

internal fun panForZoomAroundCentroid(
    panX: Float,
    panY: Float,
    centroid: Offset,
    oldZoom: Float,
    newZoom: Float,
): Offset {
    if (oldZoom <= 0f) return Offset(panX, panY)
    val zoomRatio = newZoom / oldZoom
    return Offset(
        x = centroid.x - (centroid.x - panX) * zoomRatio,
        y = centroid.y - (centroid.y - panY) * zoomRatio,
    )
}

internal fun pointerDeltaPxToCanvasDp(deltaPx: Float, pxPerDp: Float): Float = deltaPx / pxPerDp

internal fun calculateDraggedTablePosition(
    table: FloorTable,
    totalDxDp: Float,
    totalDyDp: Float,
): Pair<Int, Int> {
    val snapped = FloorMetrics.SnapGrid
    val nx = ((table.x + totalDxDp) / snapped).roundToInt() * snapped
    val ny = ((table.y + totalDyDp) / snapped).roundToInt() * snapped
    val cx = max(0, min(FloorMetrics.CanvasW - table.width, nx))
    val cy = max(0, min(FloorMetrics.CanvasH - table.height, ny))
    return cx to cy
}
