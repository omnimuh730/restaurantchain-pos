package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
internal fun CalendarPanel(
    palette: FloorPalette,
    pending: List<Reservation>,
    confirmed: List<Reservation>,
    onApprove: (Reservation) -> Unit,
    onAssign: (Reservation) -> Unit,
    onDecline: (Reservation) -> Unit,
    onClose: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier.background(palette.card).border(1.dp, palette.border)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Reservations", color = palette.text1, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.weight(1f))
            onClose?.let {
                IconCircleButton(Icons.Outlined.Close, palette, it)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
        SectionHeader(palette, "Requests", pending.size, palette.reservedBorder, palette.reservedFill)
        LazyColumn(Modifier.weight(1f)) {
            items(pending, key = { it.id }) { reservation ->
                RequestCard(
                    palette = palette,
                    reservation = reservation,
                    onApprove = { onApprove(reservation) },
                    onDecline = { onDecline(reservation) },
                )
            }
            item { SectionHeader(palette, "Confirmed", confirmed.size, palette.occupiedBorder, palette.occupiedFill) }
            items(confirmed, key = { it.id }) { reservation ->
                ConfirmedRow(palette = palette, reservation = reservation, onAssign = { onAssign(reservation) })
            }
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
internal fun SectionHeader(palette: FloorPalette, title: String, count: Int, dotColor: Color, fill: Color) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.width(8.dp))
        Text(title, color = palette.text1, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Box(Modifier.clip(CircleShape).background(fill).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(count.toString(), color = dotColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun RequestCard(
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
            Box(Modifier.size(8.dp).clip(CircleShape).background(palette.reservedBorder))
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
            IconCircleButton(Icons.Outlined.Close, palette, onDecline)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipText(palette, dayLabel(reservation.dayOffset))
            ChipText(palette, reservation.startTime)
            ChipText(palette, "${reservation.partySize}P")
            ChipText(palette, formatHours(reservation.durationHours))
            if (reservation.tableId.isNotBlank()) {
                ChipText(palette, "Table ${reservation.tableId.removePrefix("T")}", emphasize = true)
            }
        }
    }
}

@Composable
internal fun ConfirmedRow(palette: FloorPalette, reservation: Reservation, onAssign: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onAssign)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(palette.occupiedBorder))
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(reservation.guestName, color = palette.text1, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
                "${reservation.startTime} - ${reservation.partySize}P - ${formatHours(reservation.durationHours)}",
                color = palette.text2,
                fontSize = 11.sp,
            )
        }
        if (reservation.tableId.isNotBlank()) {
            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Blue500.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                Text("Table ${reservation.tableId.removePrefix("T")}", color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
internal fun ChipText(palette: FloorPalette, text: String, emphasize: Boolean = false) {
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
