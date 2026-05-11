package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.TableShape
import com.mh.restaurantchainpos.pos.data.TableStatus
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
internal fun TableNode(
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
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
        if (editMode) {
            if (showSeats) Text("(${table.seats})", color = fg.copy(alpha = 0.7f), fontSize = 13.sp)
        } else if (occupied) {
            Text(
                "${if (table.occupiedSeats > 0) table.occupiedSeats else table.seats}/${table.seats}",
                color = fg,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            if (table.revenue > 0) Text("₩%,d".format(table.revenue), color = fg, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        } else if (reserved) {
            Text("Reserved · ${table.reservationTime}", color = fg, fontSize = 12.sp)
        } else {
            Text("${table.seats}", color = fg, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
