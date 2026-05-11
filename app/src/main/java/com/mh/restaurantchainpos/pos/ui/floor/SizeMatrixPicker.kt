package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

/**
 * 3x3 grid that lets the user pick a (cols, rows) table footprint by tapping a
 * cell. All cells up-and-to-the-left of the tapped cell are highlighted to
 * preview the resulting size — same as the React `SizeMatrixPicker`.
 */
@Composable
fun SizeMatrixPicker(
    cols: Int,
    rows: Int,
    palette: FloorPalette,
    onChange: (cols: Int, rows: Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (r in 1..3) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (c in 1..3) {
                    val active = c <= cols && r <= rows
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (active) palette.editSelected else palette.editTableDefault)
                            .border(
                                1.5.dp,
                                if (active) palette.editSelected else palette.editBorder,
                                RoundedCornerShape(4.dp),
                            )
                            .clickable { onChange(c, r) },
                    )
                }
            }
        }
    }
}
