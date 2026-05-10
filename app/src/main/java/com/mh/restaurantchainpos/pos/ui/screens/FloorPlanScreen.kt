package com.mh.restaurantchainpos.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.ReservationType
import com.mh.restaurantchainpos.pos.ui.floor.FloorCalendarView
import com.mh.restaurantchainpos.pos.ui.floor.FloorCanvas
import com.mh.restaurantchainpos.pos.ui.floor.FloorEditMode
import com.mh.restaurantchainpos.pos.ui.floor.FloorTabsRow
import com.mh.restaurantchainpos.pos.ui.floor.FloorViewMode
import com.mh.restaurantchainpos.pos.ui.floor.TableCardView
import com.mh.restaurantchainpos.pos.ui.floor.TableDrawer
import com.mh.restaurantchainpos.pos.ui.floor.rememberFloorPlanState
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.DarkFloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.LightFloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
fun FloorPlanScreen(
    @Suppress("UNUSED_PARAMETER") colors: PosColors,
    role: ActiveRole,
    isDark: Boolean = false,
    onPendingReservations: (Int) -> Unit,
) {
    val palette = if (isDark) DarkFloorPalette else LightFloorPalette
    val state = rememberFloorPlanState()
    val pending = state.reservations.count { it.type == ReservationType.Request }

    LaunchedEffect(pending) { onPendingReservations(pending) }

    if (state.editMode) {
        FloorEditMode(palette, state)
        return
    }

    Column(Modifier.fillMaxSize()) {
        // View tabs
        Row(
            Modifier
                .fillMaxWidth()
                .background(palette.card)
                .border(1.dp, palette.border)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.weight(1f))
            Row(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.raised)
                    .padding(2.dp),
            ) {
                FloorViewMode.entries.forEach { mode ->
                    val active = mode == state.view
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) Blue500 else Color.Transparent)
                            .clickable { state.view = mode }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            mode.label,
                            color = if (active) Color.White else palette.text2,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
        }

        FloorTabsRow(
            palette = palette,
            role = role,
            floors = state.floors,
            activeFloorId = state.activeFloorId,
            onSelectFloor = { state.activeFloorId = it },
            onRenameFloor = state::renameFloor,
            onAddFloor = state::addFloor,
            onRemoveFloor = state::removeFloor,
            onEditLayout = { state.editMode = true },
        )

        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (state.view) {
                FloorViewMode.Floor -> FloorCanvas(
                    palette = palette,
                    tables = state.tables,
                    editMode = false,
                    selectedTableId = state.selectedTableId,
                    showSeats = state.showSeats,
                    zoom = state.zoom,
                    onZoomChange = { state.zoom = it },
                    onSelectTable = { state.selectedTableId = it },
                    onDragTable = { _, _, _, _ -> },
                )
                FloorViewMode.Table -> TableCardView(
                    palette = palette,
                    tables = state.tables,
                    onSelect = { state.selectedTableId = it.id },
                )
                FloorViewMode.Calendar -> FloorCalendarView(
                    palette = palette,
                    floors = state.floors,
                    reservations = state.reservations,
                    onAccept = { reservation ->
                        val idx = state.reservations.indexOfFirst { it.id == reservation.id }
                        if (idx >= 0) state.reservations[idx] = reservation.copy(type = ReservationType.Confirmed)
                    },
                    onDecline = { reservation ->
                        state.reservations.removeAll { it.id == reservation.id }
                    },
                )
            }
        }
    }

    state.selectedTable?.let { table ->
        TableDrawer(
            palette = palette,
            table = table,
            onClose = { state.selectedTableId = null },
            onPay = { state.selectedTableId = null },
        )
    }
}
