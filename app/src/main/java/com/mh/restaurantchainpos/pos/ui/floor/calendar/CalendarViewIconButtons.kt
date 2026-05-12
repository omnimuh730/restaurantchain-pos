package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.Red500

@Composable
internal fun IconCircleButton(icon: ImageVector, palette: FloorPalette, onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(palette.raised)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = palette.text2, modifier = Modifier.size(16.dp))
    }
}

@Composable
internal fun IconTinyButton(
    icon: ImageVector,
    palette: FloorPalette,
    onClick: () -> Unit,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    Box(
        Modifier
            .size(28.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = if (enabled) palette.text2 else palette.text3.copy(alpha = 0.45f), modifier = Modifier.size(15.dp))
    }
}

@Composable
internal fun NowButton(onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Red500.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.floor_cal_now), color = Red500, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
