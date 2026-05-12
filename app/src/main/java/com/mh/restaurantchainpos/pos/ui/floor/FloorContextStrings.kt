package com.mh.restaurantchainpos.pos.ui.floor

import android.content.Context
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.data.ReservationType
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

internal fun Context.floorAppLocale() = resources.configuration.locales[0]

internal fun Context.calendarFullDateLabel(offset: Int): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, floorAppLocale())
        .format(Date(calendarForOffset(offset).timeInMillis))

internal fun Context.calendarShortDateLabel(offset: Int): String =
    DateFormat.getDateInstance(DateFormat.SHORT, floorAppLocale())
        .format(Date(calendarForOffset(offset).timeInMillis))

internal fun Context.dayLabelForCalendarOffset(offset: Int): String = when (offset) {
    -1 -> getString(R.string.floor_cal_day_yesterday)
    0 -> getString(R.string.floor_cal_day_today)
    1 -> getString(R.string.floor_cal_day_tomorrow)
    else -> if (offset < 0) {
        getString(R.string.floor_cal_day_days_ago, -offset)
    } else {
        getString(R.string.floor_cal_day_in_days, offset)
    }
}

internal fun Context.formatReservationDurationHours(hours: Double): String {
    val intPart = hours.toInt()
    val frac = hours - intPart
    return if (frac == 0.0) {
        getString(R.string.floor_cal_duration_hours_whole, intPart)
    } else {
        getString(R.string.floor_cal_duration_hours_frac, intPart, (frac * 10).toInt())
    }
}

internal fun Context.reservationBlockTag(reservation: Reservation, state: BlockVisualState): String = when {
    state == BlockVisualState.Completed -> getString(R.string.floor_res_tag_paid)
    state == BlockVisualState.NoShow -> getString(R.string.floor_res_tag_no_show)
    reservation.type == ReservationType.Request -> getString(R.string.floor_res_tag_req)
    else -> ""
}

internal fun Context.calendarMonthYearLabel(cal: Calendar): String =
    SimpleDateFormat("LLLL yyyy", floorAppLocale()).format(cal.time)

internal fun Context.calendarPickerWeekdayInitials(): List<String> {
    val sw = DateFormatSymbols(floorAppLocale()).shortWeekdays
    return listOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
    ).map { day ->
        sw[day].replace(".", "").trim().take(2)
    }
}
