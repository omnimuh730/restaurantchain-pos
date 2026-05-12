package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R

/** Compose-friendly duration copy; avoids Context.getString in @Composable (lint). */
@Composable
internal fun reservationDurationHoursLabel(hours: Double): String {
    val intPart = hours.toInt()
    val frac = hours - intPart
    return if (frac == 0.0) {
        stringResource(R.string.floor_cal_duration_hours_whole, intPart)
    } else {
        stringResource(R.string.floor_cal_duration_hours_frac, intPart, (frac * 10).toInt())
    }
}
