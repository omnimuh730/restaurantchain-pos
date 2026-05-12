package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.KitchenFloor
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
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
        width = 224.dp,
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
    width: Dp,
    showClose: Boolean,
) {
    val floors = PosMockData.kitchenFloors
    val allTables = remember { floors.flatMap { it.tables } }
    val expanded = remember { mutableStateListOf<String>().apply { addAll(floors.map { it.id }) } }
    val allSelected = allTables.all { selectedTables.contains(it) }
    val partial = !allSelected && selectedTables.isNotEmpty()
    val allState = when {
        allSelected -> FloorSelectState.All
        partial -> FloorSelectState.Partial
        else -> FloorSelectState.None
    }

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
                .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close filter",
                        tint = colors.textMuted,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        // "All floors" master row — full-width pill with the same checkbox
        // visual the per-floor rows use, so toggling any level reads as the
        // same control.
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (allSelected) Blue500 else Color.Transparent)
                .clickable(onClick = onToggleAll)
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CheckboxBox(
                state = allState,
                color = if (allSelected) Color.White else Blue500,
                contrastBg = if (allSelected) Color.White else Color.Transparent,
                checkColor = if (allSelected) Blue600 else Color.White,
                palette = colors,
            )
            Text(
                "All floors",
                color = if (allSelected) Color.White else colors.text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            CountPill(
                text = "${selectedTables.size}/${allTables.size}",
                onLight = !allSelected,
                colors = colors,
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        LazyColumn(Modifier.weight(1f).padding(vertical = 6.dp)) {
            floors.forEach { floor ->
                val state = floorSelectState(floor, selectedTables)
                val isExpanded = expanded.contains(floor.id)
                val checkedTables = floor.tables.count { selectedTables.contains(it) }
                item("floor-${floor.id}") {
                    FloorRow(
                        colors = colors,
                        label = floor.label,
                        state = state,
                        checkedCount = checkedTables,
                        totalCount = floor.tables.size,
                        isExpanded = isExpanded,
                        onToggleSelect = { onToggleFloor(floor) },
                        onToggleExpand = {
                            if (isExpanded) expanded.remove(floor.id) else expanded.add(floor.id)
                        },
                    )
                }
                if (isExpanded) {
                    items(floor.tables, key = { "table-$it" }) { tableId ->
                        TableRow(
                            colors = colors,
                            label = tableLabelFor(tableId),
                            checked = selectedTables.contains(tableId),
                            onClick = { onToggleTable(tableId) },
                        )
                    }
                    item("floor-${floor.id}-spacer") {
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FloorRow(
    colors: PosColors,
    label: String,
    state: FloorSelectState,
    checkedCount: Int,
    totalCount: Int,
    isExpanded: Boolean,
    onToggleSelect: () -> Unit,
    onToggleExpand: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 0f else -90f,
        animationSpec = tween(180),
        label = "floor-chevron",
    )
    val labelColor = when (state) {
        FloorSelectState.None -> colors.text
        FloorSelectState.Partial -> Blue600
        FloorSelectState.All -> Blue600
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggleSelect)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Chevron has its own click target so users can collapse the floor
        // without un-checking it.
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = onToggleExpand),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = colors.textMuted,
                modifier = Modifier.size(16.dp).rotate(rotation),
            )
        }
        CheckboxBox(
            state = state,
            color = Blue500,
            contrastBg = Color.Transparent,
            checkColor = Color.White,
            palette = colors,
        )
        Text(
            label,
            color = labelColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        CountPill(
            text = "$checkedCount/$totalCount",
            onLight = true,
            colors = colors,
        )
    }
}

@Composable
private fun TableRow(
    colors: PosColors,
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (checked) Blue500.copy(alpha = 0.10f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(start = 38.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Mini-checkbox keeps the visual language consistent with the floor
        // rows above (was previously a confusing radio-style dot).
        CheckboxBox(
            state = if (checked) FloorSelectState.All else FloorSelectState.None,
            color = Blue500,
            contrastBg = Color.Transparent,
            checkColor = Color.White,
            palette = colors,
            size = 14.dp,
            corner = 3.dp,
        )
        Text(
            label,
            color = if (checked) Blue600 else colors.text,
            fontSize = 12.sp,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun CountPill(text: String, onLight: Boolean, colors: PosColors) {
    val bg = if (onLight) colors.surfaceRaised else Color.White.copy(alpha = 0.18f)
    val fg = if (onLight) colors.textMuted else Color.White
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * Tri-state checkbox glyph used across the kitchen filter rows.
 * - [color] drives the border + filled background when fully selected.
 * - [contrastBg] / [checkColor] let an "inverted" caller (the active "All
 *   floors" pill, where the row is already filled blue) show a white box
 *   with a blue check instead of the default blue-on-white.
 */
@Composable
private fun CheckboxBox(
    state: FloorSelectState,
    color: Color,
    contrastBg: Color,
    checkColor: Color,
    palette: PosColors,
    size: Dp = 16.dp,
    corner: Dp = 4.dp,
) {
    val shape = RoundedCornerShape(corner)
    val borderColor = when (state) {
        FloorSelectState.None -> if (contrastBg == Color.Transparent) palette.border else contrastBg
        else -> color
    }
    val fillColor = when (state) {
        FloorSelectState.None -> contrastBg
        FloorSelectState.Partial -> color.copy(alpha = 0.30f)
        FloorSelectState.All -> color
    }
    Box(
        Modifier
            .size(size)
            .clip(shape)
            .background(fillColor)
            .border(1.5.dp, borderColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            FloorSelectState.All -> Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = checkColor,
                modifier = Modifier.size(size - 4.dp),
            )
            FloorSelectState.Partial -> Box(
                Modifier
                    .size(width = size - 6.dp, height = 2.dp)
                    .background(checkColor),
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
