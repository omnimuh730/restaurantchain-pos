package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.TableStatus
import com.mh.restaurantchainpos.pos.ui.i18n.floorDisplayTitle
import com.mh.restaurantchainpos.pos.ui.i18n.ordersMenuLineTitle
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.orders.floorTableDisplayLine
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
fun TableDrawer(
    palette: FloorPalette,
    table: FloorTable?,
    onClose: () -> Unit,
    onPay: (FloorTable) -> Unit,
) {
    val isMobile = rememberIsMobile()
    val open = table != null
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = open, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose,
                    ),
            )
        }
        if (isMobile) {
            AnimatedVisibility(
                visible = open,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                table?.let { DrawerSheet(palette, it, onClose, onPay, mobile = true) }
            }
        } else {
            AnimatedVisibility(
                visible = open,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                table?.let { DrawerSheet(palette, it, onClose, onPay, mobile = false) }
            }
        }
    }
}

@Composable
private fun DrawerSheet(
    palette: FloorPalette,
    table: FloorTable,
    onClose: () -> Unit,
    onPay: (FloorTable) -> Unit,
    mobile: Boolean,
) {
    val sheetShape = if (mobile) RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp) else RoundedCornerShape(0.dp)
    val sheetModifier = if (mobile) {
        Modifier
            .fillMaxWidth()
            .heightIn(max = 640.dp)
    } else {
        Modifier
            .fillMaxHeight()
            .width(360.dp)
    }
    Column(
        sheetModifier
            .clip(sheetShape)
            .background(palette.card)
            .border(1.dp, palette.border, sheetShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
    ) {
        if (mobile) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(palette.text3),
                )
            }
        }
        Header(palette, table, onClose)
        Divider(palette)
        Box(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
            when (table.status) {
                TableStatus.Occupied -> OccupiedBody(palette, table)
                TableStatus.Reserved -> ReservedBody(palette, table)
                TableStatus.Available -> AvailableBody(palette)
            }
        }
        Divider(palette)
        Footer(palette, table, onPay)
        if (mobile) Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun Header(palette: FloorPalette, table: FloorTable, onClose: () -> Unit) {
    val ctx = LocalContext.current
    Row(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(Modifier.weight(1f)) {
            Text(ctx.floorTableDisplayLine(table, editMode = false), color = palette.text1, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.floor_seats_count, table.seats), color = palette.text2, fontSize = 12.sp)
                Text("|", color = palette.text3, fontSize = 12.sp)
                Text(stringResource(R.string.floor_party_seated_minutes, 26), color = palette.text2, fontSize = 12.sp)
                if (table.status != TableStatus.Available) {
                    Text("|", color = palette.text3, fontSize = 12.sp)
                    val statusColor = if (table.status == TableStatus.Occupied) {
                        palette.occupiedText
                    } else {
                        palette.reservedText
                    }
                    Text(table.status.floorDisplayTitle(), color = statusColor, fontSize = 12.sp)
                }
            }
        }
        Text("✕", color = palette.text3, fontSize = 16.sp, modifier = Modifier.clickable(onClick = onClose))
    }
}

@Composable
private fun OccupiedBody(palette: FloorPalette, table: FloorTable) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        table.orderItems.forEach { item ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("${item.qty}×", color = palette.text2, fontSize = 12.sp, modifier = Modifier.width(28.dp))
                Text(ordersMenuLineTitle(item.nameKey), color = palette.text1, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("₩%,d".format(item.price * item.qty), color = palette.text2, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ReservedBody(palette: FloorPalette, table: FloorTable) {
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReservedRow(palette, stringResource(R.string.floor_reserved_label_guest), table.guestName)
        ReservedRow(palette, stringResource(R.string.floor_reserved_label_time), table.reservationTime)
        ReservedRow(palette, stringResource(R.string.floor_reserved_label_party), "${table.seats}")
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.floor_reservation_qr_heading), color = palette.text3, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            QrPlaceholder(seed = table.id, size = 156.dp)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.floor_reservation_qr_hint), color = palette.text3, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ReservedRow(palette: FloorPalette, label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, color = palette.text2, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value.ifBlank { stringResource(R.string.floor_empty_dash) }, color = palette.text1, fontSize = 13.sp)
    }
}

@Composable
private fun AvailableBody(palette: FloorPalette) {
    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.floor_no_active_orders), color = palette.text3, fontSize = 13.sp)
    }
}

@Composable
private fun Footer(palette: FloorPalette, table: FloorTable, onPay: (FloorTable) -> Unit) {
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (table.status == TableStatus.Occupied) {
            Row(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.floor_order_total), color = palette.text1, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("₩%,d".format(table.revenue), color = palette.text1, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            FooterButton(stringResource(R.string.floor_payment_confirm), Blue500) { onPay(table) }
        }
        if (table.status == TableStatus.Available) {
            FooterButton(stringResource(R.string.floor_seat_guest), Blue500) {}
        }
        if (table.status == TableStatus.Reserved) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { FooterButton(stringResource(R.string.floor_check_in), Blue500) {} }
                Box(Modifier.weight(1f)) {
                    FooterButton(stringResource(R.string.common_cancel), palette.raised, contentColor = palette.text1, border = palette.border) {}
                }
            }
        }
    }
}

@Composable
private fun FooterButton(
    label: String,
    background: Color,
    contentColor: Color = Color.White,
    border: Color = background,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = contentColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun Divider(palette: FloorPalette) {
    Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
}

/**
 * Lightweight pseudo-QR (not a real scannable code). The seed deterministically
 * fills cells so it looks like a QR pattern in the design without pulling in a
 * full QR library — the React side already shows a static QR for demo data.
 */
@Composable
private fun QrPlaceholder(seed: String, size: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(10.dp),
    ) {
        Canvas(Modifier.matchParentSize()) {
            val cells = 25
            val cell = this.size.width / cells
            val seedHash = seed.hashCode().toLong()
            var rng = if (seedHash == 0L) 1L else seedHash
            for (y in 0 until cells) {
                for (x in 0 until cells) {
                    rng = rng * 6364136223846793005L + 1442695040888963407L
                    val on = ((rng ushr 33).toInt() and 1) == 1
                    val edge = (x < 7 && y < 7) || (x >= cells - 7 && y < 7) || (x < 7 && y >= cells - 7)
                    if (on || edge) {
                        drawRect(
                            color = Color(0xFF111111),
                            topLeft = Offset(x * cell, y * cell),
                            size = Size(cell, cell),
                        )
                    }
                }
            }
        }
    }
}
