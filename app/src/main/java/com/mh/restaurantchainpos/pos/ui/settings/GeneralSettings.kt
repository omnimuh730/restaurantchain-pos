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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

private data class HoursRow(val day: String, var open: String, var close: String, var closed: Boolean)

@Composable
fun GeneralSettings(colors: PosColors) {
    val mainPhone = remember { mutableStateOf("(555) 000-1234") }
    val altPhone = remember { mutableStateOf("(555) 000-5678") }
    val deposit = remember { mutableStateOf("500") }
    var depositCurrency by remember { mutableStateOf(CurrencyKind.Foreign) }
    val grace = remember { mutableStateOf("20") }
    val daysOff = remember { mutableStateListOf("Fri, May 15, 2026", "Wed, May 20, 2026") }
    val hours = remember {
        mutableStateListOf(
            HoursRow("Mon", "10:00", "22:00", false),
            HoursRow("Tue", "10:00", "22:00", false),
            HoursRow("Wed", "10:00", "22:00", false),
            HoursRow("Thu", "10:00", "23:00", false),
            HoursRow("Fri", "10:00", "23:00", false),
            HoursRow("Sat", "11:00", "23:00", false),
            HoursRow("Sun", "11:00", "21:00", false),
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(
            colors = colors,
            title = "Restaurant info",
            subtitle = "Public-facing restaurant details",
            badge = "Free tier",
            badgeIcon = "🏬",
        ) {
            SettingLabel(colors, "Restaurant name")
            Text("Glass Onion", color = colors.text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))
            SettingLabel(colors, "Description")
            Text(
                "Modern Pan-Asian dining sourced from local farms. " +
                    "Open seven days a week with a curated wine list and a private events room.",
                color = colors.textMuted,
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    SettingLabel(colors, "Deposit money")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingTextField(
                            colors = colors,
                            value = deposit.value,
                            onChange = { deposit.value = it.filter { c -> c.isDigit() } },
                            leading = if (depositCurrency == CurrencyKind.Domestic) "₩" else "$",
                            keyboard = KeyboardType.Number,
                            modifier = Modifier.weight(1f),
                        )
                        CurrencySwitch(depositCurrency) { depositCurrency = it }
                    }
                    Text("Charged when a guest reserves a private room.", color = colors.textMuted, fontSize = 10.sp)
                }
                Column(Modifier.weight(1f)) {
                    SettingLabel(colors, "Grace period")
                    SettingTextField(
                        colors = colors,
                        value = grace.value,
                        onChange = { grace.value = it.filter { c -> c.isDigit() } },
                        leading = "⏱",
                        trailing = "min",
                        keyboard = KeyboardType.Number,
                    )
                    Text("Time to hold a table after the reservation start.", color = colors.textMuted, fontSize = 10.sp)
                }
            }
        }

        SettingCard(colors = colors, title = "Days off", subtitle = "Mark days when the restaurant is closed.") {
            PrimaryButton("+ Add day off", { daysOff.add("Sat, Jun 1, 2026") })
            Spacer(Modifier.height(12.dp))
            daysOff.forEach { date ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceRaised)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(date, color = colors.text, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { daysOff.remove(date) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✕", color = Red500, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        SettingCard(colors = colors, title = "Opening hours", subtitle = "Per-day hours and closures.") {
            hours.forEachIndexed { index, row ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(row.day, color = colors.textMuted, fontSize = 12.sp, modifier = Modifier.size(width = 36.dp, height = 20.dp))
                    ToggleSwitch(checked = !row.closed) {
                        hours[index] = row.copy(closed = !row.closed)
                    }
                    if (!row.closed) {
                        SettingTextField(
                            colors = colors,
                            value = row.open,
                            onChange = { hours[index] = row.copy(open = it) },
                            modifier = Modifier.weight(1f),
                        )
                        Text("→", color = colors.textMuted, fontSize = 12.sp)
                        SettingTextField(
                            colors = colors,
                            value = row.close,
                            onChange = { hours[index] = row.copy(close = it) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Text("Closed", color = colors.textMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        SettingCard(colors = colors, title = "Phone numbers") {
            SettingLabel(colors, "Main phone")
            SettingTextField(colors, mainPhone.value, { mainPhone.value = it }, leading = "☏")
            Spacer(Modifier.height(12.dp))
            SettingLabel(colors, "Alternate phone")
            SettingTextField(colors, altPhone.value, { altPhone.value = it }, leading = "☏")
            Text("Optional. Shown to guests when the main line is busy.", color = colors.textMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun CurrencySwitch(active: CurrencyKind, onChange: (CurrencyKind) -> Unit) {
    val labels = listOf("Foreign" to CurrencyKind.Foreign, "Domestic" to CurrencyKind.Domestic)
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Blue500)
            .padding(2.dp),
    ) {
        labels.forEach { (label, kind) ->
            val isActive = kind == active
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isActive) Color.White else Color.Transparent)
                    .clickable { onChange(kind) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(label, color = if (isActive) Blue500 else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
