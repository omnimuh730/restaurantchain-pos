package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
internal fun EditSidebar(palette: FloorPalette, state: FloorPlanState, modifier: Modifier = Modifier) {
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
            Text(stringResource(R.string.floor_edit_add_table), color = palette.editText2, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
        val sel = state.selectedTable
        if (sel != null) {
            TableInspector(palette, sel, state)
        } else {
            Text(stringResource(R.string.floor_edit_tap_table), color = palette.editText3, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
