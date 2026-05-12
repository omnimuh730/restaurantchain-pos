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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.mh.restaurantchainpos.pos.data.TableShape
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
internal fun MobileFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
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
internal fun MobileEditDrawer(
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
                    Text("+ Add table", color = palette.editText2, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                val sel = state.selectedTable
                if (sel != null) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.editCanvas)
                            .border(1.dp, palette.editBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                    ) {
                        BasicTextField(
                            value = sel.label,
                            onValueChange = { v -> state.updateSelected { it.copy(label = v) } },
                            singleLine = true,
                            textStyle = TextStyle(color = palette.editText1, fontSize = 16.sp, fontWeight = FontWeight.Medium),
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
                                Text(sel.seats.toString(), color = palette.editText1, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}
