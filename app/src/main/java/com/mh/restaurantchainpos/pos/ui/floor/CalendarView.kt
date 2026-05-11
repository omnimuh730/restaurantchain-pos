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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.Floor
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.data.ReservationType
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
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
    val floorId by remember { mutableStateOf(floors.first().id) }
    var panelOpen by remember { mutableStateOf(false) }
    var assigningId by remember { mutableStateOf<String?>(null) }
    var previewTableId by remember { mutableStateOf<String?>(null) }
    val activeFloor = floors.firstOrNull { it.id == floorId } ?: floors.first()
    val dayReservations = reservations.filter { it.dayOffset == dayOffset }
    val pending = dayReservations.filter { it.type == ReservationType.Request }
    val confirmed = dayReservations.filter { it.type == ReservationType.Confirmed }
    val assigningRez = assigningId?.let { id -> reservations.firstOrNull { it.id == id } }
    val previewTable = previewTableId?.let { id -> activeFloor.tables.firstOrNull { it.id == id } }

    fun startAssign(rez: Reservation) {
        assigningId = rez.id
        previewTableId = null
        panelOpen = false
    }

    fun cancelAssign() {
        assigningId = null
        previewTableId = null
    }

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
                    previewTableLabel = previewTable?.label,
                    onCancel = { cancelAssign() },
                    onConfirm = {
                        val tableId = previewTableId
                        if (tableId != null) {
                            onAssignTable(rez.id, tableId)
                            cancelAssign()
                        }
                    },
                )
            }
            Row(Modifier.weight(1f).fillMaxWidth()) {
                // On desktop the panel docks to the left, but it's hidden during assign mode
                // so the user can see the highlighted timeline rows clearly.
                if (!isMobile && assigningRez == null) {
                    CalendarPanel(
                        palette = palette,
                        pending = pending,
                        confirmed = confirmed,
                        onApprove = { startAssign(it) },
                        onDecline = onDecline,
                        onClose = null,
                        modifier = Modifier.width(320.dp).fillMaxSize(),
                    )
                }
                Box(Modifier.weight(1f).fillMaxSize().background(palette.bg)) {
                    CalendarTimeline(
                        palette = palette,
                        floor = activeFloor,
                        reservations = confirmed,
                        assigningRez = assigningRez,
                        previewTableId = previewTableId,
                        onPickTable = { tableId -> previewTableId = tableId },
                    )
                }
            }
        }

        // Slide-over panel — used on mobile, and also on desktop when assigning (since
        // the side panel is hidden in that case but the user might still want to browse).
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
            CalendarPanel(
                palette = palette,
                pending = pending,
                confirmed = confirmed,
                onApprove = { startAssign(it) },
                onDecline = { onDecline(it) },
                onClose = { panelOpen = false },
                modifier = Modifier.fillMaxHeight().width(if (isMobile) 320.dp else 360.dp),
            )
        }
    }
}

