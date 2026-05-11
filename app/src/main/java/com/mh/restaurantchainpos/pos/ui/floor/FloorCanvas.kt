package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.FloorMetrics
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.TableShape
import com.mh.restaurantchainpos.pos.data.TableStatus
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val MaxFloorZoom = 3f
private const val ZoomStep = 0.1f

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
    zoomControlsBottomPadding: Dp = 12.dp,
    zoomControlsEndPadding: Dp = 12.dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val bg = if (editMode) palette.editBg else palette.bg
    val currentZoom = rememberUpdatedState(zoom)
    val currentOnZoomChange = rememberUpdatedState(onZoomChange)

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
            Canvas(Modifier.matchParentSize()) {
                val step = FloorMetrics.SnapGrid.dp.toPx()
                val dotColor = palette.editBorder.copy(alpha = if (editMode) 0.4f else 0.18f)
                var y = 0f
                while (y < size.height) {
                    var x = 0f
                    while (x < size.width) {
                        drawCircle(dotColor, radius = 1.5f, center = Offset(x, y))
                        x += step
                    }
                    y += step
                }
            }
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
                .align(Alignment.BottomEnd)
                .padding(end = zoomControlsEndPadding, bottom = zoomControlsBottomPadding),
        )
    }
}

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
): Float {
    if (viewportW <= 0f || viewportH <= 0f || contentWidth <= 0f || contentHeight <= 0f) return 1f
    val fitW = viewportW / contentWidth
    val fitH = viewportH / contentHeight
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
): Offset {
    val bounds = contentBounds ?: FloorContentBoundsPx(left = 0f, top = 0f, right = canvasW, bottom = canvasH)
    val minX = min(viewportW - bounds.right * zoom, -bounds.left * zoom)
    val maxX = max(viewportW - bounds.right * zoom, -bounds.left * zoom)
    val minY = min(viewportH - bounds.bottom * zoom, -bounds.top * zoom)
    val maxY = max(viewportH - bounds.bottom * zoom, -bounds.top * zoom)
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

private fun nextZoom(zoom: Float, delta: Float, minZoom: Float): Float =
    (zoom + delta).coerceIn(minZoom, MaxFloorZoom)

@Composable
private fun ZoomControls(
    palette: FloorPalette,
    zoom: Float,
    minZoom: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.editBorder.copy(alpha = 0.32f))
            .border(1.dp, palette.editBorder.copy(alpha = 0.38f), RoundedCornerShape(10.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        ZoomButton(
            enabled = zoom > minZoom + 0.001f,
            palette = palette,
            icon = Icons.Outlined.Remove,
            contentDescription = "Zoom out",
            onClick = { onZoomChange(nextZoom(zoom, -ZoomStep, minZoom)) },
        )
        Text(
            "${(zoom * 100).roundToInt()}%",
            color = palette.text2,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(42.dp),
        )
        ZoomButton(
            enabled = zoom < MaxFloorZoom - 0.001f,
            palette = palette,
            icon = Icons.Outlined.Add,
            contentDescription = "Zoom in",
            onClick = { onZoomChange(nextZoom(zoom, ZoomStep, minZoom)) },
        )
    }
}

@Composable
private fun ZoomButton(
    enabled: Boolean,
    palette: FloorPalette,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(if (enabled) Color.White.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.32f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) palette.editText1 else palette.editText3,
            modifier = Modifier.size(15.dp),
        )
    }
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

@Composable
private fun TableNode(
    palette: FloorPalette,
    table: FloorTable,
    isSelected: Boolean,
    editMode: Boolean,
    showSeats: Boolean,
    pxPerDp: Float,
    onSelect: () -> Unit,
    onDragMove: (dragStartTable: FloorTable, totalDxDp: Float, totalDyDp: Float, commit: Boolean) -> Unit,
) {
    val occupied = table.status == TableStatus.Occupied
    val reserved = table.status == TableStatus.Reserved
    val (fill, border, fg) = when {
        editMode && isSelected -> Triple(palette.editSelected, Color(0xFF3370E8), Color.White)
        editMode -> Triple(palette.editTableDefault, palette.editBorder, palette.editText2)
        occupied -> Triple(palette.editSelected, Color(0xFF3370E8), Color.White)
        reserved -> Triple(palette.editTableDefault, palette.reservedBorder, palette.editText1)
        else -> Triple(palette.editTableDefault, palette.availableBorder, palette.editText1)
    }
    val shape = if (table.shape == TableShape.Circle) CircleShape else RoundedCornerShape(12.dp)
    // Canvas-space coordinates (`table.x`, etc.) are conceptual dp — the
    // React reference uses CSS px which we mirror as dp on Android. Use the
    // dp-overload of `offset` so positioning matches sizing.
    val baseModifier = Modifier
        .offset(table.x.dp, table.y.dp)
        .size(table.width.dp, table.height.dp)
        .clip(shape)
        .background(fill)
        .border(if (isSelected) 2.dp else 1.5.dp, border, shape)
    val currentTable by rememberUpdatedState(table)
    val currentOnDragMove by rememberUpdatedState(onDragMove)
    val withGesture = if (editMode) {
        baseModifier
            .pointerInput(table.id, pxPerDp) {
                // Custom gesture loop. Eagerly consume on the Initial pass so
                // the parent canvas pan never sees this pointer.
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    down.consume()
                    val dragStartTable = currentTable
                    onSelect()
                    var totalDx = 0f
                    var totalDy = 0f
                    var moved = false
                    // `positionChange` is already reported in the transformed
                    // canvas' local coordinates, so only convert px -> dp here.
                    // Dividing by zoom again makes the table outrun the finger
                    // whenever the canvas is zoomed below 100%.
                    // The React reference computes each move from the drag
                    // start, so keep total gesture movement instead of snapping
                    // tiny per-frame deltas back to the start cell.
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val pointer = event.changes.firstOrNull { it.id == down.id } ?: break
                        val drag = pointer.positionChange()
                        if (drag != Offset.Zero) {
                            pointer.consume()
                            moved = true
                            totalDx += pointerDeltaPxToCanvasDp(drag.x, pxPerDp)
                            totalDy += pointerDeltaPxToCanvasDp(drag.y, pxPerDp)
                            currentOnDragMove(dragStartTable, totalDx, totalDy, false)
                        }
                        if (pointer.changedToUp()) {
                            pointer.consume()
                            if (moved) currentOnDragMove(dragStartTable, totalDx, totalDy, true)
                            break
                        }
                    }
                }
            }
    } else baseModifier.pointerInput(table.id) {
        detectTapGestures { onSelect() }
    }

    Column(
        modifier = withGesture.padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            table.label,
            color = fg,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
        if (editMode) {
            if (showSeats) Text("(${table.seats})", color = fg.copy(alpha = 0.7f), fontSize = 11.sp)
        } else if (occupied) {
            Text(
                "${if (table.occupiedSeats > 0) table.occupiedSeats else table.seats}/${table.seats}",
                color = fg,
                fontSize = 11.sp,
            )
            if (table.revenue > 0) Text("₩%,d".format(table.revenue), color = fg, fontSize = 12.sp)
        } else if (reserved) {
            Text("Reserved · ${table.reservationTime}", color = fg, fontSize = 10.sp)
        } else {
            Text("${table.seats}", color = fg, fontSize = 11.sp)
        }
    }
}
