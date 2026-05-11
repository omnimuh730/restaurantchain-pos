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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
internal fun TableInspector(palette: FloorPalette, sel: FloorTable, state: FloorPlanState) {
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
                textStyle = TextStyle(color = palette.editText1, fontSize = 16.sp, fontWeight = FontWeight.Medium),
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
internal fun InspectorBlock(palette: FloorPalette, label: String, content: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = palette.editText2, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
internal fun CircleStepper(palette: FloorPalette, label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(1.dp, palette.editBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = palette.editText2, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun ActionButton(
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
        Text(label, color = contentColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
