package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.data.ReservationType
import com.mh.restaurantchainpos.pos.ui.theme.Blue400
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

internal fun rowBackground(
    palette: FloorPalette,
    assigning: Boolean,
    canAssign: Boolean,
    pending: Boolean,
    flashing: Boolean,
): Color = when {
    flashing -> Blue500.copy(alpha = 0.18f)
    pending -> Blue500.copy(alpha = 0.12f)
    assigning && canAssign -> Blue500.copy(alpha = 0.04f)
    assigning -> palette.availableBorder.copy(alpha = 0.22f)
    else -> palette.raised
}

internal fun rowBorder(
    palette: FloorPalette,
    assigning: Boolean,
    canAssign: Boolean,
    pending: Boolean,
    flashing: Boolean,
): Modifier = when {
    pending || flashing -> Modifier.border(2.dp, Blue600, RoundedCornerShape(6.dp))
    assigning && canAssign -> Modifier.border(1.5.dp, Blue600.copy(alpha = 0.30f), RoundedCornerShape(6.dp))
    else -> Modifier.border(1.dp, Color.Transparent, RoundedCornerShape(6.dp))
}

internal data class TimeLabel(val label: String, val hour: Float)

internal fun buildTimeLabels(startHour: Float, windowHours: Float): List<TimeLabel> {
    val labels = mutableListOf<TimeLabel>()
    val step = when {
        windowHours <= 6f -> 1f
        windowHours <= 12f -> 2f
        else -> 3f
    }
    var hour = ceil(startHour / step) * step
    val end = startHour + windowHours + 0.001f
    while (hour <= end) {
        val display = floor(hour).toInt().floorMod(24)
        labels += TimeLabel("${display}:00", hour)
        hour += step
    }
    return labels
}

internal enum class BlockVisualState { OnTime, Completed, NoShow }

internal data class ReservationVisual(
    val fill: Color,
    val border: Color,
    val text: Color,
    val subText: Color,
    val dashed: Boolean,
    val strokeWidth: Dp,
)

internal fun blockVisualState(reservation: Reservation, @Suppress("UNUSED_PARAMETER") nowHour: Float, @Suppress("UNUSED_PARAMETER") isToday: Boolean): BlockVisualState =
    when (reservation.status) {
        "COMPLETED" -> BlockVisualState.Completed
        "NO_SHOW" -> BlockVisualState.NoShow
        else -> BlockVisualState.OnTime
    }

internal fun reservationVisual(
    palette: FloorPalette,
    reservation: Reservation,
    visualState: BlockVisualState,
    preview: Boolean,
    @Suppress("UNUSED_PARAMETER") allReservations: List<Reservation>,
): ReservationVisual = when {
    preview -> ReservationVisual(
        fill = Blue500.copy(alpha = 0.08f),
        border = Blue600,
        text = Blue600,
        subText = palette.text2,
        dashed = true,
        strokeWidth = 2.dp,
    )
    visualState == BlockVisualState.Completed -> ReservationVisual(
        fill = Blue600,
        border = Blue600,
        text = Color.White,
        subText = Color.White.copy(alpha = 0.84f),
        dashed = false,
        strokeWidth = 1.5.dp,
    )
    visualState == BlockVisualState.NoShow -> ReservationVisual(
        fill = palette.text3,
        border = palette.text2,
        text = Color.White,
        subText = Color.White.copy(alpha = 0.84f),
        dashed = true,
        strokeWidth = 1.5.dp,
    )
    reservation.type == ReservationType.Request -> ReservationVisual(
        fill = Blue500.copy(alpha = 0.10f),
        border = Blue400,
        text = Blue600,
        subText = palette.text2,
        dashed = true,
        strokeWidth = 1.5.dp,
    )
    else -> ReservationVisual(
        fill = palette.occupiedFill,
        border = palette.occupiedBorder,
        text = palette.text1,
        subText = palette.text2,
        dashed = false,
        strokeWidth = 1.5.dp,
    )
}

internal fun blockTag(reservation: Reservation, state: BlockVisualState): String = when {
    state == BlockVisualState.Completed -> "PAID"
    state == BlockVisualState.NoShow -> "NO-SHOW"
    reservation.type == ReservationType.Request -> "REQ"
    else -> ""
}

internal fun Modifier.dashedBorder(color: Color, strokeWidth: Dp, cornerRadius: Dp): Modifier =
    this.drawBehind {
        val w = strokeWidth.toPx()
        val r = cornerRadius.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(w / 2, w / 2),
            size = Size(size.width - w, size.height - w),
            cornerRadius = CornerRadius(r, r),
            style = Stroke(
                width = w,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f), 0f),
            ),
        )
    }

internal fun timeToHour(time: String): Float {
    val hour = time.substringBefore(":").toFloatOrNull() ?: 0f
    val minute = time.substringAfter(":", "0").toFloatOrNull() ?: 0f
    return hour + minute / 60f
}

internal fun hourOfDay(ms: Long): Float {
    val cal = Calendar.getInstance().apply { timeInMillis = ms }
    return cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60f
}

internal fun calendarForOffset(offset: Int): Calendar =
    Calendar.getInstance().zeroTime().apply { add(Calendar.DATE, offset) }

internal fun offsetForCalendar(calendar: Calendar): Int =
    ((calendar.cloneAsCalendar().zeroTime().timeInMillis - Calendar.getInstance().zeroTime().timeInMillis) / DayMillis).toInt()

internal fun offsetForDate(year: Int, month: Int, day: Int): Int =
    offsetForCalendar(Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        zeroTime()
    })

internal fun Calendar.zeroTime(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}

internal fun Calendar.cloneAsCalendar(): Calendar =
    (clone() as Calendar)

internal fun buildCalendarMonth(year: Int, month: Int): List<List<Int?>> {
    val first = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
        zeroTime()
    }
    val firstDay = first.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = first.getActualMaximum(Calendar.DAY_OF_MONTH)
    val weeks = mutableListOf<List<Int?>>()
    var week = MutableList<Int?>(firstDay) { null }
    for (day in 1..daysInMonth) {
        week += day
        if (week.size == 7) {
            weeks += week
            week = mutableListOf()
        }
    }
    if (week.isNotEmpty()) {
        while (week.size < 7) week += null
        weeks += week
    }
    return weeks
}

internal fun sameYmd(calendar: Calendar, year: Int, month: Int, day: Int): Boolean =
    calendar.get(Calendar.YEAR) == year &&
        calendar.get(Calendar.MONTH) == month &&
        calendar.get(Calendar.DAY_OF_MONTH) == day

internal fun fullDateLabel(offset: Int): String =
    SimpleDateFormat("EEE, MMM d, yyyy", Locale.US).format(Date(calendarForOffset(offset).timeInMillis))

internal fun shortDateLabel(offset: Int): String =
    SimpleDateFormat("M/d", Locale.US).format(Date(calendarForOffset(offset).timeInMillis))

internal fun dayLabel(offset: Int): String = when (offset) {
    -1 -> "Yesterday"
    0 -> "Today"
    1 -> "Tomorrow"
    else -> if (offset < 0) "${-offset}d ago" else "in ${offset}d"
}

internal fun formatHours(hours: Double): String {
    val intPart = hours.toInt()
    val frac = hours - intPart
    return if (frac == 0.0) "${intPart}h" else "${intPart}.${(frac * 10).toInt()}h"
}

internal fun Int.floorMod(mod: Int): Int {
    val r = this % mod
    return if (r < 0) r + mod else r
}
