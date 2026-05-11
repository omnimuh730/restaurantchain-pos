package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import com.mh.restaurantchainpos.pos.ui.theme.Slate400
import java.util.Calendar
import kotlin.math.roundToInt

private data class HoursRow(val day: String, val open: String, val close: String, val closed: Boolean)
private data class DayOff(val year: Int, val month: Int, val day: Int) {
    fun key(): String = "%04d-%02d-%02d".format(year, month + 1, day)
}

private val MonthLong = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)
private val MonthShort = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
private val WeekdayShort = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
private val WeekdayLetter = listOf("S", "M", "T", "W", "T", "F", "S")

private fun firstDayOfWeek(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(year, month, 1)
    // Calendar.SUNDAY = 1 .. SATURDAY = 7, we want 0..6
    return cal.get(Calendar.DAY_OF_WEEK) - 1
}

private fun daysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(year, month, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

private fun formatLongDate(d: DayOff): String {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(d.year, d.month, d.day)
    val wd = WeekdayShort[cal.get(Calendar.DAY_OF_WEEK) - 1]
    return "$wd, ${MonthShort[d.month]} ${d.day}, ${d.year}"
}

@Composable
fun GeneralSettings(colors: PosColors) {
    val mainPhone = remember { mutableStateOf("(555) 000-1234") }
    val altPhone = remember { mutableStateOf("(555) 000-5678") }
    val deposit = remember { mutableStateOf("500") }
    var depositCurrency by remember { mutableStateOf(CurrencyKind.Foreign) }
    val grace = remember { mutableStateOf("20") }
    val daysOff = remember {
        mutableStateListOf(
            DayOff(2026, 4, 15), // May 15, 2026 (month index 4 = May)
            DayOff(2026, 4, 20),
        )
    }
    val hours = remember {
        mutableStateListOf(
            HoursRow("Mon", "10:00 AM", "10:00 PM", false),
            HoursRow("Tue", "10:00 AM", "10:00 PM", false),
            HoursRow("Wed", "10:00 AM", "10:00 PM", false),
            HoursRow("Thu", "10:00 AM", "11:00 PM", false),
            HoursRow("Fri", "10:00 AM", "11:00 PM", false),
            HoursRow("Sat", "11:00 AM", "11:00 PM", false),
            HoursRow("Sun", "11:00 AM", "9:00 PM", false),
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

@Composable
private fun RestaurantInfoCard(
    colors: PosColors,
    deposit: String,
    onDepositChange: (String) -> Unit,
    depositCurrency: CurrencyKind,
    onCurrencyChange: (CurrencyKind) -> Unit,
    grace: String,
    onGraceChange: (String) -> Unit,
) {
    SettingCard(
        colors = colors,
        title = "Restaurant Info",
        subtitle = "Basic information about your restaurant",
        badge = "Free Tier",
        badgeIcon = Icons.Outlined.Image,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                Text("Restaurant Name", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text("Glass Onion", color = colors.text, fontSize = 14.sp)
            }
            Column {
                Text("Description", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Modern Asian fusion restaurant with a curated cocktail bar, serving contemporary dishes inspired by flavors across East and Southeast Asia.",
                    color = colors.textMuted,
                    fontSize = 13.sp,
                )
            }
            Column {
                Text("Deposit Money", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    SettingTextField(
                        colors = colors,
                        value = deposit,
                        onChange = onDepositChange,
                        leadingText = if (depositCurrency == CurrencyKind.Foreign) "$" else "₩",
                        leadingTint = if (depositCurrency == CurrencyKind.Foreign) Red500 else Blue600,
                        keyboard = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                    CurrencySwitch(currency = depositCurrency, onChange = onCurrencyChange)
                }
                Spacer(Modifier.height(4.dp))
                Text("Starting cash in drawer at the beginning of each shift", color = colors.textMuted, fontSize = 11.sp)
            }
            Column {
                Text("Grace Period", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                SettingTextField(
                    colors = colors,
                    value = grace,
                    onChange = onGraceChange,
                    leadingIcon = Icons.Outlined.Timer,
                    trailingText = "min",
                    keyboard = KeyboardType.Number,
                )
                Spacer(Modifier.height(4.dp))
                Text("Wait time before a reservation is marked as no-show", color = colors.textMuted, fontSize = 11.sp)
            }
            RestaurantImageGallery(colors)
        }
    }
}

@Composable
private fun CurrencySwitch(currency: CurrencyKind, onChange: (CurrencyKind) -> Unit) {
    // Pill button looking like screenshot: a blue rounded container with the active currency name
    // on a blue background and the inactive symbol on a white inset pill.
    val isForeign = currency == CurrencyKind.Foreign
    Row(
        Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Blue600)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isForeign) {
            Box(Modifier.padding(horizontal = 10.dp), contentAlignment = Alignment.Center) {
                Text("Foreign", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Box(
                Modifier
                    .width(36.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.White)
                    .clickable { onChange(CurrencyKind.Domestic) },
                contentAlignment = Alignment.Center,
            ) {
                Text("₩", color = Blue600, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        } else {
            Box(
                Modifier
                    .width(36.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color.White)
                    .clickable { onChange(CurrencyKind.Foreign) },
                contentAlignment = Alignment.Center,
            ) {
                Text("$", color = Red500, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(Modifier.padding(horizontal = 10.dp), contentAlignment = Alignment.Center) {
                Text("Domestic", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun RestaurantImageGallery(colors: PosColors) {
    val images = remember {
        listOf(
            Color(0xFF334155), // mock dark teal
            Color(0xFF1F2937),
            Color(0xFF374151),
            Color(0xFF475569),
        )
    }
    var index by remember { mutableIntStateOf(0) }
    var showAll by remember { mutableStateOf(false) }
    var dragOffsetPx by remember { mutableStateOf(0f) }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Restaurant Images (${index + 1}/${images.size})",
                color = colors.text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceRaised)
                    .clickable { showAll = !showAll }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    if (showAll) "Hide" else "Show All",
                    color = colors.text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                .pointerInput(images.size) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val threshold = 80f
                            if (dragOffsetPx < -threshold) index = (index + 1) % images.size
                            else if (dragOffsetPx > threshold) index = (index - 1 + images.size) % images.size
                            dragOffsetPx = 0f
                        },
                        onDragCancel = { dragOffsetPx = 0f },
                        onHorizontalDrag = { _, dx -> dragOffsetPx += dx },
                    )
                },
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(images[index]),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Image,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(56.dp),
                )
            }
            Box(
                Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { index = (index - 1 + images.size) % images.size }
                    .size(34.dp)
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.KeyboardArrowLeft, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Box(
                Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { index = (index + 1) % images.size }
                    .size(34.dp)
                    .align(Alignment.CenterEnd),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            images.forEachIndexed { i, _ ->
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .height(8.dp)
                        .width(if (i == index) 18.dp else 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (i == index) Blue600 else colors.border)
                        .clickable { index = i },
                )
            }
        }
    }
}

@Composable
private fun DaysOffCard(colors: PosColors, daysOff: SnapshotStateList<DayOff>, onAdd: () -> Unit) {
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
private fun OpeningHoursCard(colors: PosColors, hours: SnapshotStateList<HoursRow>) {
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
private fun TimeField(colors: PosColors, value: String, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    SettingTextField(
        colors = colors,
        value = value,
        onChange = onChange,
        trailingIcon = Icons.Outlined.Schedule,
        modifier = modifier,
    )
}

@Composable
private fun PhoneNumbersCard(
    colors: PosColors,
    mainPhone: String,
    onMainPhoneChange: (String) -> Unit,
    altPhone: String,
    onAltPhoneChange: (String) -> Unit,
) {
    SettingCard(
        colors = colors,
        title = "Phone Numbers",
        headerIcon = Icons.Outlined.Phone,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                Text("Main Phone", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                SettingTextField(colors, mainPhone, onMainPhoneChange, leadingIcon = Icons.Outlined.Phone, keyboard = KeyboardType.Phone)
            }
            Column {
                Text("Alternate Phone", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                SettingTextField(colors, altPhone, onAltPhoneChange, leadingIcon = Icons.Outlined.Phone, keyboard = KeyboardType.Phone)
                Spacer(Modifier.height(4.dp))
                Text("Optional. Shown to guests when the main line is busy.", color = colors.textMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun DayOffCalendarDialog(
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

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .widthIn(max = 380.dp)
                .clickable(enabled = false) {},
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Select day off", color = colors.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text("Choose a date when the restaurant will be closed", color = colors.textMuted, fontSize = 12.sp)
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
                                if (viewMonth == 0) { viewMonth = 11; viewYear-- } else viewMonth--
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.KeyboardArrowLeft, contentDescription = null, tint = colors.text, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        "${MonthLong[viewMonth]} $viewYear",
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
                                if (viewMonth == 11) { viewMonth = 0; viewYear++ } else viewMonth++
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = colors.text, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth()) {
                    WeekdayLetter.forEach { l ->
                        Box(Modifier.weight(1f).padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                            Text(l, color = colors.textMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                    Text("Cancel", color = colors.textMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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
                        "Save",
                        color = if (hasChanges) Color.White else colors.textMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