@Composable
private fun AssignBanner(
    palette: FloorPalette,
    reservation: Reservation,
    previewTableLabel: String?,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val confirmPhase = previewTableLabel != null
    Row(
        Modifier
            .fillMaxWidth()
            .background(Blue500.copy(alpha = 0.08f))
            .border(1.dp, Blue500.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.GpsFixed,
            contentDescription = null,
            tint = Blue600,
            modifier = Modifier.size(16.dp),
        )
        Column(Modifier.weight(1f)) {
            if (confirmPhase) {
                Text(
                    "Confirm ${reservation.guestName} → Table $previewTableLabel",
                    color = palette.text1,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            } else {
                Text(
                    "Assign ${reservation.guestName} (${reservation.partySize}P, ${reservation.startTime})",
                    color = palette.text1,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Click a highlighted table row",
                    color = palette.text2,
                    fontSize = 11.sp,
                )
            }
        }
        if (confirmPhase) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Blue600)
                    .clickable(onClick = onConfirm)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(5.dp))
                Text("Confirm", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Box(
            Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(onClick = onCancel),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Close, contentDescription = "Cancel", tint = palette.text2, modifier = Modifier.size(16.dp))
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
        Box(
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Menu, contentDescription = "Reservations", tint = palette.text2, modifier = Modifier.size(20.dp))
            if (pendingCount > 0) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 2.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Amber500),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$pendingCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
    onApprove: (Reservation) -> Unit,
    onDecline: (Reservation) -> Unit,
    onClose: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier.background(palette.card).border(1.dp, palette.border)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Reservations", color = palette.text1, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.weight(1f))
            if (onClose != null) {
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = palette.text2, modifier = Modifier.size(16.dp))
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
        SectionHeader(palette, "Requests", pending.size, Amber500)
        LazyColumn(Modifier.weight(1f)) {
            items(pending) { reservation ->
                RequestCard(
                    palette = palette,
                    reservation = reservation,
                    onApprove = { onApprove(reservation) },
                    onDecline = { onDecline(reservation) },
                )
            }
            item { SectionHeader(palette, "Confirmed", confirmed.size, Green500) }
            items(confirmed) { reservation -> ConfirmedRow(palette, reservation) }
            if (pending.isEmpty() && confirmed.isEmpty()) {
                item {
                    Text(
                        "No reservations on this day.",
                        color = palette.text3,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(palette: FloorPalette, title: String, count: Int, dotColor: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.width(8.dp))
        Text(title, color = palette.text1, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Box(
            Modifier
                .clip(CircleShape)
                .background(dotColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text("$count", color = dotColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RequestCard(
    palette: FloorPalette,
    reservation: Reservation,
    onApprove: () -> Unit,
    onDecline: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(palette.raised)
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(Amber500))
            Spacer(Modifier.width(8.dp))
            Text(reservation.guestName, color = palette.text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Row(
                Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .background(Blue500.copy(alpha = 0.12f))
                    .clickable(onClick = onApprove)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Blue600, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(4.dp))
                Text("Approve", color = Blue600, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onDecline),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Close, contentDescription = "Decline", tint = palette.text3, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipText(palette, dayLabel(reservation.dayOffset))
            ChipText(palette, "⏱ ${reservation.startTime}")
            ChipText(palette, "${reservation.partySize}P")
            ChipText(palette, formatHours(reservation.durationHours))
            if (reservation.tableId.isNotBlank()) {
                ChipText(palette, "Table ${reservation.tableId.removePrefix("T")}", emphasize = true)
            }
        }
    }
}

@Composable
private fun ConfirmedRow(palette: FloorPalette, reservation: Reservation) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(Green500))
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(reservation.guestName, color = palette.text1, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
                "${reservation.startTime} · ${reservation.partySize}P · ${formatHours(reservation.durationHours)}",
                color = palette.text2,
                fontSize = 11.sp,
            )
        }
        if (reservation.tableId.isNotBlank()) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Blue500.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text("Table ${reservation.tableId.removePrefix("T")}", color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ChipText(palette: FloorPalette, text: String, emphasize: Boolean = false) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (emphasize) Blue500.copy(alpha = 0.12f) else palette.card)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text,
            color = if (emphasize) Blue600 else palette.text2,
            fontSize = 11.sp,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun CalendarTimeline(
    palette: FloorPalette,
    floor: Floor,
    reservations: List<Reservation>,
    assigningRez: Reservation? = null,
    previewTableId: String? = null,
    onPickTable: (String) -> Unit = {},
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
                    Text("${hour.toString().padStart(2, '0')}:00", color = palette.text2, fontSize = 12.sp)
                }
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            items(floor.tables) { table ->
                val isPreview = previewTableId == table.id
                val canPick = assigningRez != null &&
                    previewTableId == null &&
                    table.seats >= assigningRez.partySize
                val dimmed = assigningRez != null && previewTableId != null && !isPreview
                val rowBg = when {
                    isPreview -> Blue500.copy(alpha = 0.10f)
                    canPick -> Blue500.copy(alpha = 0.05f)
                    else -> Color.Transparent
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(rowBg)
                        .border(1.dp, palette.border.copy(alpha = 0.4f))
                        .then(if (canPick) Modifier.clickable { onPickTable(table.id) } else Modifier)
                        .horizontalScroll(hScroll),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .width(96.dp)
                            .height(40.dp)
                            .background(
                                when {
                                    isPreview -> Blue600
                                    canPick -> Blue500.copy(alpha = 0.15f)
                                    else -> palette.card
                                },
                            )
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                table.label,
                                color = when {
                                    isPreview -> Color.White
                                    canPick -> Blue600
                                    dimmed -> palette.text3
                                    else -> palette.text1
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "(${table.seats})",
                                color = when {
                                    isPreview -> Color.White.copy(alpha = 0.8f)
                                    canPick -> Blue600.copy(alpha = 0.7f)
                                    dimmed -> palette.text3
                                    else -> palette.text2
                                },
                                fontSize = 11.sp,
                            )
                            if (canPick) {
                                Spacer(Modifier.width(4.dp))
                                Box(Modifier.size(6.dp).clip(CircleShape).background(Blue500))
                            }
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
                        // Existing confirmed reservations on this table.
                        reservations.filter { it.tableId == table.id }.forEach { reservation ->
                            ReservationBlock(reservation = reservation, dimmed = dimmed)
                        }
                        // Dashed preview placement once the user has picked this table.
                        if (isPreview && assigningRez != null) {
                            ReservationPreviewBlock(reservation = assigningRez)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationBlock(reservation: Reservation, dimmed: Boolean) {
    val color = reservationColor(reservation)
    val startHour = reservation.startTime.substringBefore(":").toIntOrNull() ?: 0
    val startMin = reservation.startTime.substringAfter(":", "0").toIntOrNull() ?: 0
    val offsetHours = (startHour - Hours.first()).coerceAtLeast(0)
    val offsetXdp = (offsetHours * 72) + ((startMin / 60f) * 72f).toInt()
    val widthDp = (reservation.durationHours * 72).toInt()
    val alpha = if (dimmed) 0.4f else 1f
    Box(
        Modifier
            .padding(start = offsetXdp.dp, top = 4.dp, bottom = 4.dp)
            .width(widthDp.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f * alpha))
            .border(1.dp, color.copy(alpha = alpha), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            "${reservation.guestName} · ${reservation.startTime}",
            color = color.copy(alpha = if (dimmed) 0.6f else 1f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
private fun ReservationPreviewBlock(reservation: Reservation) {
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
            .background(Blue500.copy(alpha = 0.10f))
            .dashedBorder(color = Blue600, strokeWidth = 1.5.dp, cornerRadius = 8.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            reservation.guestName,
            color = Blue600,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

private fun Modifier.dashedBorder(color: Color, strokeWidth: Dp, cornerRadius: Dp): Modifier =
    this.drawBehind {
        val w = strokeWidth.toPx()
        val r = cornerRadius.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(w / 2, w / 2),
            size = Size(size.width - w, size.height - w),
            cornerRadius = CornerRadius(r, r),
            style = Stroke(
                width = w,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f), 0f),
            ),
        )
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

private fun formatHours(hours: Double): String {
    val intPart = hours.toInt()
    val frac = hours - intPart
    return if (frac == 0.0) "${intPart}h" else "${intPart}.${(frac * 10).toInt()}h"
}
