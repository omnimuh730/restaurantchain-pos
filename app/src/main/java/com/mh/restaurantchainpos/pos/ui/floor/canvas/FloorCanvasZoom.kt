package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import kotlin.math.roundToInt

internal const val MaxFloorZoom = 3f
internal const val ZoomStep = 0.1f
internal const val FloorContentGutterDp = 24
internal fun nextZoom(zoom: Float, delta: Float, minZoom: Float): Float =
    (zoom + delta).coerceIn(minZoom, MaxFloorZoom)

@Composable
internal fun ZoomControls(
    palette: FloorPalette,
    zoom: Float,
    minZoom: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val zoomOutLabel = stringResource(R.string.floor_cd_zoom_out)
    val zoomInLabel = stringResource(R.string.floor_cd_zoom_in)
    Row(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.editBorder.copy(alpha = 0.32f))
            .border(1.dp, palette.editBorder.copy(alpha = 0.38f), RoundedCornerShape(10.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        ZoomButton(
            enabled = zoom > minZoom + 0.001f,
            palette = palette,
            icon = Icons.Outlined.Remove,
            contentDescription = zoomOutLabel,
            onClick = { onZoomChange(nextZoom(zoom, -ZoomStep, minZoom)) },
        )
        Text(
            "${(zoom * 100).roundToInt()}%",
            color = palette.text2,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(50.dp),
        )
        ZoomButton(
            enabled = zoom < MaxFloorZoom - 0.001f,
            palette = palette,
            icon = Icons.Outlined.Add,
            contentDescription = zoomInLabel,
            onClick = { onZoomChange(nextZoom(zoom, ZoomStep, minZoom)) },
        )
    }
}

@Composable
internal fun ZoomButton(
    enabled: Boolean,
    palette: FloorPalette,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(if (enabled) Color.White.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.32f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) palette.editText1 else palette.editText3,
            modifier = Modifier.size(15.dp),
        )
    }
}
