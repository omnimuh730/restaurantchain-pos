package com.mh.restaurantchainpos.pos.ui.floor

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.TableStatus
import com.mh.restaurantchainpos.pos.ui.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
fun TableCardView(
    palette: FloorPalette,
    tables: List<FloorTable>,
    onSelect: (FloorTable) -> Unit,
) {
    val isMobile = rememberIsMobile()
    val cols = if (isMobile) 2 else 4
    val pad = if (isMobile) 12.dp else 24.dp
    LazyVerticalGrid(
        columns = GridCells.Fixed(cols),
        modifier = Modifier.fillMaxSize().background(palette.editBg).padding(pad),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(tables) { table -> TableCard(palette, table, onSelect) }
    }
}

@Composable
private fun TableCard(palette: FloorPalette, table: FloorTable, onSelect: (FloorTable) -> Unit) {
    val occupied = table.status == TableStatus.Occupied
    val reserved = table.status == TableStatus.Reserved
    val (bg, borderC, textC) = when {
        occupied -> Triple(Blue500, Color(0xFF3370E8), Color.White)
        reserved -> Triple(palette.editTableDefault, palette.reservedBorder, palette.editText1)
        else -> Triple(palette.editTableDefault, palette.border, palette.editText1)
    }
    val borderW = if (occupied) 2.dp else 1.dp
    Column(
        Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(borderW, borderC, RoundedCornerShape(10.dp))
            .clickable { onSelect(table) }
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(table.label, color = textC, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(2.dp))
        Text(
            if (occupied) "${if (table.occupiedSeats > 0) table.occupiedSeats else table.seats}/${table.seats} seats"
            else "${table.seats} seats",
            color = if (occupied) Color.White.copy(alpha = 0.85f) else palette.editText2,
            fontSize = 12.sp,
        )
        Spacer(Modifier.weight(1f))
        when {
            occupied && table.revenue > 0 -> Text(
                "₩%,d".format(table.revenue),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.End),
            )
            reserved && table.reservationTime.isNotBlank() -> Text(
                "Reserved · ${table.reservationTime}",
                color = palette.reservedText,
                fontSize = 11.sp,
            )
            else -> Box(
                Modifier
                    .clip(CircleShape)
                    .background(palette.availableFill)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text("Available", color = palette.availableText, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
