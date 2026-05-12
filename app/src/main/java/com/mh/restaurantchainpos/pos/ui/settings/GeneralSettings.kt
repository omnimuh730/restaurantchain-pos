package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
fun GeneralSettings(colors: PosColors) {
    val mainPhone = remember { mutableStateOf("(555) 000-1234") }
    val altPhone = remember { mutableStateOf("(555) 000-5678") }
    val deposit = remember { mutableStateOf("500") }
    var depositCurrency by remember { mutableStateOf(CurrencyKind.Foreign) }
    val grace = remember { mutableStateOf("20") }
    val daysOff = remember {
        mutableStateListOf(
            DayOff(2026, 4, 15),
            DayOff(2026, 4, 20),
        )
    }
    val hours = remember {
        mutableStateListOf(
            HoursRow(R.string.settings_day_mon, "10:00 AM", "10:00 PM", false),
            HoursRow(R.string.settings_day_tue, "10:00 AM", "10:00 PM", false),
            HoursRow(R.string.settings_day_wed, "10:00 AM", "10:00 PM", false),
            HoursRow(R.string.settings_day_thu, "10:00 AM", "11:00 PM", false),
            HoursRow(R.string.settings_day_fri, "10:00 AM", "11:00 PM", false),
            HoursRow(R.string.settings_day_sat, "11:00 AM", "11:00 PM", false),
            HoursRow(R.string.settings_day_sun, "11:00 AM", "9:00 PM", false),
        )
    }
    var calendarOpen by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        RestaurantInfoCard(
            colors = colors,
            deposit = deposit.value,
            onDepositChange = { deposit.value = it.filter { c -> c.isDigit() } },
            depositCurrency = depositCurrency,
            onCurrencyChange = { depositCurrency = it },
            grace = grace.value,
            onGraceChange = { grace.value = it.filter { c -> c.isDigit() } },
        )
        DaysOffCard(colors, daysOff, onAdd = { calendarOpen = true })
        OpeningHoursCard(colors, hours)
        PhoneNumbersCard(
            colors = colors,
            mainPhone = mainPhone.value,
            onMainPhoneChange = { mainPhone.value = it },
            altPhone = altPhone.value,
            onAltPhoneChange = { altPhone.value = it },
        )
    }

    if (calendarOpen) {
        DayOffCalendarDialog(
            colors = colors,
            initiallySelected = daysOff.toList(),
            onDismiss = { calendarOpen = false },
            onSave = { selected ->
                daysOff.clear()
                daysOff.addAll(selected.sortedWith(compareBy({ it.year }, { it.month }, { it.day })))
                calendarOpen = false
            },
        )
    }
}
