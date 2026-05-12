package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.Blue400
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
internal fun CalendarLegend(palette: FloorPalette) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(palette.bg)
            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        LegendBlock(
            fill = palette.occupiedFill,
            border = palette.occupiedBorder,
            dashed = false,
            label = stringResource(R.string.floor_legend_confirmed),
            palette = palette,
        )
        LegendBlock(
            fill = Blue500.copy(alpha = 0.10f),
            border = Blue400,
            dashed = true,
            label = stringResource(R.string.floor_legend_request),
            palette = palette,
        )
    }
}

@Composable
internal fun LegendBlock(fill: Color, border: Color, dashed: Boolean, label: String, palette: FloorPalette) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        val borderModifier = if (dashed) {
            Modifier.dashedBorder(border, 1.5.dp, 4.dp)
        } else {
            Modifier.border(1.5.dp, border, RoundedCornerShape(4.dp))
        }
        Box(
            Modifier
                .size(width = 16.dp, height = 10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(fill)
                .then(borderModifier),
        )
        Text(label, color = palette.text3, fontSize = 10.sp)
    }
}
