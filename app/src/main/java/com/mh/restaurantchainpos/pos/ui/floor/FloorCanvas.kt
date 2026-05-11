package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val bg = if (editMode) palette.editBg else palette.bg

    var viewportW by remember { mutableFloatStateOf(0f) }
    var viewportH by remember { mutableFloatStateOf(0f) }
    var panX by remember { mutableFloatStateOf(0f) }
    var panY by remember { mutableFloatStateOf(0f) }
    var fitApplied by remember(editMode) { mutableStateOf(false) }

    val canvasPxW = with(density) { FloorMetrics.CanvasW.dp.toPx() }
    val canvasPxH = with(density) { FloorMetrics.CanvasH.dp.toPx() }

    // Compute the "fit-to-content" zoom for the current viewport. Tables in the
    // mock data are tightly clustered in the canvas top-left, so on a phone
    // viewport zoom=1 leaves them stacked on top of each other. We use this to
    // both clamp the minimum zoom and to apply once on first measurement so
    // the initial render shows the full floor layout.
    val fitZoom = remember(tables, viewportW, viewportH) {
        val maxX = tables.maxOfOrNull { (it.x + it.width).toFloat() } ?: 0f
        val maxY = tables.maxOfOrNull { (it.y + it.height).toFloat() } ?: 0f
        val maxXPx = with(density) { maxX.dp.toPx() }
        val maxYPx = with(density) { maxY.dp.toPx() }
        if (maxXPx <= 0f || maxYPx <= 0f || viewportW <= 0f || viewportH <= 0f) {
            1f
        } else {
            val fitW = viewportW / maxXPx
            val fitH = viewportH / maxYPx
            min(1f, max(0.1f, min(fitW, fitH)))
        }
    }

    val minZoom = if (editMode) 0.1f else min(fitZoom, 1f)

    LaunchedEffect(fitZoom, viewportW) {
        if (!fitApplied && viewportW > 0f && fitZoom < 1f && !editMode) {
            onZoomChange(fitZoom)
            fitApplied = true
        }
    }
    LaunchedEffect(minZoom) {
        if (zoom < minZoom) onZoomChange(minZoom)
    }

    fun clampPan() {
        val w = canvasPxW * zoom
        val h = canvasPxH * zoom
        val minX = min(0f, viewportW - w)
        val minY = min(0f, viewportH - h)
        panX = panX.coerceIn(minX, 0f)
        panY = panY.coerceIn(minY, 0f)
    }

    Box(
        modifier
            .fillMaxSize()
            .background(bg)
            .clipToBounds()
            .onSizeChanged {
                viewportW = it.width.toFloat()
                viewportH = it.height.toFloat()
                clampPan()
            }
            // Single canvas-pan / pinch-zoom handler. Uses `requireUnconsumed = true`
            // so the loop only starts when the down event was NOT swallowed by a
            // table — that way single-touch background drag pans the canvas, but
            // touching a table immediately routes events to the table's own
            // gesture loop without competing with this one.
            .pointerInput(editMode, minZoom) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = true)
                    if (editMode) onSelectTable(null)
                    do {
                        val event = awaitPointerEvent()
                        val pan = event.calculatePan()
                        val gestureZoom = event.calculateZoom()
                        if (gestureZoom != 1f) {
                            val next = (zoom * gestureZoom).coerceIn(minZoom, 3f)
                            if (next != zoom) onZoomChange(next)
                        }
                        if (pan != Offset.Zero) {
                            panX += pan.x
                            panY += pan.y
                            clampPan()
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

        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(palette.editBorder.copy(alpha = 0.3f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text("${(zoom * 100).roundToInt()}%", color = palette.text2, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
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
