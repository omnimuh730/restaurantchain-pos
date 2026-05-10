package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.TableStatus
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
fun TableDrawer(
    palette: FloorPalette,
    table: FloorTable,
    onClose: () -> Unit,
    onPay: (FloorTable) -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onClose),
    ) {
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(360.dp)
                .background(palette.card)
                .border(1.dp, palette.border)
                .clickable(enabled = false) {},
        ) {
            Column(Modifier.fillMaxSize()) {
                Header(palette, table, onClose)
                Divider(palette)
                Box(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    when (table.status) {
                        TableStatus.Occupied -> OccupiedBody(palette, table)
                        TableStatus.Reserved -> ReservedBody(palette, table)
                        TableStatus.Available -> AvailableBody(palette)
                    }
                }
                Divider(palette)
                Footer(palette, table, onPay)
            }
        }
    }
}

@Composable
private fun Header(palette: FloorPalette, table: FloorTable, onClose: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(Modifier.weight(1f)) {
            Text(table.label, color = palette.text1, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("👥 ${table.seats} seats", color = palette.text2, fontSize = 12.sp)
                Text("|", color = palette.text3, fontSize = 12.sp)
                Text("⏱ 26m", color = palette.text2, fontSize = 12.sp)
                if (table.status != TableStatus.Available) {
                    Text("|", color = palette.text3, fontSize = 12.sp)
                    val statusColor = when (table.status) {
                        TableStatus.Occupied -> palette.occupiedText
                        TableStatus.Reserved -> palette.reservedText
                        TableStatus.Available -> palette.availableText
                    }
                    Text(table.status.name.lowercase(), color = statusColor, fontSize = 12.sp)
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
                Text(item.name, color = palette.text1, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("₩%,d".format(item.price * item.qty), color = palette.text2, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ReservedBody(palette: FloorPalette, table: FloorTable) {
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReservedRow(palette, "Guest", table.guestName)
        ReservedRow(palette, "Time", table.reservationTime)
        ReservedRow(palette, "Party", "${table.seats}")
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⌗ RESERVATION QR", color = palette.text3, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            QrPlaceholder(seed = table.id, size = 156.dp)
            Spacer(Modifier.height(8.dp))
            Text("Show this QR to your guest to confirm.", color = palette.text3, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ReservedRow(palette: FloorPalette, label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, color = palette.text2, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value.ifBlank { "—" }, color = palette.text1, fontSize = 13.sp)
    }
}

@Composable
private fun AvailableBody(palette: FloorPalette) {
    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text("No active orders", color = palette.text3, fontSize = 13.sp)
    }
}

@Composable
private fun Footer(palette: FloorPalette, table: FloorTable, onPay: (FloorTable) -> Unit) {
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (table.status == TableStatus.Occupied) {
            Row(Modifier.fillMaxWidth()) {
                Text("Order total", color = palette.text1, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("₩%,d".format(table.revenue), color = palette.text1, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            FooterButton("Payment", Blue500) { onPay(table) }
        }
        if (table.status == TableStatus.Available) {
            FooterButton("Seat guest", Blue500) {}
        }
        if (table.status == TableStatus.Reserved) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { FooterButton("Check-in", Blue500) {} }
                Box(Modifier.weight(1f)) {
                    FooterButton("Cancel", palette.raised, contentColor = palette.text1, border = palette.border) {}
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
