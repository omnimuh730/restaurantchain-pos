package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainpos.pos.data.Floor
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.data.ReservationType
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import kotlinx.coroutines.delay

@Composable
fun FloorCalendarView(
    palette: FloorPalette,
    floors: List<Floor>,
    activeFloorId: String,
    reservations: List<Reservation>,
    onAccept: (Reservation) -> Unit,
    onDecline: (Reservation) -> Unit,
    onAssignTable: (reservationId: String, tableId: String) -> Unit = { _, _ -> },
) {
    val isMobile = rememberIsMobile()
    var dayOffset by remember { mutableIntStateOf(0) }
    var startHour by remember { mutableFloatStateOf(16f) }
    var windowHours by remember { mutableFloatStateOf(8f) }
    var panelOpen by remember { mutableStateOf(false) }
    var datePickerOpen by remember { mutableStateOf(false) }
    var pickerMonthOffset by remember { mutableIntStateOf(0) }
    var assigningId by remember { mutableStateOf<String?>(null) }
    var previewTableId by remember { mutableStateOf<String?>(null) }
    var flashTableId by remember { mutableStateOf<String?>(null) }
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(30_000)
        }
    }

    LaunchedEffect(flashTableId) {
        if (flashTableId != null) {
            delay(850)
            flashTableId = null
        }
    }

    val activeFloor = floors.firstOrNull { it.id == activeFloorId } ?: floors.first()
    val tables = activeFloor.tables
    val dayReservations = reservations.filter { it.dayOffset == dayOffset }
    val pending = dayReservations.filter { it.type == ReservationType.Request }
    val confirmed = dayReservations.filter { it.type == ReservationType.Confirmed }
    val requestCount = reservations.count { it.type == ReservationType.Request }
    val assigningRez = assigningId?.let { id -> reservations.firstOrNull { it.id == id } }
    val previewTable = previewTableId?.let { id -> tables.firstOrNull { it.id == id } }
    val isToday = dayOffset == 0
    val nowHour = hourOfDay(nowMillis)
    val showNowLine = isToday && nowHour >= startHour && nowHour <= startHour + windowHours
    val nowPercent = ((nowHour - startHour) / windowHours).coerceIn(0f, 1f)

    fun clampStart(value: Float, window: Float = windowHours): Float =
        value.coerceIn(0f, MaxWindowHours - window)

    fun focusReservation(reservation: Reservation) {
        val start = timeToHour(reservation.startTime)
        dayOffset = reservation.dayOffset
        startHour = clampStart(start - windowHours / 3f)
    }

    fun cancelAssign() {
        assigningId = null
        previewTableId = null
    }

    fun startAssign(reservation: Reservation) {
        focusReservation(reservation)
        assigningId = reservation.id
        previewTableId = null
        panelOpen = false
    }

    fun approveRequest(reservation: Reservation) {
        onAccept(reservation)
        focusReservation(reservation)
        if (reservation.tableId.isBlank()) {
            assigningId = reservation.id
            previewTableId = null
            panelOpen = false
        } else {
            assigningId = null
            previewTableId = null
            flashTableId = reservation.tableId
        }
    }

    fun isTableAvailable(tableId: String, reservation: Reservation): Boolean {
        val table = tables.firstOrNull { it.id == tableId } ?: return false
        if (table.seats < reservation.partySize) return false
        val start = timeToHour(reservation.startTime)
        val end = start + reservation.durationHours.toFloat()
        return reservations.none { other ->
            other.id != reservation.id &&
                other.dayOffset == reservation.dayOffset &&
                other.tableId == tableId &&
                other.status != "COMPLETED" &&
                start < timeToHour(other.startTime) + other.durationHours.toFloat() &&
                end > timeToHour(other.startTime)
        }
    }

    fun zoomBy(delta: Float) {
        val next = (windowHours + delta).coerceIn(MinWindowHours, MaxWindowHours)
        val center = startHour + windowHours / 2f
        windowHours = next
        startHour = clampStart(center - next / 2f, next)
    }

    fun goToNow() {
        dayOffset = 0
        startHour = clampStart(nowHour - windowHours / 3f)
    }

    Box(Modifier.fillMaxSize().background(palette.bg)) {
        Column(Modifier.fillMaxSize()) {
            CalendarDateBar(
                palette = palette,
                dayOffset = dayOffset,
                requestCount = requestCount,
                isToday = isToday,
                reservationCount = dayReservations.size,
                windowHours = windowHours,
                isMobile = isMobile,
                onMenuClick = { panelOpen = true },
                onPreviousDay = { dayOffset -= 1 },
                onNextDay = { dayOffset += 1 },
                onOpenPicker = {
                    pickerMonthOffset = dayOffset
                    datePickerOpen = !datePickerOpen
                },
                onZoomIn = { zoomBy(-WindowStepHours) },
                onZoomOut = { zoomBy(WindowStepHours) },
                onNow = { goToNow() },
            )

            CalendarLegend(palette)

            assigningRez?.let { reservation ->
                AssignBanner(
                    palette = palette,
                    reservation = reservation,
                    previewTableLabel = previewTable?.label,
                    onCancel = { cancelAssign() },
                    onConfirm = {
                        val tableId = previewTableId
                        if (tableId != null) {
                            onAssignTable(reservation.id, tableId)
                            flashTableId = tableId
                            cancelAssign()
                        }
                    },
                )
            }

            Row(Modifier.weight(1f).fillMaxWidth()) {
                CalendarTimeline(
                    palette = palette,
                    tables = tables,
                    dayReservations = dayReservations,
                    allReservations = reservations,
                    startHour = startHour,
                    windowHours = windowHours,
                    assigningRez = assigningRez,
                    previewTableId = previewTableId,
                    flashTableId = flashTableId,
                    showNowLine = showNowLine,
                    nowPercent = nowPercent,
                    nowHour = nowHour,
                    isToday = isToday,
                    isTableAvailable = ::isTableAvailable,
                    onPickTable = { previewTableId = it },
                    modifier = Modifier.weight(1f).fillMaxSize(),
                )

                if (!isMobile) {
                    Box(Modifier.width(288.dp).fillMaxHeight().background(palette.card).border(1.dp, palette.border)) {
                        CalendarPanel(
                            palette = palette,
                            pending = pending,
                            confirmed = confirmed,
                            onApprove = { approveRequest(it) },
                            onAssign = { startAssign(it) },
                            onDecline = onDecline,
                            onClose = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }

        if (datePickerOpen) {
            Box(
                Modifier
                    .fillMaxSize()
                    .zIndex(20f)
                    .clickable { datePickerOpen = false },
            )
            CalendarDatePickerPopup(
                palette = palette,
                selectedOffset = dayOffset,
                pickerMonthOffset = pickerMonthOffset,
                reservations = reservations,
                onMonthOffsetChange = { pickerMonthOffset = it },
                onSelectOffset = {
                    dayOffset = it
                    datePickerOpen = false
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 52.dp)
                    .zIndex(21f),
            )
        }

        AnimatedVisibility(
            visible = panelOpen,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .clickable { panelOpen = false },
            )
        }
        AnimatedVisibility(
            visible = panelOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
        ) {
            CalendarPanel(
                palette = palette,
                pending = pending,
                confirmed = confirmed,
                onApprove = { approveRequest(it) },
                onAssign = { startAssign(it) },
                onDecline = onDecline,
                onClose = { panelOpen = false },
                modifier = Modifier.fillMaxHeight().width(if (isMobile) 320.dp else 360.dp),
            )
        }
    }
}
