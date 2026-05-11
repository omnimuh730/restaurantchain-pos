package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.Floor
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.data.ReservationType
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.Green500
import com.mh.restaurantchainpos.pos.ui.theme.Red500

private val Hours = (8..23).toList()

@Composable
fun FloorCalendarView(
    palette: FloorPalette,
    floors: List<Floor>,
    reservations: List<Reservation>,
    onAccept: (Reservation) -> Unit,
    onDecline: (Reservation) -> Unit,
    onAssignTable: (reservationId: String, tableId: String) -> Unit = { _, _ -> },
) {
    val isMobile = rememberIsMobile()
    var dayOffset by remember { mutableStateOf(0) }
    var floorId by remember { mutableStateOf(floors.first().id) }
    var panelOpen by remember { mutableStateOf(false) }
    var assigningId by remember { mutableStateOf<String?>(null) }
    val activeFloor = floors.firstOrNull { it.id == floorId } ?: floors.first()
    val dayReservations = reservations.filter { it.dayOffset == dayOffset }
    val pending = dayReservations.filter { it.type == ReservationType.Request }
    val confirmed = dayReservations.filter { it.type == ReservationType.Confirmed }
    val assigningRez = assigningId?.let { id -> reservations.firstOrNull { it.id == id } }

    Box(Modifier.fillMaxSize().background(palette.bg)) {
        Column(Modifier.fillMaxSize()) {
            CalendarHeader(
                palette = palette,
                dayOffset = dayOffset,
                onDayOffset = { dayOffset = it },
                isMobile = isMobile,
                pendingCount = pending.size,
                onMenuClick = { panelOpen = true },
            )
            assigningRez?.let { rez ->
                AssignBanner(
                    palette = palette,
                    reservation = rez,
                    onCancel = { assigningId = null },
                )
            }
            Row(Modifier.weight(1f).fillMaxWidth()) {
                if (!isMobile) {
                    CalendarPanel(
                        palette = palette,
                        pending = pending,
                        confirmed = confirmed,
                        onAccept = onAccept,
                        onDecline = onDecline,
                        onStartAssign = { assigningId = it.id },
                        modifier = Modifier.width(280.dp).fillMaxSize(),
                    )
                }
                Box(Modifier.weight(1f).fillMaxSize().background(palette.bg)) {
                    CalendarTimeline(
                        palette = palette,
                        floor = activeFloor,
                        reservations = confirmed,
                        assigningRez = assigningRez,
                        onAssignToTable = { tableId ->
                            assigningRez?.let { rez ->
                                onAssignTable(rez.id, tableId)
                                assigningId = null
                            }
                        },
                    )
                }
            }
        }

        if (isMobile) {
            AnimatedVisibility(
                visible = panelOpen,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000))
                        .clickable { panelOpen = false },
                )
            }
            AnimatedVisibility(
                visible = panelOpen,
                enter = slideInHorizontally(initialOffsetX = { -it }),
                exit = slideOutHorizontally(targetOffsetX = { -it }),
            ) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .width(320.dp)
                        .background(palette.card)
                        .border(1.dp, palette.border),
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Reservations", color = palette.text1, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable { panelOpen = false },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("✕", color = palette.text2, fontSize = 14.sp)
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    CalendarPanel(
                        palette = palette,
                        pending = pending,
                        confirmed = confirmed,
                        onAccept = onAccept,
                        onDecline = onDecline,
                        onStartAssign = {
                            assigningId = it.id
                            panelOpen = false
                        },
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AssignBanner(palette: FloorPalette, reservation: Reservation, onCancel: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Blue500.copy(alpha = 0.1f))
            .border(1.dp, Blue500.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("⌖", color = Blue500, fontSize = 14.sp)
        Text(
            "Assign ${reservation.guestName} (${reservation.partySize}P · ${reservation.startTime}) — tap a table row",
            color = palette.text1,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
        )
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onCancel)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text("✕ Cancel", color = palette.text2, fontSize = 11.sp)
        }
    }
}

@Composable
private fun CalendarHeader(
    palette: FloorPalette,
    dayOffset: Int,
    onDayOffset: (Int) -> Unit,
    isMobile: Boolean,
    pendingCount: Int,
    onMenuClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(palette.card)
            .border(1.dp, palette.border)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isMobile) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onMenuClick),
                contentAlignment = Alignment.Center,
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Text("☰", color = palette.text2, fontSize = 18.sp)
                    if (pendingCount > 0) {
                        Box(
                            Modifier
                                .padding(top = 2.dp, end = 2.dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Amber500),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("$pendingCount", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Stepper("‹", palette) { onDayOffset(dayOffset - 1) }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dayLabel(dayOffset), color = palette.text1, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            if (!isMobile) Text(dayDate(dayOffset), color = palette.text2, fontSize = 12.sp)
        }
        Stepper("›", palette) { onDayOffset(dayOffset + 1) }
        if (!isMobile) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LegendDot(Green500, "Confirmed", palette)
                LegendDot(Amber500, "Request", palette)
                LegendDot(Red500, "No-show", palette)
                LegendDot(Blue500, "Walk-in", palette)
            }
        }
    }
}

@Composable
private fun Stepper(label: String, palette: FloorPalette, onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(palette.raised)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = palette.text2, fontSize = 16.sp)
    }
}

@Composable
private fun LegendDot(color: Color, label: String, palette: FloorPalette) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, color = palette.text2, fontSize = 11.sp)
    }
}

