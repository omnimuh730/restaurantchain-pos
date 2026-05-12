package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Date range picker modal with quick-presets and a month-grid calendar.
 * Mirrors `restaurantchain-pos-ui-demo/.../CustomRangePicker.tsx`.
 */
@Composable
fun CustomRangePicker(
    initialStart: Long,
    initialEnd: Long,
    isDark: Boolean,
    onClose: () -> Unit,
    onApply: (start: Long, end: Long) -> Unit,
) {
    var start by remember { mutableLongStateOf(zeroDay(initialStart)) }
    var end by remember { mutableLongStateOf(zeroDay(initialEnd)) }
    val nav = remember { Calendar.getInstance().apply { timeInMillis = initialStart } }
    var navMillis by remember { mutableLongStateOf(nav.timeInMillis) }

    val locale = LocalConfiguration.current.locales[0]
    val monthYearFmt = remember(locale) {
        SimpleDateFormat("MMMM yyyy", locale).apply { timeZone = TimeZone.getDefault() }
    }
    val shortFmt = remember(locale) {
        SimpleDateFormat("MMM d, yyyy", locale).apply { timeZone = TimeZone.getDefault() }
    }
    fun monthYear(ms: Long): String = monthYearFmt.format(Date(ms))
    fun shortDate(ms: Long): String = shortFmt.format(Date(ms))
    val weekLetters = remember(locale) { localizedWeekdayInitials(locale) }

    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val card = if (isDark) Color(0xFF1F2937) else Color.White
    val border = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val muted = if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9)

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(card)
                    .border(1.dp, border, RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.analytics_range_title), color = text1, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${shortDate(start)} – ${shortDate(end)}",
                        color = text2,
                        fontSize = 12.sp,
                    )
                }
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) { Text("✕", color = text2, fontSize = 14.sp) }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(
                    stringResource(R.string.analytics_preset_last_1_week) to 7,
                    stringResource(R.string.analytics_preset_last_2_weeks) to 14,
                    stringResource(R.string.analytics_preset_this_month) to -1,
                    stringResource(R.string.analytics_preset_last_month) to -2,
                ).forEach { (label, presetDays) ->
                    PresetPill(label, text2, border) {
                        val now = Calendar.getInstance().also { it.zeroDay() }.timeInMillis
                        when (presetDays) {
                            -1 -> {
                                val c = Calendar.getInstance().also { it.zeroDay(); it.set(Calendar.DAY_OF_MONTH, 1) }
                                start = c.timeInMillis
                                end = now
                            }
                            -2 -> {
                                val c = Calendar.getInstance().also {
                                    it.zeroDay(); it.set(Calendar.DAY_OF_MONTH, 1); it.add(Calendar.MONTH, -1)
                                }
                                start = c.timeInMillis
                                val e = Calendar.getInstance().apply {
                                    timeInMillis = c.timeInMillis; add(Calendar.MONTH, 1); add(Calendar.DATE, -1)
                                }
                                end = e.timeInMillis
                            }
                            else -> {
                                start = now - (presetDays - 1) * HistoryData.DAY
                                end = now
                            }
                        }
                        navMillis = start
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(28.dp).clip(CircleShape).clickable {
                        val c = Calendar.getInstance().apply { timeInMillis = navMillis; add(Calendar.MONTH, -1) }
                        navMillis = c.timeInMillis
                    },
                    contentAlignment = Alignment.Center,
                ) { Text("‹", color = text2, fontSize = 16.sp) }
                Spacer(Modifier.weight(1f))
                Text(monthYear(navMillis), color = text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.size(28.dp).clip(CircleShape).clickable {
                        val c = Calendar.getInstance().apply { timeInMillis = navMillis; add(Calendar.MONTH, 1) }
                        navMillis = c.timeInMillis
                    },
                    contentAlignment = Alignment.Center,
                ) { Text("›", color = text2, fontSize = 16.sp) }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                weekLetters.forEach { letter ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(letter, color = text2, fontSize = 11.sp)
                    }
                }
            }

            MonthGrid(
                navMillis = navMillis,
                start = start,
                end = end,
                isDark = isDark,
                onPick = { dayMs ->
                    if (start == end) {
                        if (dayMs < start) {
                            end = start; start = dayMs
                        } else end = dayMs
                    } else {
                        start = dayMs; end = dayMs
                    }
                },
                muted = muted,
                text1 = text1,
                text2 = text2,
                todayDot = if (isDark) Color(0xFFF3F4F6) else Color(0xFF111827),
            )

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.analytics_range_cancel),
                    color = text2,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onClose)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Blue600)
                        .clickable {
                            val s = minOf(start, end)
                            val e = maxOf(start, end) + HistoryData.DAY - 1
                            onApply(s, e)
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(stringResource(R.string.analytics_range_apply), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        }
    }
}

