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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material.icons.outlined.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
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
    val controlGap = if (isMobile) 4.dp else 8.dp
    val ctx = LocalContext.current
    Row(
        Modifier
            .fillMaxWidth()
            .background(palette.bg)
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            Modifier
                .weight(1f)
                .padding(end = controlGap),
            horizontalArrangement = Arrangement.spacedBy(controlGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CalendarMenuButton(
                palette = palette,
                requestCount = requestCount,
                onClick = onMenuClick,
            )
            IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, palette, onPreviousDay)
            Row(
                Modifier
                    .widthIn(max = if (isMobile) 132.dp else 230.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onOpenPicker)
                    .padding(horizontal = if (isMobile) 6.dp else 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (isMobile) ctx.calendarShortDateLabel(dayOffset) else ctx.calendarFullDateLabel(dayOffset),
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
                        Text(stringResource(R.string.floor_cal_today), color = Red500, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
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
            if (!isToday && !isMobile) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.raised)
                        .border(1.dp, palette.border, RoundedCornerShape(6.dp))
                        .clickable(onClick = onNow)
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                ) {
                    Text(stringResource(R.string.floor_cal_today), color = palette.text2, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconTinyButton(
                Icons.Outlined.ZoomIn,
                palette,
                onZoomIn,
                enabled = windowHours > MinWindowHours,
                contentDescription = stringResource(R.string.floor_cd_zoom_in),
            )
            NowButton(onNow)
            IconTinyButton(
                Icons.Outlined.ZoomOut,
                palette,
                onZoomOut,
                enabled = windowHours < MaxWindowHours,
                contentDescription = stringResource(R.string.floor_cd_zoom_out),
            )
            if (!isMobile) {
                Spacer(Modifier.width(2.dp))
                Text(stringResource(R.string.floor_cal_hours_window, windowHours.toInt()), color = palette.text3, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun CalendarMenuButton(
    palette: FloorPalette,
    requestCount: Int,
    onClick: () -> Unit,
) {
    // The badge sits in the top-right outside the 34dp circular touch target,
    // so the parent box must NOT clip — only the inner ripple surface is
    // clipped to a circle. Otherwise the badge gets sliced by the circular
    // mask (the bug shown on mobile).
    Box(
        Modifier.size(38.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Menu, contentDescription = stringResource(R.string.floor_cd_calendar_menu), tint = palette.text2, modifier = Modifier.size(20.dp))
        }
        if (requestCount > 0) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Blue500),
                contentAlignment = Alignment.Center,
            ) {
                Text(requestCount.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
