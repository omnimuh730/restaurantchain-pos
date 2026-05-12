package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.data.FloorMetrics
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Pan/zoom canvas for the floor plan. Mirrors the React FloorCanvas:
 * - Free-form 2D pan when dragging the dot-grid background.
 * - Pinch-to-zoom with clamped min/max.
 * - In edit mode each table is draggable, snapping to the SnapGrid;
 *   tap selects a table.
 */
@Composable
fun FloorCanvas(
    palette: FloorPalette,
    tables: List<FloorTable>,
    editMode: Boolean,
    selectedTableId: String?,
    showSeats: Boolean,
    zoom: Float,
    onZoomChange: (Float) -> Unit,
    onSelectTable: (String?) -> Unit,
    onDragTable: (id: String, x: Int, y: Int, commit: Boolean) -> Unit,
    zoomControlsTopPadding: Dp = 12.dp,
    zoomControlsEndPadding: Dp = 12.dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val bg = if (editMode) palette.editBg else palette.bg
    val currentZoom = rememberUpdatedState(zoom)
    val currentOnZoomChange = rememberUpdatedState(onZoomChange)
    val contentGutterPx = with(density) { FloorContentGutterDp.dp.toPx() }

    var viewportW by remember { mutableFloatStateOf(0f) }
    var viewportH by remember { mutableFloatStateOf(0f) }
    var panX by remember { mutableFloatStateOf(0f) }
    var panY by remember { mutableFloatStateOf(0f) }
    var fitApplied by remember(editMode) { mutableStateOf(false) }

    val canvasPxW = with(density) { FloorMetrics.CanvasW.dp.toPx() }
    val canvasPxH = with(density) { FloorMetrics.CanvasH.dp.toPx() }
    val contentBoundsDp = remember(tables) { calculateTableContentBoundsDp(tables) }
    val contentBoundsPx = contentBoundsDp?.let { bounds ->
        FloorContentBoundsPx(
            left = with(density) { bounds.left.dp.toPx() },
            top = with(density) { bounds.top.dp.toPx() },
            right = with(density) { bounds.right.dp.toPx() },
            bottom = with(density) { bounds.bottom.dp.toPx() },
        )
    }

    val fitZoom = remember(contentBoundsPx, viewportW, viewportH) {
        calculateFitContentZoom(
            viewportW = viewportW,
            viewportH = viewportH,
            contentWidth = contentBoundsPx?.width ?: 0f,
            contentHeight = contentBoundsPx?.height ?: 0f,
            viewportPadding = contentGutterPx,
        )
    }
    val minZoom = fitZoom

    fun clampPanAt(proposedX: Float, proposedY: Float, zoomForClamp: Float): Offset =
        clampPanToContentBounds(
            proposedX = proposedX,
            proposedY = proposedY,
            zoom = zoomForClamp,
            viewportW = viewportW,
            viewportH = viewportH,
            canvasW = canvasPxW,
            canvasH = canvasPxH,
            contentBounds = contentBoundsPx,
            viewportPadding = contentGutterPx,
        )

    fun applyClampedPan(zoomForClamp: Float = currentZoom.value) {
        val clamped = clampPanAt(panX, panY, zoomForClamp)
        panX = clamped.x
        panY = clamped.y
    }

    LaunchedEffect(fitZoom, viewportW) {
        if (!fitApplied && viewportW > 0f && fitZoom < 1f && !editMode) {
            onZoomChange(fitZoom)
            fitApplied = true
        }
    }
    LaunchedEffect(zoom, minZoom, viewportW, viewportH, contentBoundsPx) {
        val clampedZoom = zoom.coerceIn(minZoom, MaxFloorZoom)
        if (clampedZoom != zoom) {
            onZoomChange(clampedZoom)
        }
        val clamped = clampPanAt(panX, panY, clampedZoom)
        panX = clamped.x
        panY = clamped.y
    }

    Box(
        modifier
            .fillMaxSize()
            .background(bg)
            .clipToBounds()
            .onSizeChanged {
                viewportW = it.width.toFloat()
                viewportH = it.height.toFloat()
                applyClampedPan()
            }
            // Single canvas-pan / pinch-zoom handler. Uses `requireUnconsumed = true`
            // so the loop only starts when the down event was NOT swallowed by a
            // table — that way single-touch background drag pans the canvas, but
            // touching a table immediately routes events to the table's own
            // gesture loop without competing with this one.
            .pointerInput(editMode, minZoom, contentBoundsPx, viewportW, viewportH) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = true)
                    if (editMode) onSelectTable(null)
                    var gestureZoomLevel = currentZoom.value
                    do {
                        val event = awaitPointerEvent()
                        val pan = event.calculatePan()
                        val gestureZoom = event.calculateZoom()
                        if (gestureZoom != 1f) {
                            val nextZoom = (gestureZoomLevel * gestureZoom).coerceIn(minZoom, MaxFloorZoom)
                            if (nextZoom != gestureZoomLevel) {
                                val zoomPan = panForZoomAroundCentroid(
                                    panX = panX,
                                    panY = panY,
                                    centroid = event.calculateCentroid(useCurrent = true),
                                    oldZoom = gestureZoomLevel,
                                    newZoom = nextZoom,
                                )
                                val clamped = clampPanAt(zoomPan.x, zoomPan.y, nextZoom)
                                panX = clamped.x
                                panY = clamped.y
                                gestureZoomLevel = nextZoom
                                currentOnZoomChange.value(nextZoom)
                            }
                        }
                        if (pan != Offset.Zero) {
                            val clamped = clampPanAt(panX + pan.x, panY + pan.y, gestureZoomLevel)
                            panX = clamped.x
                            panY = clamped.y
                        }
                        event.changes.forEach { if (it.positionChange() != Offset.Zero) it.consume() }
                    } while (event.changes.any { it.pressed })
                }
            },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawFloorWorldDotGridCulled(
                palette = palette,
                editMode = editMode,
                panX = panX,
                panY = panY,
                zoom = zoom,
                worldW = canvasPxW,
                worldH = canvasPxH,
                viewportW = viewportW,
                viewportH = viewportH,
                gridStepPx = FloorMetrics.SnapGrid.dp.toPx(),
            )
        }
        Box(
            Modifier
                .size(width = FloorMetrics.CanvasW.dp, height = FloorMetrics.CanvasH.dp)
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0f, 0f)
                    scaleX = zoom
                    scaleY = zoom
                    translationX = panX
                    translationY = panY
                },
        ) {
            tables.forEach { table ->
                TableNode(
                    palette = palette,
                    table = table,
                    isSelected = table.id == selectedTableId,
                    editMode = editMode,
                    showSeats = showSeats,
                    pxPerDp = density.density,
                    onSelect = { onSelectTable(table.id) },
                    onDragMove = { dragStartTable, totalDxDp, totalDyDp, commit ->
                        val (cx, cy) = calculateDraggedTablePosition(
                            table = dragStartTable,
                            totalDxDp = totalDxDp,
                            totalDyDp = totalDyDp,
                        )
                        onDragTable(dragStartTable.id, cx, cy, commit)
                    },
                )
            }
        }

        ZoomControls(
            palette = palette,
            zoom = zoom,
            minZoom = minZoom,
            onZoomChange = onZoomChange,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = zoomControlsEndPadding, top = zoomControlsTopPadding),
        )
    }
}