@Composable
private fun PresetPill(label: String, color: Color, border: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(label, color = color, fontSize = 12.sp)
    }
}

@Composable
private fun MonthGrid(
    navMillis: Long,
    start: Long,
    end: Long,
    isDark: Boolean,
    onPick: (Long) -> Unit,
    muted: Color,
    text1: Color,
    text2: Color,
    todayDot: Color,
) {
    val todayCal = remember { Calendar.getInstance().also { it.zeroDay() } }
    val nav = Calendar.getInstance().apply { timeInMillis = navMillis; set(Calendar.DAY_OF_MONTH, 1); zeroDay() }
    val firstWeekday = nav.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = nav.getActualMaximum(Calendar.DAY_OF_MONTH)
    val rows = ((firstWeekday + daysInMonth) + 6) / 7
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        for (r in 0 until rows) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (c in 0 until 7) {
                    val cellIndex = r * 7 + c
                    val day = cellIndex - firstWeekday + 1
                    val cell = Calendar.getInstance().apply {
                        timeInMillis = nav.timeInMillis
                        set(Calendar.DAY_OF_MONTH, 1)
                        if (day in 1..daysInMonth) set(Calendar.DAY_OF_MONTH, day)
                    }
                    val cellMs = if (day in 1..daysInMonth) cell.timeInMillis else 0L
                    val s = minOf(start, end)
                    val e = maxOf(start, end)
                    val inRange = cellMs in s..e
                    val edge = cellMs == s || cellMs == e
                    val isToday = day in 1..daysInMonth &&
                        cell.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        cell.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                        cell.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH)
                    Column(
                        Modifier
                            .weight(1f)
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (day in 1..daysInMonth) {
                            val dayBg = when {
                                edge -> Blue600
                                inRange -> if (isDark) Color(0x333B82F6) else Color(0xFFDBEAFE)
                                else -> Color.Transparent
                            }
                            Box(
                                Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(dayBg)
                                    .clickable { onPick(cellMs) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    day.toString(),
                                    color = when {
                                        edge -> Color.White
                                        inRange -> Blue600
                                        else -> text1
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = if (edge) FontWeight.SemiBold else FontWeight.Normal,
                                )
                                if (isToday) {
                                    // Inside the day circle: top marker reads clearly vs range fill; edge uses white on blue.
                                    val dotFill = if (edge) Color.White else todayDot
                                    Box(
                                        Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = 5.dp)
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(dotFill),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @Suppress("UNUSED_EXPRESSION") muted
    @Suppress("UNUSED_EXPRESSION") text2
}

private fun zeroDay(ms: Long): Long {
    val c = Calendar.getInstance().apply { timeInMillis = ms }
    c.zeroDay()
    return c.timeInMillis
}

private fun Calendar.zeroDay() {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}

private fun localizedWeekdayInitials(locale: Locale): List<String> {
    val fmt = SimpleDateFormat("EEEEE", locale)
    val base = Calendar.getInstance().apply {
        clear()
        set(2020, Calendar.MARCH, 1, 12, 0, 0)
    }
    return (0..6).map { i ->
        val cal = (base.clone() as Calendar)
        cal.add(Calendar.DATE, i)
        fmt.format(cal.time)
    }
}
