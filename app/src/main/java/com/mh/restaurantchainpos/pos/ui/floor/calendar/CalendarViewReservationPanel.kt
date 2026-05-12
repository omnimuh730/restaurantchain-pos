package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.outlined.ExpandMore
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.ui.orders.tableOrderLabel
import com.mh.restaurantchainpos.pos.ui.theme.Blue400
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.Red500

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
    var requestsOpen by remember { mutableStateOf(true) }
    var confirmedOpen by remember { mutableStateOf(true) }

    Column(modifier.background(palette.card).border(1.dp, palette.border)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.floor_reservations_title),
                color = palette.text1,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f),
            )
            onClose?.let { IconCircleButton(Icons.Outlined.Close, palette, it) }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))

        LazyColumn(Modifier.weight(1f)) {
            item("requests-header") {
                CollapsibleSectionHeader(
                    palette = palette,
                    title = stringResource(R.string.floor_res_section_requests),
                    count = pending.size,
                    dotColor = Blue400,
                    fill = Blue500.copy(alpha = 0.12f),
                    textColor = Blue600,
                    expanded = requestsOpen,
                    onToggle = { requestsOpen = !requestsOpen },
                )
            }
            if (requestsOpen) {
                items(pending, key = { "p-${it.id}" }) { reservation ->
                    RequestCard(
                        palette = palette,
                        reservation = reservation,
                        onApprove = { onApprove(reservation) },
                        onDecline = { onDecline(reservation) },
                    )
                }
                if (pending.isEmpty()) {
                    item("no-requests") {
                        EmptyHint(palette, stringResource(R.string.floor_res_no_pending_requests))
                    }
                }
            }

            item("confirmed-header") {
                CollapsibleSectionHeader(
                    palette = palette,
                    title = stringResource(R.string.floor_res_section_confirmed),
                    count = confirmed.size,
                    dotColor = palette.occupiedBorder,
                    fill = palette.occupiedFill,
                    textColor = palette.occupiedText,
                    expanded = confirmedOpen,
                    onToggle = { confirmedOpen = !confirmedOpen },
                )
            }
            if (confirmedOpen) {
                items(confirmed, key = { "c-${it.id}" }) { reservation ->
                    ConfirmedRow(palette = palette, reservation = reservation, onAssign = { onAssign(reservation) })
                }
                if (confirmed.isEmpty()) {
                    item("no-confirmed") {
                        EmptyHint(palette, stringResource(R.string.floor_res_no_confirmed))
                    }
                }
            }
        }
    }
}

@Composable
internal fun CollapsibleSectionHeader(
    palette: FloorPalette,
    title: String,
    count: Int,
    dotColor: Color,
    fill: Color,
    textColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        animationSpec = tween(180),
        label = "section-chevron",
    )
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.ExpandMore,
            contentDescription = if (expanded) stringResource(R.string.floor_res_cd_collapse) else stringResource(R.string.floor_res_cd_expand),
            tint = palette.text2,
            modifier = Modifier.size(16.dp).rotate(rotation),
        )
        Spacer(Modifier.width(6.dp))
        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            color = palette.text1,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Box(Modifier.clip(CircleShape).background(fill).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(count.toString(), color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun SectionHeader(palette: FloorPalette, title: String, count: Int, dotColor: Color, fill: Color) {
    // Kept for backwards compatibility with any external callers.
    CollapsibleSectionHeader(
        palette = palette,
        title = title,
        count = count,
        dotColor = dotColor,
        fill = fill,
        textColor = dotColor,
        expanded = true,
        onToggle = {},
    )
}

@Composable
internal fun RequestCard(
    palette: FloorPalette,
    reservation: Reservation,
    onApprove: () -> Unit,
    onDecline: () -> Unit,
) {
    val ctx = LocalContext.current
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
            Box(Modifier.size(8.dp).clip(CircleShape).background(Blue400))
            Spacer(Modifier.width(8.dp))
            Text(
                reservation.guestName,
                color = palette.text1,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipText(palette, ctx.dayLabelForCalendarOffset(reservation.dayOffset))
            ChipText(palette, reservation.startTime)
            ChipText(palette, ctx.getString(R.string.floor_cal_party_size, reservation.partySize))
            ChipText(palette, ctx.formatReservationDurationHours(reservation.durationHours))
            if (reservation.tableId.isNotBlank()) {
                ChipText(palette, ctx.tableOrderLabel(reservation.tableId), emphasize = true)
            }
        }
        Spacer(Modifier.height(10.dp))
        // Operators must be able to both approve AND decline a request, so we
        // render two equal-weight, labelled buttons (the previous icon-only
        // close was easy to miss).
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Blue600)
                    .clickable(onClick = onApprove)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(5.dp))
                Text(stringResource(R.string.floor_res_approve), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Red500.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable(onClick = onDecline)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Outlined.Close, contentDescription = null, tint = Red500, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(5.dp))
                Text(stringResource(R.string.floor_res_decline), color = Red500, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
internal fun ConfirmedRow(palette: FloorPalette, reservation: Reservation, onAssign: () -> Unit) {
    val ctx = LocalContext.current
    val partyLabel = ctx.getString(R.string.floor_cal_party_size, reservation.partySize)
    val duration = ctx.formatReservationDurationHours(reservation.durationHours)
    val subline = ctx.getString(R.string.floor_res_confirmed_subline, reservation.startTime, partyLabel, duration)
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
            Text(subline, color = palette.text2, fontSize = 11.sp)
        }
        if (reservation.tableId.isNotBlank()) {
            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Blue500.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                Text(ctx.tableOrderLabel(reservation.tableId), color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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

@Composable
private fun EmptyHint(palette: FloorPalette, message: String) {
    Text(
        message,
        color = palette.text3,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}
