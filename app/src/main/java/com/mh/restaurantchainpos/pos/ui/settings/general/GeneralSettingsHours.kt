package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

internal data class HoursRow(val day: String, val open: String, val close: String, val closed: Boolean)

@Composable
internal fun DaysOffCard(colors: PosColors, daysOff: SnapshotStateList<DayOff>, onAdd: () -> Unit) {
    SettingCard(
        colors = colors,
        title = "Days Off",
        subtitle = "Select dates when the restaurant will be closed",
        headerIcon = Icons.Outlined.CalendarToday,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                Text("Add Day Off", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                        .clickable(onClick = onAdd)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(10.dp))
                        Text("Select a date", color = colors.textMuted, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text("Click to select a date to add as a day off", color = colors.textMuted, fontSize = 11.sp)
            }

            if (daysOff.isNotEmpty()) {
                Column {
                    Text("Scheduled Days Off (${daysOff.size})", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        daysOff.toList().forEach { day ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.surfaceRaised)
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(formatLongDate(day), color = colors.text, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Box(
                                    Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { daysOff.remove(day) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Outlined.Close, contentDescription = null, tint = Red500, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun OpeningHoursCard(colors: PosColors, hours: SnapshotStateList<HoursRow>) {
    SettingCard(
        colors = colors,
        title = "Opening Hours",
        subtitle = "Set your operating hours for each day",
        headerIcon = Icons.Outlined.Schedule,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            hours.forEachIndexed { index, row ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(row.day, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(36.dp))
                    ToggleSwitch(checked = !row.closed) {
                        hours[index] = row.copy(closed = !row.closed)
                    }
                    if (!row.closed) {
                        TimeField(colors, row.open, Modifier.weight(1f)) { hours[index] = row.copy(open = it) }
                        Text("to", color = colors.textMuted, fontSize = 12.sp)
                        TimeField(colors, row.close, Modifier.weight(1f)) { hours[index] = row.copy(close = it) }
                    } else {
                        Text("Closed", color = colors.textMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
internal fun TimeField(colors: PosColors, value: String, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    SettingTextField(
        colors = colors,
        value = value,
        onChange = onChange,
        trailingIcon = Icons.Outlined.Schedule,
        modifier = modifier,
    )
}
