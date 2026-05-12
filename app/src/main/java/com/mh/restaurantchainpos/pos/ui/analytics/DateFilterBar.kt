package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val WeekPagerCenterPage = 260
private const val WeekPagerPageCount = 521

@Composable
fun DateFilterBar(
    period: Period,
    onPeriodChange: (Period) -> Unit,
    range: DateRange?,
    onRangeChange: (DateRange?) -> Unit,
    isDark: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val today = remember { Calendar.getInstance().also { it.zeroTime() } }
    val weekPager = rememberPagerState(
        initialPage = WeekPagerCenterPage,
        pageCount = { WeekPagerPageCount },
    )
    var pickerOpen by remember { mutableStateOf(false) }
    var selectedDate by remember {
        mutableStateOf(Calendar.getInstance().also { it.zeroTime() }.timeInMillis)
    }

    val weekOffset = weekPager.settledPage - WeekPagerCenterPage
    val days = remember(today, weekOffset) { buildWeekDays(today, weekOffset) }

    LaunchedEffect(selectedDate) {
        val delta = weekPageDeltaForSelectedDay(selectedDate, today)
        val target = (WeekPagerCenterPage + delta).coerceIn(0, WeekPagerPageCount - 1)
        if (weekPager.settledPage != target) {
            weekPager.scrollToPage(target)
        }
    }

    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val pillBg = if (isDark) Color(0xFF1F2937) else Color.White

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = weekPager,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) { page ->
            val off = page - WeekPagerCenterPage
            val visibleDays = remember(today, off) { buildWeekDays(today, off) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 6.dp),
            ) {
                visibleDays.forEach { d ->
                    val isToday = d.sameDayAs(today)
                    val isSelected = d.timeInMillis == selectedDate
                    val isFuture = d.timeInMillis > today.timeInMillis
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isToday) text1 else Color.Transparent),
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isFuture -> Color.Transparent
                                        isSelected -> Blue600
                                        else -> pillBg
                                    },
                                )
                                .then(
                                    if (isFuture) {
                                        Modifier
                                    } else {
                                        Modifier.clickable {
                                            selectedDate = d.timeInMillis
                                            val start = d.timeInMillis
                                            val end = d.timeInMillis + HistoryData.DAY - 1
                                            onRangeChange(DateRange(start, end))
                                            onPeriodChange(if (isToday) Period.Today else Period.Custom)
                                        }
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (off == 0) weekdayShort(d) else dayOfMonth(d),
                                color = when {
                                    isFuture -> if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1)
                                    isSelected -> Color.White
                                    else -> text2
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }

        Box(
            Modifier
                .padding(top = 4.dp)
                .clip(CircleShape)
                .background(if (period == Period.Custom && rangeMatchesNonDay(range, days, today)) Blue600 else pillBg)
                .clickable { pickerOpen = true }
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            val active = period == Period.Custom && rangeMatchesNonDay(range, days, today)
            val labelColor = if (active) Color.White else text2
            val label = when {
                range != null && range.startMs != range.endMs - HistoryData.DAY + 1 ->
                    "${shortDate(range.startMs)} - ${shortDate(range.endMs)}"
                selectedDate == today.timeInMillis -> stringResource(R.string.analytics_date_today)
                else -> shortDate(selectedDate)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = labelColor, modifier = Modifier.size(14.dp))
                Text(label, color = labelColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }

    if (pickerOpen) {
        CustomRangePicker(
            initialStart = range?.startMs ?: selectedDate,
            initialEnd = range?.endMs ?: selectedDate,
            isDark = isDark,
            onClose = { pickerOpen = false },
            onApply = { start, end ->
                onRangeChange(DateRange(start, end))
                onPeriodChange(Period.Custom)
                selectedDate = start
                pickerOpen = false
            },
        )
    }
}

private fun buildWeekDays(today: Calendar, weekOffset: Int): List<Calendar> {
    val sunday = Calendar.getInstance().apply {
        timeInMillis = today.timeInMillis
        add(Calendar.DATE, -get(Calendar.DAY_OF_WEEK) + 1 + weekOffset * 7)
    }
    return (0 until 7).map {
        Calendar.getInstance().apply {
            timeInMillis = sunday.timeInMillis
            add(Calendar.DATE, it)
        }
    }
}

private fun weekPageDeltaForSelectedDay(selectedMs: Long, today: Calendar): Int {
    val d = Calendar.getInstance().apply {
        timeInMillis = selectedMs
        zeroTime()
    }
    val sunD = startOfWeekSunday(d)
    val sunT = startOfWeekSunday(today)
    return ((sunD.timeInMillis - sunT.timeInMillis) / (7 * HistoryData.DAY)).toInt()
}

private fun startOfWeekSunday(c: Calendar): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = c.timeInMillis
        zeroTime()
        add(Calendar.DATE, -get(Calendar.DAY_OF_WEEK) + 1)
    }
}

private fun rangeMatchesNonDay(range: DateRange?, days: List<Calendar>, today: Calendar): Boolean {
    if (range == null) return false
    val startCal = Calendar.getInstance().apply { timeInMillis = range.startMs; zeroTime() }
    val endCal = Calendar.getInstance().apply { timeInMillis = range.endMs; zeroTime() }
    val sameDay = startCal.timeInMillis == endCal.timeInMillis
    if (!sameDay) return true
    return days.none { it.timeInMillis == startCal.timeInMillis }
}

private fun Calendar.zeroTime() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun Calendar.sameDayAs(other: Calendar): Boolean {
    return get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
        get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
        get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
}

private fun weekdayShort(c: Calendar): String =
    SimpleDateFormat("EEEEE", Locale.getDefault()).format(Date(c.timeInMillis))

private fun dayOfMonth(c: Calendar): String =
    SimpleDateFormat("d", Locale.getDefault()).format(Date(c.timeInMillis))

private fun shortDate(ms: Long): String =
    SimpleDateFormat("MMM d", Locale.getDefault()).apply { timeZone = TimeZone.getDefault() }.format(Date(ms))