@Composable
private fun CalendarPanel(
    palette: FloorPalette,
    pending: List<Reservation>,
    confirmed: List<Reservation>,
    onAccept: (Reservation) -> Unit,
    onDecline: (Reservation) -> Unit,
    onStartAssign: (Reservation) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.background(palette.card).border(1.dp, palette.border)) {
        Text(
            "Requests · ${pending.size}",
            color = palette.text2,
            fontSize = 11.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
        LazyColumn(Modifier.weight(1f)) {
            items(pending) { reservation ->
                ReservationRow(palette, reservation, isRequest = true) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ActionPill("Accept", Color.White, Blue500) { onAccept(reservation) }
                        ActionPill("Assign", palette.text1, palette.raised) { onStartAssign(reservation) }
                        ActionPill("Decline", Color(0xFFEF4444), Color(0x14EF4444)) { onDecline(reservation) }
                    }
                }
            }
            item {
                Text(
                    "Confirmed · ${confirmed.size}",
                    color = palette.text2,
                    fontSize = 11.sp,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                )
                Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
            }
            items(confirmed) { reservation ->
                ReservationRow(palette, reservation, isRequest = false) {
                    Text(reservation.status.ifBlank { "Confirmed" }, color = palette.text2, fontSize = 11.sp)
                }
            }
            if (pending.isEmpty() && confirmed.isEmpty()) {
                item {
                    Text(
                        "No reservations on this day.",
                        color = palette.text3,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservationRow(
    palette: FloorPalette,
    reservation: Reservation,
    isRequest: Boolean,
    trailing: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(if (isRequest) Amber500 else Green500))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(reservation.guestName, color = palette.text1, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    "${reservation.startTime} · party ${reservation.partySize} · ${reservation.durationHours}h",
                    color = palette.text2,
                    fontSize = 11.sp,
                )
            }
            Text(reservation.tableId.ifBlank { "—" }, color = palette.text3, fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { trailing() }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border.copy(alpha = 0.5f)))
}

@Composable
private fun ActionPill(label: String, contentColor: Color, background: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(label, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CalendarTimeline(
    palette: FloorPalette,
    floor: Floor,
    reservations: List<Reservation>,
    assigningRez: Reservation? = null,
    onAssignToTable: (String) -> Unit = {},
) {
    val hScroll = rememberScrollState()
    Column(Modifier.fillMaxSize().background(palette.bg)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(palette.raised)
                .padding(vertical = 4.dp)
                .horizontalScroll(hScroll),
        ) {
            Spacer(Modifier.width(96.dp))
            Hours.forEach { hour ->
                Box(Modifier.width(72.dp), contentAlignment = Alignment.Center) {
                    Text("${hour.toString().padStart(2, '0')}:00", color = palette.text2, fontSize = 11.sp)
                }
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            items(floor.tables) { table ->
                val canAssign = assigningRez != null && table.seats >= assigningRez.partySize
                val rowBg = if (canAssign) Blue500.copy(alpha = 0.06f) else Color.Transparent
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(rowBg)
                        .border(1.dp, palette.border.copy(alpha = 0.4f))
                        .then(if (canAssign) Modifier.clickable { onAssignToTable(table.id) } else Modifier)
                        .horizontalScroll(hScroll),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .width(96.dp)
                            .height(40.dp)
                            .background(if (canAssign) Blue500.copy(alpha = 0.15f) else palette.card)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Column {
                            Text(table.label, color = palette.text1, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            if (canAssign) Text("✓ assignable", color = Blue500, fontSize = 9.sp)
                        }
                    }
                    Box(Modifier.height(40.dp)) {
                        Row {
                            Hours.forEach { _ ->
                                Box(
                                    Modifier
                                        .width(72.dp)
                                        .height(40.dp)
                                        .border(1.dp, palette.border.copy(alpha = 0.3f)),
                                )
                            }
                        }
                        reservations.filter { it.tableId == table.id }.forEach { reservation ->
                            val startHour = reservation.startTime.substringBefore(":").toIntOrNull() ?: 0
                            val startMin = reservation.startTime.substringAfter(":", "0").toIntOrNull() ?: 0
                            val offsetHours = (startHour - Hours.first()).coerceAtLeast(0)
                            val offsetXdp = (offsetHours * 72) + ((startMin / 60f) * 72f).toInt()
                            val widthDp = (reservation.durationHours * 72).toInt()
                            Box(
                                Modifier
                                    .padding(start = offsetXdp.dp, top = 4.dp, bottom = 4.dp)
                                    .width(widthDp.dp)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(reservationColor(reservation).copy(alpha = 0.2f))
                                    .border(1.dp, reservationColor(reservation), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    "${reservation.guestName} · ${reservation.startTime}",
                                    color = reservationColor(reservation),
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun reservationColor(reservation: Reservation): Color = when {
    reservation.status == "NO_SHOW" -> Red500
    reservation.status == "COMPLETED" -> Blue500
    reservation.type == ReservationType.Request -> Amber500
    else -> Green500
}

private fun dayLabel(offset: Int): String = when (offset) {
    -1 -> "Yesterday"
    0 -> "Today"
    1 -> "Tomorrow"
    else -> if (offset < 0) "${-offset}d ago" else "in ${offset}d"
}

private fun dayDate(offset: Int): String {
    val day = (15 + offset).coerceAtLeast(1)
    return "May $day, 2026"
}
