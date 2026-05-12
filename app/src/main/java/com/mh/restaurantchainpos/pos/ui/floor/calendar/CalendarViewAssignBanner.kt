package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
internal fun AssignBanner(
    palette: FloorPalette,
    reservation: Reservation,
    previewTableLabel: String?,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val confirmPhase = previewTableLabel != null
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Blue500.copy(alpha = 0.08f))
            .border(1.dp, Blue500.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Outlined.GpsFixed, contentDescription = null, tint = Blue600, modifier = Modifier.size(16.dp))
        Text(
            if (confirmPhase) {
                stringResource(R.string.floor_assign_banner_confirm, reservation.guestName, previewTableLabel ?: "")
            } else {
                stringResource(
                    R.string.floor_assign_banner_hint,
                    reservation.guestName,
                    reservation.partySize,
                    reservation.startTime,
                )
            },
            color = palette.text1,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (confirmPhase) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Blue600)
                    .clickable(onClick = onConfirm)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(5.dp))
                Text(stringResource(R.string.common_confirm), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        IconCircleButton(Icons.Outlined.Close, palette, onCancel)
    }
}
