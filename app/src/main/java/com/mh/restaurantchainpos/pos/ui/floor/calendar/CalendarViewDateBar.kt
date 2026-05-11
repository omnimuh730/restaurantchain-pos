package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.Red500

@Composable
internal fun CalendarDateBar(
    palette: FloorPalette,
    dayOffset: Int,
    requestCount: Int,
    isToday: Boolean,
    reservationCount: Int,
    windowHours: Float,
    isMobile: Boolean,
    onMenuClick: () -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onOpenPicker: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onNow: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(palette.bg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Menu, contentDescription = "Reservations", tint = palette.text2, modifier = Modifier.size(20.dp))
            if (requestCount > 0) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Amber500),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(requestCount.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, palette, onPreviousDay)
        Row(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onOpenPicker)
                .padding(horizontal = 10.dp, vertical = 7.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = palette.text2, modifier = Modifier.size(if (isMobile) 0.dp else 15.dp))
            if (!isMobile) Spacer(Modifier.width(6.dp))
            Text(
                if (isMobile) shortDateLabel(dayOffset) else fullDateLabel(dayOffset),
                color = palette.text1,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (isToday) {
                Spacer(Modifier.width(7.dp))
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(Red500.copy(alpha = 0.12f))
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text("Today", color = Red500, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            } else if (!isMobile && reservationCount > 0) {
                Spacer(Modifier.width(7.dp))
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(palette.occupiedFill)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text(reservationCount.toString(), color = palette.occupiedText, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowRight, palette, onNextDay)
        Spacer(Modifier.width(6.dp))
        IconTinyButton(Icons.Outlined.Add, palette, onZoomIn, enabled = windowHours > MinWindowHours)
        NowButton(onNow)
        IconTinyButton(Icons.Outlined.Remove, palette, onZoomOut, enabled = windowHours < MaxWindowHours)
        if (!isMobile) {
            Spacer(Modifier.width(4.dp))
            Text("${windowHours.toInt()}h", color = palette.text3, fontSize = 10.sp)
        }
    }
}
