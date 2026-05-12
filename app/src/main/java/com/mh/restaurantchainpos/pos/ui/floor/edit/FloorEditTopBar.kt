package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
internal fun EditTopBar(palette: FloorPalette, state: FloorPlanState, isMobile: Boolean) {
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
            fontSize = if (isMobile) 15.sp else 16.sp,
        )
        if (!isMobile) Text("|", color = palette.editText3)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (!isMobile) Text(stringResource(R.string.floor_edit_show_seats), color = palette.editText2, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
            Text(stringResource(R.string.floor_edit_save), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun IconToolbar(label: String, enabled: Boolean, palette: FloorPalette, onClick: () -> Unit) {
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
