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
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
fun TableCardView(
    palette: FloorPalette,
    tables: List<FloorTable>,
    onSelect: (FloorTable) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = Modifier.fillMaxSize().background(palette.bg).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(tables) { table -> TableCard(palette, table, onSelect) }
    }
}

@Composable
private fun TableCard(palette: FloorPalette, table: FloorTable, onSelect: (FloorTable) -> Unit) {
    val (fill, border, accent) = when (table.status) {
        TableStatus.Occupied -> Triple(palette.occupiedFill, palette.occupiedBorder, palette.occupiedText)
        TableStatus.Reserved -> Triple(palette.reservedFill, palette.reservedBorder, palette.reservedText)
        TableStatus.Available -> Triple(palette.availableFill, palette.availableBorder, palette.availableText)
    }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.card)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .clickable { onSelect(table) }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(border))
            Spacer(Modifier.size(8.dp))
            Text(table.label, color = palette.text1, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(fill)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(table.status.name.lowercase(), color = accent, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        }
        Text(
            "${table.seats} seats · ${table.guestName.ifBlank { "Available" }}",
            color = palette.text2,
            fontSize = 12.sp,
        )
        if (table.status == TableStatus.Occupied && table.revenue > 0) {
            Text("₩%,d".format(table.revenue), color = accent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        if (table.status == TableStatus.Reserved && table.reservationTime.isNotBlank()) {
            Text("Arrives ${table.reservationTime}", color = accent, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}
