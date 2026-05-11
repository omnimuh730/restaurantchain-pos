package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.FloorMetrics
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.TableShape
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

/**
 * Edit-mode chrome wrapping `FloorCanvas`. On tablet/desktop the inspector
 * lives in a left rail; on mobile it slides up as a bottom sheet (mirrors the
 * React `mobileEditDrawer`). Selecting a table while on mobile auto-opens the
 * sheet, and tapping the canvas background dismisses it.
 */
@Composable
fun FloorEditMode(
    palette: FloorPalette,
    state: FloorPlanState,
) {
    val isMobile = rememberIsMobile()
    var mobileDrawerOpen by remember { mutableStateOf(false) }

    LaunchedEffect(state.selectedTableId, isMobile) {
        if (isMobile && state.selectedTableId != null) mobileDrawerOpen = true
    }

    Column(Modifier.fillMaxSize().background(palette.editBg)) {
        EditTopBar(palette, state, isMobile)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            Row(Modifier.fillMaxSize()) {
                if (!isMobile) {
                    EditSidebar(palette, state, Modifier.width(232.dp).fillMaxHeight())
                }
                FloorCanvas(
                    palette = palette,
                    tables = state.tables,
                    editMode = true,
                    selectedTableId = state.selectedTableId,
                    showSeats = state.showSeats,
                    zoom = state.zoom,
                    onZoomChange = { state.zoom = it },
                    onSelectTable = {
                        state.selectedTableId = it
                        if (isMobile) mobileDrawerOpen = it != null
                    },
                    onDragTable = { id, x, y, commit ->
                        // FloorCanvas already snapped + clamped, so `x` / `y` are
                        // the final canvas coordinates. During the drag we mutate
                        // tables without committing history; on release we commit
                        // a single history entry for the whole gesture.
                        if (commit) {
                            state.updateTables(commit = true) { list -> list }
                        } else {
                            state.updateTables(commit = false) { list ->
                                list.map { if (it.id == id) it.copy(x = x, y = y) else it }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            if (isMobile) {
                val fabBottom: androidx.compose.ui.unit.Dp by animateDpAsState(
                    targetValue = if (mobileDrawerOpen) 372.dp else 48.dp,
                    animationSpec = tween(durationMillis = 240),
                    label = "fab-bottom",
                )
                MobileFab(
                    onClick = { state.addTable() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = fabBottom),
                )
                MobileEditDrawer(
                    palette = palette,
                    state = state,
                    open = mobileDrawerOpen,
                    onToggle = { mobileDrawerOpen = !mobileDrawerOpen },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun MobileFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Blue500)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text("+", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MobileEditDrawer(
    palette: FloorPalette,
    state: FloorPlanState,
    open: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerHeight: androidx.compose.ui.unit.Dp by animateDpAsState(
        targetValue = if (open) 360.dp else 28.dp,
        animationSpec = tween(durationMillis = 240),
        label = "drawer-height",
    )
    val sheetShape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    Column(
        modifier
            .fillMaxWidth()
            .height(drawerHeight)
            .clip(sheetShape)
            .background(palette.editCanvas)
            .border(1.dp, palette.editBorder, sheetShape),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(palette.editText3),
            )
        }
        if (open) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Transparent)
                        .border(2.dp, palette.editBorder, RoundedCornerShape(10.dp))
                        .clickable { state.addTable() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("+ Add table", color = palette.editText2, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                val sel = state.selectedTable
                if (sel != null) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.editCanvas)
                            .border(1.dp, palette.editBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        BasicTextField(
                            value = sel.label,
                            onValueChange = { v -> state.updateSelected { it.copy(label = v) } },
                            singleLine = true,
                            textStyle = TextStyle(color = palette.editText1, fontSize = 14.sp),
                            cursorBrush = SolidColor(Blue500),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        InspectorBlock(palette, "Seats") {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircleStepper(palette, "−") { state.updateSelected { it.copy(seats = (it.seats - 1).coerceAtLeast(1)) } }
                                Text(sel.seats.toString(), color = palette.editText1, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                CircleStepper(palette, "+") { state.updateSelected { it.copy(seats = it.seats + 1) } }
                            }
                        }
                        InspectorBlock(palette, "Shape") {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(TableShape.Rect, TableShape.Circle).forEach { shape ->
                                    val active = sel.shape == shape
                                    Box(
                                        Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (active) Blue500.copy(alpha = 0.12f) else Color.Transparent)
                                            .border(2.dp, if (active) Blue500 else palette.editBorder, RoundedCornerShape(8.dp))
                                            .clickable { state.updateSelected { it.copy(shape = shape) } },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Box(
                                            Modifier
                                                .size(width = 18.dp, height = if (shape == TableShape.Rect) 13.dp else 18.dp)
                                                .clip(if (shape == TableShape.Circle) CircleShape else RoundedCornerShape(3.dp))
                                                .background(if (active) Blue500 else palette.editTableDefault),
                                        )
                                    }
                                }
                            }
                        }
                        InspectorBlock(palette, "Size") {
                            val cols = (sel.width / FloorMetrics.BaseUnit).coerceIn(1, 3)
                            val rows = (sel.height / FloorMetrics.BaseUnit).coerceIn(1, 3)
                            SizeMatrixPicker(cols = cols, rows = rows, palette = palette) { c, r ->
                                state.updateSelected {
                                    it.copy(width = c * FloorMetrics.BaseUnit, height = r * FloorMetrics.BaseUnit)
                                }
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton("Copy", palette.editText1, palette.editCanvas, palette.editBorder, Modifier.weight(1f)) {
                            state.duplicateTable(sel.id)
                        }
                        ActionButton("Delete", Color(0xFFEF4444), Color(0x14EF4444), Color(0x33EF4444), Modifier.weight(1f)) {
                            state.deleteTable(sel.id)
                        }
                    }
                } else {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Tap a table to edit",
                            color = palette.editText3,
                            fontSize = 12.sp,
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun EditTopBar(palette: FloorPalette, state: FloorPlanState, isMobile: Boolean) {
    val gap = if (isMobile) 8.dp else 12.dp
    val padH = if (isMobile) 12.dp else 16.dp
    Row(
        Modifier
            .fillMaxWidth()
            .height(if (isMobile) 52.dp else 56.dp)
            .background(palette.editCanvas)
            .border(1.dp, palette.editBorder)
            .padding(horizontal = padH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable {
                    state.editMode = false
                    state.selectedTableId = null
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("✕", color = palette.editText1, fontSize = 16.sp)
        }
        Text(
            state.activeFloor.name,
            color = palette.editText1,
            fontWeight = FontWeight.SemiBold,
            fontSize = if (isMobile) 13.sp else 14.sp,
        )
        if (!isMobile) Text("|", color = palette.editText3)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (!isMobile) Text("Show seats", color = palette.editText2, fontSize = 13.sp)
            Box(
                Modifier
                    .size(width = 32.dp, height = 18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (state.showSeats) Blue500 else palette.editBorder)
                    .clickable { state.showSeats = !state.showSeats },
            ) {
                Box(
                    Modifier
                        .size(14.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(if (state.showSeats) Alignment.CenterEnd else Alignment.CenterStart),
                )
            }
        }
        Spacer(Modifier.weight(1f))
        IconToolbar("↶", enabled = state.canUndo, palette = palette) { state.undo() }
        IconToolbar("↷", enabled = state.canRedo, palette = palette) { state.redo() }
        Box(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Blue500)
                .clickable { state.editMode = false; state.selectedTableId = null }
                .padding(horizontal = if (isMobile) 12.dp else 16.dp, vertical = 6.dp),
        ) {
            Text("Save", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun IconToolbar(label: String, enabled: Boolean, palette: FloorPalette, onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (enabled) palette.editText1 else palette.editText3,
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun EditSidebar(palette: FloorPalette, state: FloorPlanState, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(palette.editCanvas)
            .border(1.dp, palette.editBorder)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent)
                .border(2.dp, palette.editBorder, RoundedCornerShape(12.dp))
                .clickable { state.addTable() },
            contentAlignment = Alignment.Center,
        ) {
            Text("+ Add table", color = palette.editText2, fontSize = 13.sp)
        }
        val sel = state.selectedTable
        if (sel != null) {
            TableInspector(palette, sel, state)
        } else {
            Text("Tap a table to edit", color = palette.editText3, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TableInspector(palette: FloorPalette, sel: FloorTable, state: FloorPlanState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(palette.editCanvas)
                .border(1.dp, palette.editBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            BasicTextField(
                value = sel.label,
                onValueChange = { v -> state.updateSelected { it.copy(label = v) } },
                singleLine = true,
                textStyle = TextStyle(color = palette.editText1, fontSize = 14.sp),
                cursorBrush = SolidColor(Blue500),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        InspectorBlock(palette, "Seats") {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CircleStepper(palette, "−") { state.updateSelected { it.copy(seats = (it.seats - 1).coerceAtLeast(1)) } }
                Text(sel.seats.toString(), color = palette.editText1, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                CircleStepper(palette, "+") { state.updateSelected { it.copy(seats = it.seats + 1) } }
            }
        }

        InspectorBlock(palette, "Shape") {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(TableShape.Rect, TableShape.Circle).forEach { shape ->
                    val active = sel.shape == shape
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) Blue500.copy(alpha = 0.12f) else Color.Transparent)
                            .border(2.dp, if (active) Blue500 else palette.editBorder, RoundedCornerShape(8.dp))
                            .clickable { state.updateSelected { it.copy(shape = shape) } },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(width = 22.dp, height = if (shape == TableShape.Rect) 16.dp else 22.dp)
                                .clip(if (shape == TableShape.Circle) CircleShape else RoundedCornerShape(3.dp))
                                .background(if (active) Blue500 else palette.editTableDefault),
                        )
                    }
                }
            }
        }

        InspectorBlock(palette, "Size") {
            val cols = (sel.width / FloorMetrics.BaseUnit).coerceIn(1, 3)
            val rows = (sel.height / FloorMetrics.BaseUnit).coerceIn(1, 3)
            SizeMatrixPicker(cols = cols, rows = rows, palette = palette) { c, r ->
                state.updateSelected {
                    it.copy(width = c * FloorMetrics.BaseUnit, height = r * FloorMetrics.BaseUnit)
                }
            }
        }

        Spacer(Modifier.height(2.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.editBorder))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton("Copy", palette.editText1, palette.editCanvas, palette.editBorder, Modifier.weight(1f)) {
                state.duplicateTable(sel.id)
            }
            ActionButton("Delete", Color(0xFFEF4444), Color(0x14EF4444), Color(0x33EF4444), Modifier.weight(1f)) {
                state.deleteTable(sel.id)
            }
        }
    }
}

@Composable
private fun InspectorBlock(palette: FloorPalette, label: String, content: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = palette.editText2, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun CircleStepper(palette: FloorPalette, label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(1.dp, palette.editBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = palette.editText2, fontSize = 14.sp)
    }
}

@Composable
private fun ActionButton(
    label: String,
    contentColor: Color,
    background: Color,
    border: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = contentColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