/**
 * Snap-grid dots in **world** coordinates, but only points visible in the viewport,
 * so the texture fills the screen (not only the virtual canvas rect) when zoomed.
 */
private fun DrawScope.drawFloorWorldDotGridCulled(
    palette: FloorPalette,
    editMode: Boolean,
    panX: Float,
    panY: Float,
    zoom: Float,
    worldW: Float,
    worldH: Float,
    viewportW: Float,
    viewportH: Float,
    gridStepPx: Float,
) {
    if (viewportW <= 1f || viewportH <= 1f || zoom <= 0.001f || gridStepPx <= 0.001f) return
    val z = zoom
    val dotColor = if (editMode) {
        palette.editBorder.copy(alpha = 0.52f)
    } else {
        palette.text3.copy(alpha = 0.72f)
    }
    val dotR = (2f * z).coerceIn(1.15f, 2.4f)
    val minWx = (floor((-panX / z) / gridStepPx) * gridStepPx).toFloat().coerceIn(0f, worldW)
    val maxWx = (ceil(((-panX + viewportW) / z) / gridStepPx) * gridStepPx + gridStepPx).toFloat()
        .coerceIn(0f, worldW + gridStepPx)
    val minWy = (floor((-panY / z) / gridStepPx) * gridStepPx).toFloat().coerceIn(0f, worldH)
    val maxWy = (ceil(((-panY + viewportH) / z) / gridStepPx) * gridStepPx + gridStepPx).toFloat()
        .coerceIn(0f, worldH + gridStepPx)
    var wy = minWy
    while (wy <= maxWy) {
        var wx = minWx
        while (wx <= maxWx) {
            val sx = wx * z + panX
            val sy = wy * z + panY
            drawCircle(dotColor, dotR, Offset(sx, sy))
            wx += gridStepPx
        }
        wy += gridStepPx
    }
}
