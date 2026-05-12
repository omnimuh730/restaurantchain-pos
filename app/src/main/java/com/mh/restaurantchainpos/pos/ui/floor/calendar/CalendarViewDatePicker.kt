package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import java.util.Calendar

@Composable
internal fun CalendarDatePickerPopup(
    palette: FloorPalette,
    selectedOffset: Int,
    pickerMonthOffset: Int,
    reservations: List<Reservation>,
    onMonthOffsetChange: (Int) -> Unit,
    onSelectOffset: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val base = calendarForOffset(pickerMonthOffset)
    val year = base.get(Calendar.YEAR)
    val month = base.get(Calendar.MONTH)
    val monthGrid = remember(year, month) { buildCalendarMonth(year, month) }
    val monthLabel = ctx.calendarMonthYearLabel(base)
    val localeTag = ctx.resources.configuration.locales[0].toLanguageTag()
    val weekdayLabels = remember(localeTag) { ctx.calendarPickerWeekdayInitials() }
    val selectedDate = calendarForOffset(selectedOffset)

    Column(
        modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.card)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, palette, onClick = {
                val prev = calendarForOffset(pickerMonthOffset)
                prev.add(Calendar.MONTH, -1)
                onMonthOffsetChange(offsetForCalendar(prev))
            })
            Text(
                monthLabel,
                color = palette.text1,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowRight, palette, onClick = {
                val next = calendarForOffset(pickerMonthOffset)
                next.add(Calendar.MONTH, 1)
                onMonthOffsetChange(offsetForCalendar(next))
            })
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { label ->
                Box(Modifier.weight(1f).height(22.dp), contentAlignment = Alignment.Center) {
                    Text(label, color = palette.text3, fontSize = 10.sp)
                }
            }
        }
        monthGrid.forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    if (day == null) {
                        Spacer(Modifier.weight(1f).height(34.dp))
                    } else {
                        val offset = offsetForDate(year, month, day)
                        val isSelected = sameYmd(selectedDate, year, month, day)
                        val isToday = offset == 0
                        val hasReservation = reservations.any { it.dayOffset == offset }
                        Box(
                            Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> Blue600
                                        isToday -> Red500.copy(alpha = 0.10f)
                                        else -> Color.Transparent
                                    },
                                )
                                .clickable { onSelectOffset(offset) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                day.toString(),
                                color = when {
                                    isSelected -> Color.White
                                    isToday -> Red500
                                    else -> palette.text1
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            )
                            if (hasReservation && !isSelected) {
                                Box(
                                    Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 3.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Blue600),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
