package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.KitchenFloor
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

private enum class FloorSelectState { None, Partial, All }

@Composable
fun TableFilterSidebar(
    colors: PosColors,
    open: Boolean,
    selectedTables: List<String>,
    onToggleTable: (String) -> Unit,
    onToggleFloor: (KitchenFloor) -> Unit,
    onToggleAll: () -> Unit,
    onClose: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = open,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut(),
        ) {
            Box(Modifier.fillMaxSize().background(Color(0x80000000)).clickable(onClick = onClose))
        }
        AnimatedVisibility(
            visible = open,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
        ) {
            FilterPanel(
                colors = colors,
                selectedTables = selectedTables,
                onToggleAll = onToggleAll,
                onToggleFloor = onToggleFloor,
                onToggleTable = onToggleTable,
                onClose = onClose,
                width = 288.dp,
                showClose = true,
            )
        }
    }
}

/** Static side panel used on tablet/desktop layouts. */
@Composable
fun TableFilterPanel(
    colors: PosColors,
    selectedTables: List<String>,
    onToggleTable: (String) -> Unit,
    onToggleFloor: (KitchenFloor) -> Unit,
    onToggleAll: () -> Unit,
) {
    FilterPanel(
        colors = colors,
        selectedTables = selectedTables,
        onToggleAll = onToggleAll,
        onToggleFloor = onToggleFloor,
        onToggleTable = onToggleTable,
        onClose = {},
        width = 208.dp,
        showClose = false,
    )
}

@Composable
private fun FilterPanel(
    colors: PosColors,
    selectedTables: List<String>,
    onToggleAll: () -> Unit,
    onToggleFloor: (KitchenFloor) -> Unit,
    onToggleTable: (String) -> Unit,
    onClose: () -> Unit,
    width: androidx.compose.ui.unit.Dp,
    showClose: Boolean,
) {
    val floors = PosMockData.kitchenFloors
    val allTables = remember { floors.flatMap { it.tables } }
    val expanded = remember { mutableStateListOf<String>().apply { addAll(floors.map { it.id }) } }
    val allSelected = allTables.all { selectedTables.contains(it) }

    Column(
        Modifier
            .fillMaxHeight()
            .width(width)
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("My Tables", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            if (showClose) {
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✕", color = colors.textMuted, fontSize = 14.sp)
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .background(if (allSelected) Blue500 else Color.Transparent)
                .clickable(onClick = onToggleAll)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "All floors",
                color = if (allSelected) Color.White else colors.text,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                "${selectedTables.size}/${allTables.size}",
                color = if (allSelected) Color(0xCCFFFFFF) else colors.textMuted,
                fontSize = 11.sp,
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        LazyColumn(Modifier.weight(1f)) {
            floors.forEach { floor ->
                val state = floorSelectState(floor, selectedTables)
                val isExpanded = expanded.contains(floor.id)
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (isExpanded) expanded.remove(floor.id) else expanded.add(floor.id)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(if (isExpanded) "▾" else "▸", color = colors.textMuted, fontSize = 12.sp)
                        }
                        Row(
                            Modifier
                                .weight(1f)
                                .clickable { onToggleFloor(floor) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CheckboxBox(state = state, color = Blue500, palette = colors)
                            Text(
                                floor.label,
                                color = if (state == FloorSelectState.None) colors.text else Blue500,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                "${floor.tables.count { selectedTables.contains(it) }}/${floor.tables.size}",
                                color = colors.textMuted,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
                if (isExpanded) {
                    items(floor.tables) { tableId ->
                        val active = selectedTables.contains(tableId)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTable(tableId) }
                                .background(if (active) Blue500.copy(alpha = 0.1f) else Color.Transparent)
                                .padding(start = 48.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (active) Blue500 else Color.Transparent)
                                    .border(2.dp, if (active) Blue500 else colors.border, CircleShape),
                            )
                            Text(tableLabelFor(tableId), color = if (active) Blue500 else colors.textMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckboxBox(state: FloorSelectState, color: Color, palette: PosColors) {
    Box(
        Modifier
            .size(14.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                when (state) {
                    FloorSelectState.None -> Color.Transparent
                    FloorSelectState.Partial -> color.copy(alpha = 0.3f)
                    FloorSelectState.All -> color
                },
            )
            .border(1.5.dp, if (state == FloorSelectState.None) palette.border else color, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            FloorSelectState.All -> Text("✓", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            FloorSelectState.Partial -> Box(
                Modifier
                    .size(width = 6.dp, height = 1.5.dp)
                    .background(color),
            )
            else -> Unit
        }
    }
}

private fun floorSelectState(floor: KitchenFloor, selected: List<String>): FloorSelectState {
    val count = floor.tables.count { selected.contains(it) }
    return when (count) {
        0 -> FloorSelectState.None
        floor.tables.size -> FloorSelectState.All
        else -> FloorSelectState.Partial
    }
}

private fun tableLabelFor(id: String): String {
    val tableMatch = Regex("^T(\\d+)").find(id)
    if (tableMatch != null) return "Table ${tableMatch.groupValues[1]}"
    val barMatch = Regex("^BAR(\\d+)").find(id)
    if (barMatch != null) return "Bar ${barMatch.groupValues[1]}"
    return id
}
