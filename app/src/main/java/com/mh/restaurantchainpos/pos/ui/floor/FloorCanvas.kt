package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.IntOffset
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

    fun clampPan() {
        val canvasW = with(density) { FloorMetrics.CanvasW.dp.toPx() } * zoom
        val canvasH = with(density) { FloorMetrics.CanvasH.dp.toPx() } * zoom
        val minX = min(0f, viewportW - canvasW)
        val minY = min(0f, viewportH - canvasH)
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
            .pointerInput(zoom) {
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    val next = (zoom * gestureZoom).coerceIn(0.4f, 3f)
                    if (next != zoom) onZoomChange(next)
                    panX += pan.x
                    panY += pan.y
                    clampPan()
                }
            }
            .pointerInput(editMode, zoom) {
                detectDragGestures(
                    onDragStart = { if (editMode) onSelectTable(null) },
                    onDrag = { _, drag ->
                        panX += drag.x
                        panY += drag.y
                        clampPan()
                    },
                )
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
                    zoom = zoom,
                    onSelect = { onSelectTable(table.id) },
                    onDrag = { dx, dy, commit ->
                        val nx = ((table.x + dx).toFloat() / FloorMetrics.SnapGrid).roundToInt() * FloorMetrics.SnapGrid
                        val ny = ((table.y + dy).toFloat() / FloorMetrics.SnapGrid).roundToInt() * FloorMetrics.SnapGrid
                        val cx = max(0, min(FloorMetrics.CanvasW - table.width, nx))
                        val cy = max(0, min(FloorMetrics.CanvasH - table.height, ny))
                        onDragTable(table.id, cx, cy, commit)
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

@Composable
private fun TableNode(
    palette: FloorPalette,
    table: FloorTable,
    isSelected: Boolean,
    editMode: Boolean,
    showSeats: Boolean,
    zoom: Float,
    onSelect: () -> Unit,
    onDrag: (dx: Int, dy: Int, commit: Boolean) -> Unit,
) {
    val occupied = table.status == TableStatus.Occupied
    val reserved = table.status == TableStatus.Reserved
    val (fill, border, fg) = when {
        editMode && isSelected -> Triple(palette.editSelected, palette.editSelected, Color.White)
        editMode -> Triple(palette.editTableDefault, palette.editBorder, palette.editText2)
        occupied -> Triple(palette.occupiedFill, palette.occupiedBorder, palette.occupiedText)
        reserved -> Triple(palette.reservedFill, palette.reservedBorder, palette.reservedText)
        else -> Triple(palette.availableFill, palette.availableBorder, palette.availableText)
    }
    val shape = if (table.shape == TableShape.Circle) CircleShape else RoundedCornerShape(12.dp)
    val baseModifier = Modifier
        .offset { IntOffset(table.x, table.y) }
        .size(table.width.dp, table.height.dp)
        .clip(shape)
        .background(fill)
        .border(if (isSelected) 2.dp else 1.5.dp, border, shape)
    val withGesture = if (editMode) {
        baseModifier
            .pointerInput(table.id, zoom) {
                // Custom gesture loop: consume the drag so the parent canvas
                // pan does not fire while the user is moving a table.
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)
                    onSelect()
                    var pendingDx = 0f
                    var pendingDy = 0f
                    var moved = false
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val pointer = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (pointer.changedToUp()) {
                            if (moved) onDrag(0, 0, true)
                            break
                        }
                        val drag = pointer.positionChange()
                        if (drag != Offset.Zero) {
                            pointer.consume()
                            moved = true
                            pendingDx += drag.x / zoom
                            pendingDy += drag.y / zoom
                            val ix = pendingDx.roundToInt()
                            val iy = pendingDy.roundToInt()
                            if (ix != 0 || iy != 0) {
                                onDrag(ix, iy, false)
                                pendingDx -= ix
                                pendingDy -= iy
                            }
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

