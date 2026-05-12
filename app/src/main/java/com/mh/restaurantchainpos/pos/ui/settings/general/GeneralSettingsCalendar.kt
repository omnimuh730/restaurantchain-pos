package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar

internal data class DayOff(val year: Int, val month: Int, val day: Int) {
    fun key(): String = "%04d-%02d-%02d".format(year, month + 1, day)
}

private fun firstDayOfWeek(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(year, month, 1)
    return cal.get(Calendar.DAY_OF_WEEK) - 1
}

private fun daysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(year, month, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

@Composable
internal fun formatLongDate(d: DayOff): String {
    val locale = LocalConfiguration.current.locales[0]
    return remember(d.key(), locale) {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(d.year, d.month, d.day)
        SimpleDateFormat("EEE, MMM d, yyyy", locale).format(cal.time)
    }
}

@Composable
internal fun DayOffCalendarDialog(
    colors: PosColors,
    initiallySelected: List<DayOff>,
    onDismiss: () -> Unit,
    onSave: (List<DayOff>) -> Unit,
) {
    val today = remember {
        val c = Calendar.getInstance()
        DayOff(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }
    var viewYear by remember { mutableIntStateOf(initiallySelected.firstOrNull()?.year ?: today.year) }
    var viewMonth by remember { mutableIntStateOf(initiallySelected.firstOrNull()?.month ?: today.month) }
    val selected = remember { mutableStateListOf<DayOff>().apply { addAll(initiallySelected) } }
    val hasChanges = selected.map { it.key() }.toSet() != initiallySelected.map { it.key() }.toSet()
    val locale = LocalConfiguration.current.locales[0]
    val monthTitle = remember(viewYear, viewMonth, locale) {
        val c = Calendar.getInstance()
        c.clear()
        c.set(viewYear, viewMonth, 1)
        SimpleDateFormat("MMMM yyyy", locale).format(c.time)
    }
    val weekdayLabels = remember(locale) {
        val sw = DateFormatSymbols(locale).shortWeekdays
        (1..7).map { i -> sw[i].orEmpty() }
    }

    ModalScrim(onDismiss = onDismiss) {
        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .widthIn(max = 380.dp)
                .consumeModalTaps(),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.settings_cal_day_off_title), color = colors.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(stringResource(R.string.settings_cal_day_off_subtitle), color = colors.textMuted, fontSize = 12.sp)
                }
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(18.dp))
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))

            Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (viewMonth == 0) {
                                    viewMonth = 11
                                    viewYear--
                                } else viewMonth--
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.KeyboardArrowLeft, contentDescription = null, tint = colors.text, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        monthTitle,
                        color = colors.text,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (viewMonth == 11) {
                                    viewMonth = 0
                                    viewYear++
                                } else viewMonth++
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = colors.text, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth()) {
                    weekdayLabels.forEach { label ->
                        Box(Modifier.weight(1f).padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                            Text(label, color = colors.textMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                val lead = firstDayOfWeek(viewYear, viewMonth)
                val total = daysInMonth(viewYear, viewMonth)
                val rows = ((lead + total + 6) / 7)
                Column {
                    repeat(rows) { rowIdx ->
                        Row(Modifier.fillMaxWidth()) {
                            for (col in 0 until 7) {
                                val cell = rowIdx * 7 + col
                                val dayNum = cell - lead + 1
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (dayNum in 1..total) {
                                        val d = DayOff(viewYear, viewMonth, dayNum)
                                        val isSelected = selected.any { it.key() == d.key() }
                                        val isToday = d.key() == today.key()
                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) Blue600 else Color.Transparent)
                                                .border(
                                                    1.5.dp,
                                                    when {
                                                        isSelected -> Color.Transparent
                                                        isToday -> colors.text
                                                        else -> Color.Transparent
                                                    },
                                                    RoundedCornerShape(10.dp),
                                                )
                                                .clickable {
                                                    val existing = selected.firstOrNull { it.key() == d.key() }
                                                    if (existing != null) selected.remove(existing) else selected.add(d)
                                                },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                "$dayNum",
                                                color = if (isSelected) Color.White else colors.text,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.SemiBold else FontWeight.Normal,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(stringResource(R.string.common_cancel), color = colors.textMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.size(6.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (hasChanges) Blue600 else colors.surfaceRaised)
                        .clickable(enabled = hasChanges) { onSave(selected.toList()) }
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                ) {
                    Text(
                        stringResource(R.string.common_save),
                        color = if (hasChanges) Color.White else colors.textMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
