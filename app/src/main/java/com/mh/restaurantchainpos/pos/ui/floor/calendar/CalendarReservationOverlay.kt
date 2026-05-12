package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

/**
 * Full-page reservation drawer overlay. Hosted by `FloorPlanScreen` so the
 * overlay spans the entire page body (top view-mode tabs + Hall/Lounge tabs +
 * calendar body), not just the calendar sub-component that triggered it.
 */
@Composable
fun CalendarReservationOverlay(
    palette: FloorPalette,
    open: Boolean,
    pending: List<Reservation>,
    confirmed: List<Reservation>,
    isMobile: Boolean,
    onApprove: (Reservation) -> Unit,
    onAssign: (Reservation) -> Unit,
    onDecline: (Reservation) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = open,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .clickable(onClick = onClose),
            )
        }
        AnimatedVisibility(
            visible = open,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier.align(Alignment.TopStart),
        ) {
            CalendarPanel(
                palette = palette,
                pending = pending,
                confirmed = confirmed,
                onApprove = onApprove,
                onAssign = onAssign,
                onDecline = onDecline,
                onClose = onClose,
                modifier = Modifier.fillMaxHeight().width(if (isMobile) 320.dp else 360.dp),
            )
        }
    }
}
