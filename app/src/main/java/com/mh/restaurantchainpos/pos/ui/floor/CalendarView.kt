package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainpos.pos.data.Floor
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.data.ReservationType
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private const val BusinessOpen = 11f
private const val BusinessClose = 23f
private const val MinWindowHours = 4f
private const val MaxWindowHours = 24f
private const val WindowStepHours = 2f
private const val DayMillis = 86_400_000L

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

@Composable
private fun CalendarDateBar(
    palette: FloorPalette,
    dayOffset: Int,
    requestCount: Int,
    isToday: Boolean,
    reservationCount: Int,
    windowHours: Float,
    isMobile: Boolean,
    onMenuClick: () -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onOpenPicker: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onNow: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(palette.bg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Menu, contentDescription = "Reservations", tint = palette.text2, modifier = Modifier.size(20.dp))
            if (requestCount > 0) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Amber500),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(requestCount.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, palette, onPreviousDay)
        Row(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onOpenPicker)
                .padding(horizontal = 10.dp, vertical = 7.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = palette.text2, modifier = Modifier.size(if (isMobile) 0.dp else 15.dp))
            if (!isMobile) Spacer(Modifier.width(6.dp))
            Text(
                if (isMobile) shortDateLabel(dayOffset) else fullDateLabel(dayOffset),
                color = palette.text1,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (isToday) {
                Spacer(Modifier.width(7.dp))
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(Red500.copy(alpha = 0.12f))
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text("Today", color = Red500, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            } else if (!isMobile && reservationCount > 0) {
                Spacer(Modifier.width(7.dp))
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(palette.occupiedFill)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text(reservationCount.toString(), color = palette.occupiedText, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowRight, palette, onNextDay)
        Spacer(Modifier.width(6.dp))
        IconTinyButton(Icons.Outlined.Add, palette, onZoomIn, enabled = windowHours > MinWindowHours)
        NowButton(onNow)
        IconTinyButton(Icons.Outlined.Remove, palette, onZoomOut, enabled = windowHours < MaxWindowHours)
        if (!isMobile) {
            Spacer(Modifier.width(4.dp))
            Text("${windowHours.toInt()}h", color = palette.text3, fontSize = 10.sp)
        }
    }
}

@Composable
private fun CalendarLegend(palette: FloorPalette) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(palette.bg)
            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        LegendBlock(
            fill = palette.occupiedFill,
            border = palette.occupiedBorder,
            dashed = false,
            label = "Confirmed",
            palette = palette,
        )
        LegendBlock(
            fill = palette.reservedFill,
            border = palette.reservedBorder,
            dashed = true,
            label = "Request",
            palette = palette,
        )
    }
}

@Composable
private fun LegendBlock(fill: Color, border: Color, dashed: Boolean, label: String, palette: FloorPalette) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        val borderModifier = if (dashed) {
            Modifier.dashedBorder(border, 1.5.dp, 4.dp)
        } else {
            Modifier.border(1.5.dp, border, RoundedCornerShape(4.dp))
        }
        Box(
            Modifier
                .size(width = 16.dp, height = 10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(fill)
                .then(borderModifier),
        )
        Text(label, color = palette.text3, fontSize = 10.sp)
    }
}

@Composable
private fun CalendarTimeline(
    palette: FloorPalette,
    tables: List<FloorTable>,
    dayReservations: List<Reservation>,
    allReservations: List<Reservation>,
    startHour: Float,
    windowHours: Float,
    assigningRez: Reservation?,
    previewTableId: String?,
    flashTableId: String?,
    showNowLine: Boolean,
    nowPercent: Float,
    nowHour: Float,
    isToday: Boolean,
    isTableAvailable: (String, Reservation) -> Boolean,
    onPickTable: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelWidth = 96.dp
    val rowHeight = if (rememberIsMobile()) 54.dp else 62.dp
    val timeLabels = remember(startHour, windowHours) { buildTimeLabels(startHour, windowHours) }

    Column(modifier.background(palette.bg)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(palette.bg)
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        ) {
            Spacer(Modifier.width(labelWidth))
            TimeHeader(palette, startHour, windowHours, timeLabels, Modifier.weight(1f).height(22.dp))
        }

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(tables, key = { it.id }) { table ->
                val isAvailableForAssign = assigningRez?.let { isTableAvailable(table.id, it) } == true
                val isPending = previewTableId == table.id
                val isFlashing = flashTableId == table.id
                Row(Modifier.fillMaxWidth().height(rowHeight), verticalAlignment = Alignment.CenterVertically) {
                    TableNameCell(
                        palette = palette,
                        table = table,
                        isAvailableForAssign = isAvailableForAssign,
                        isPending = isPending,
                        assigning = assigningRez != null,
                        modifier = Modifier.width(labelWidth).height(rowHeight),
                    )
                    BoxWithConstraints(
                        Modifier
                            .weight(1f)
                            .height(rowHeight)
                            .clip(RoundedCornerShape(6.dp))
                            .background(rowBackground(palette, assigningRez != null, isAvailableForAssign, isPending, isFlashing))
                            .then(rowBorder(palette, assigningRez != null, isAvailableForAssign, isPending, isFlashing))
                            .then(
                                if (assigningRez != null && isAvailableForAssign) {
                                    Modifier.clickable { onPickTable(table.id) }
                                } else {
                                    Modifier
                                },
                            ),
                    ) {
                        ClosedHoursOverlay(palette, startHour, windowHours)
                        TimelineGrid(palette, startHour, windowHours, timeLabels)
                        dayReservations
                            .filter { it.tableId == table.id }
                            .forEach { reservation ->
                                CalendarReservationBlock(
                                    palette = palette,
                                    reservation = reservation,
                                    allReservations = allReservations,
                                    startHour = startHour,
                                    windowHours = windowHours,
                                    nowHour = nowHour,
                                    isToday = isToday,
                                )
                            }
                        if (isPending && assigningRez != null) {
                            CalendarReservationBlock(
                                palette = palette,
                                reservation = assigningRez,
                                allReservations = allReservations,
                                startHour = startHour,
                                windowHours = windowHours,
                                nowHour = nowHour,
                                isToday = isToday,
                                preview = true,
                            )
                        }
                        if (showNowLine) {
                            NowLine(nowPercent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeHeader(
    palette: FloorPalette,
    startHour: Float,
    windowHours: Float,
    labels: List<TimeLabel>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        labels.forEach { label ->
            val pct = ((label.hour - startHour) / windowHours).coerceIn(0f, 1f)
            Text(
                label.label,
                color = palette.text2,
                fontSize = 12.sp,
                modifier = Modifier.offset(x = maxWidth * pct - 18.dp).align(Alignment.CenterStart),
            )
        }
    }
}

@Composable
private fun TableNameCell(
    palette: FloorPalette,
    table: FloorTable,
    isAvailableForAssign: Boolean,
    isPending: Boolean,
    assigning: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
            .background(if (isPending) Blue600 else Color.Transparent)
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            table.label,
            color = when {
                isPending -> Color.White
                assigning && isAvailableForAssign -> Blue600
                assigning -> palette.text3
                else -> palette.text1
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.width(3.dp))
        Text(
            "(${table.seats})",
            color = when {
                isPending -> Color.White.copy(alpha = 0.78f)
                assigning && isAvailableForAssign -> Blue600.copy(alpha = 0.72f)
                else -> palette.text2
            },
            fontSize = 10.sp,
        )
        if (assigning && isAvailableForAssign && !isPending) {
            Spacer(Modifier.width(3.dp))
            Box(Modifier.size(5.dp).clip(CircleShape).background(Blue600))
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.ClosedHoursOverlay(
    palette: FloorPalette,
    startHour: Float,
    windowHours: Float,
) {
    val endHour = startHour + windowHours
    val leftPct = ((BusinessOpen - startHour) / windowHours).coerceIn(0f, 1f)
    val rightPct = ((BusinessClose - startHour) / windowHours).coerceIn(0f, 1f)
    val closed = palette.availableBorder.copy(alpha = 0.36f)
    if (BusinessOpen > startHour) {
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .width(maxWidth * leftPct)
                .fillMaxHeight()
                .background(closed),
        )
    }
    if (BusinessClose < endHour) {
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .width(maxWidth * (1f - rightPct))
                .fillMaxHeight()
                .background(closed),
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.TimelineGrid(
    palette: FloorPalette,
    startHour: Float,
    windowHours: Float,
    labels: List<TimeLabel>,
) {
    labels.forEach { label ->
        val pct = ((label.hour - startHour) / windowHours)
        if (pct > 0f && pct < 1f) {
            Box(
                Modifier
                    .offset(x = maxWidth * pct)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(palette.border.copy(alpha = 0.72f)),
            )
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.CalendarReservationBlock(
    palette: FloorPalette,
    reservation: Reservation,
    allReservations: List<Reservation>,
    startHour: Float,
    windowHours: Float,
    nowHour: Float,
    isToday: Boolean,
    preview: Boolean = false,
) {
    val visibleStart = timeToHour(reservation.startTime)
    val visualState = blockVisualState(reservation, nowHour, isToday)
    val visibleEnd = when (visualState) {
        BlockVisualState.NoShow -> visibleStart + 20f / 60f
        else -> visibleStart + reservation.durationHours.toFloat()
    }
    val windowEnd = startHour + windowHours
    if (visibleEnd <= startHour || visibleStart >= windowEnd) return

    val leftPct = ((max(visibleStart, startHour) - startHour) / windowHours).coerceIn(0f, 1f)
    val rightPct = ((min(visibleEnd, windowEnd) - startHour) / windowHours).coerceIn(0f, 1f)
    val widthPct = (rightPct - leftPct).coerceAtLeast(0.03f)
    val visuals = reservationVisual(palette, reservation, visualState, preview, allReservations)
    val blockModifier = if (visuals.dashed) {
        Modifier.dashedBorder(visuals.border, visuals.strokeWidth, 5.dp)
    } else {
        Modifier.border(visuals.strokeWidth, visuals.border, RoundedCornerShape(5.dp))
    }

    Row(
        Modifier
            .offset(x = maxWidth * leftPct)
            .padding(top = 4.dp, bottom = 4.dp)
            .width(maxWidth * widthPct)
            .fillMaxHeight()
            .clip(RoundedCornerShape(5.dp))
            .background(visuals.fill)
            .then(blockModifier)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!preview && visualState !in listOf(BlockVisualState.Completed, BlockVisualState.NoShow)) {
            Text("||", color = palette.text2.copy(alpha = 0.42f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    reservation.guestName,
                    color = visuals.text,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                val tag = blockTag(reservation, visualState)
                if (tag.isNotBlank()) {
                    Spacer(Modifier.width(4.dp))
                    Text(tag, color = visuals.text.copy(alpha = 0.85f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (!preview) {
                Text(
                    "${reservation.partySize}P - ${formatHours(reservation.durationHours)}",
                    color = visuals.subText,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.NowLine(nowPercent: Float) {
    val x = maxWidth * nowPercent
    Box(
        Modifier
            .offset(x = x)
            .width(2.dp)
            .fillMaxHeight()
            .background(Red500)
            .zIndex(10f),
    )
    Box(
        Modifier
            .offset(x = x - 3.dp, y = (-3).dp)
            .size(8.dp)
            .clip(CircleShape)
            .background(Red500)
            .zIndex(11f),
    )
}

@Composable
private fun AssignBanner(
    palette: FloorPalette,
    reservation: Reservation,
    previewTableLabel: String?,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val confirmPhase = previewTableLabel != null
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Blue500.copy(alpha = 0.08f))
            .border(1.dp, Blue500.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Outlined.GpsFixed, contentDescription = null, tint = Blue600, modifier = Modifier.size(16.dp))
        Text(
            if (confirmPhase) {
                "Confirm ${reservation.guestName} -> $previewTableLabel"
            } else {
                "Assign ${reservation.guestName} (${reservation.partySize}P, ${reservation.startTime}) - click a highlighted table row"
            },
            color = palette.text1,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (confirmPhase) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Blue600)
                    .clickable(onClick = onConfirm)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(5.dp))
                Text("Confirm", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        IconCircleButton(Icons.Outlined.Close, palette, onCancel)
    }
}

@Composable
private fun CalendarPanel(
    palette: FloorPalette,
    pending: List<Reservation>,
    confirmed: List<Reservation>,
    onApprove: (Reservation) -> Unit,
    onAssign: (Reservation) -> Unit,
    onDecline: (Reservation) -> Unit,
    onClose: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier.background(palette.card).border(1.dp, palette.border)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Reservations", color = palette.text1, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.weight(1f))
            onClose?.let {
                IconCircleButton(Icons.Outlined.Close, palette, it)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
        SectionHeader(palette, "Requests", pending.size, palette.reservedBorder, palette.reservedFill)
        LazyColumn(Modifier.weight(1f)) {
            items(pending, key = { it.id }) { reservation ->
                RequestCard(
                    palette = palette,
                    reservation = reservation,
                    onApprove = { onApprove(reservation) },
                    onDecline = { onDecline(reservation) },
                )
            }
            item { SectionHeader(palette, "Confirmed", confirmed.size, palette.occupiedBorder, palette.occupiedFill) }
            items(confirmed, key = { it.id }) { reservation ->
                ConfirmedRow(palette = palette, reservation = reservation, onAssign = { onAssign(reservation) })
            }
            if (pending.isEmpty() && confirmed.isEmpty()) {
                item {
                    Text(
                        "No reservations on this day.",
                        color = palette.text3,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(palette: FloorPalette, title: String, count: Int, dotColor: Color, fill: Color) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.width(8.dp))
        Text(title, color = palette.text1, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Box(Modifier.clip(CircleShape).background(fill).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(count.toString(), color = dotColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RequestCard(
    palette: FloorPalette,
    reservation: Reservation,
    onApprove: () -> Unit,
    onDecline: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(palette.raised)
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(palette.reservedBorder))
            Spacer(Modifier.width(8.dp))
            Text(reservation.guestName, color = palette.text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Row(
                Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .background(Blue500.copy(alpha = 0.12f))
                    .clickable(onClick = onApprove)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Blue600, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(4.dp))
                Text("Approve", color = Blue600, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(6.dp))
            IconCircleButton(Icons.Outlined.Close, palette, onDecline)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipText(palette, dayLabel(reservation.dayOffset))
            ChipText(palette, reservation.startTime)
            ChipText(palette, "${reservation.partySize}P")
            ChipText(palette, formatHours(reservation.durationHours))
            if (reservation.tableId.isNotBlank()) {
                ChipText(palette, "Table ${reservation.tableId.removePrefix("T")}", emphasize = true)
            }
        }
    }
}

@Composable
private fun ConfirmedRow(palette: FloorPalette, reservation: Reservation, onAssign: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onAssign)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(palette.occupiedBorder))
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(reservation.guestName, color = palette.text1, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
                "${reservation.startTime} - ${reservation.partySize}P - ${formatHours(reservation.durationHours)}",
                color = palette.text2,
                fontSize = 11.sp,
            )
        }
        if (reservation.tableId.isNotBlank()) {
            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Blue500.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                Text("Table ${reservation.tableId.removePrefix("T")}", color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ChipText(palette: FloorPalette, text: String, emphasize: Boolean = false) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (emphasize) Blue500.copy(alpha = 0.12f) else palette.card)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text,
            color = if (emphasize) Blue600 else palette.text2,
            fontSize = 11.sp,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun CalendarDatePickerPopup(
    palette: FloorPalette,
    selectedOffset: Int,
    pickerMonthOffset: Int,
    reservations: List<Reservation>,
    onMonthOffsetChange: (Int) -> Unit,
    onSelectOffset: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val base = calendarForOffset(pickerMonthOffset)
    val year = base.get(Calendar.YEAR)
    val month = base.get(Calendar.MONTH)
    val monthGrid = remember(year, month) { buildCalendarMonth(year, month) }
    val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.US).format(base.time)
    val selectedDate = calendarForOffset(selectedOffset)

    Column(
        modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.card)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, palette, onClick = {
                val prev = calendarForOffset(pickerMonthOffset)
                prev.add(Calendar.MONTH, -1)
                onMonthOffsetChange(offsetForCalendar(prev))
            })
            Text(
                monthLabel,
                color = palette.text1,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconCircleButton(Icons.AutoMirrored.Outlined.KeyboardArrowRight, palette, onClick = {
                val next = calendarForOffset(pickerMonthOffset)
                next.add(Calendar.MONTH, 1)
                onMonthOffsetChange(offsetForCalendar(next))
            })
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach {
                Box(Modifier.weight(1f).height(22.dp), contentAlignment = Alignment.Center) {
                    Text(it, color = palette.text3, fontSize = 10.sp)
                }
            }
        }
        monthGrid.forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    if (day == null) {
                        Spacer(Modifier.weight(1f).height(34.dp))
                    } else {
                        val offset = offsetForDate(year, month, day)
                        val isSelected = sameYmd(selectedDate, year, month, day)
                        val isToday = offset == 0
                        val hasReservation = reservations.any { it.dayOffset == offset }
                        Box(
                            Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> Blue600
                                        isToday -> Red500.copy(alpha = 0.10f)
                                        else -> Color.Transparent
                                    },
                                )
                                .clickable { onSelectOffset(offset) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                day.toString(),
                                color = when {
                                    isSelected -> Color.White
                                    isToday -> Red500
                                    else -> palette.text1
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            )
                            if (hasReservation && !isSelected) {
                                Box(
                                    Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 3.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Blue600),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconCircleButton(icon: ImageVector, palette: FloorPalette, onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(palette.raised)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = palette.text2, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun IconTinyButton(icon: ImageVector, palette: FloorPalette, onClick: () -> Unit, enabled: Boolean = true) {
    Box(
        Modifier
            .size(28.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) palette.text2 else palette.text3.copy(alpha = 0.45f), modifier = Modifier.size(15.dp))
    }
}

@Composable
private fun NowButton(onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Red500.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("Now", color = Red500, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun rowBackground(
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

private fun rowBorder(
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

private data class TimeLabel(val label: String, val hour: Float)

private fun buildTimeLabels(startHour: Float, windowHours: Float): List<TimeLabel> {
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

private enum class BlockVisualState { OnTime, Completed, NoShow }

private data class ReservationVisual(
    val fill: Color,
    val border: Color,
    val text: Color,
    val subText: Color,
    val dashed: Boolean,
    val strokeWidth: Dp,
)

private fun blockVisualState(reservation: Reservation, @Suppress("UNUSED_PARAMETER") nowHour: Float, @Suppress("UNUSED_PARAMETER") isToday: Boolean): BlockVisualState =
    when (reservation.status) {
        "COMPLETED" -> BlockVisualState.Completed
        "NO_SHOW" -> BlockVisualState.NoShow
        else -> BlockVisualState.OnTime
    }

private fun reservationVisual(
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
        fill = palette.reservedFill,
        border = palette.reservedBorder,
        text = palette.text1,
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

private fun blockTag(reservation: Reservation, state: BlockVisualState): String = when {
    state == BlockVisualState.Completed -> "PAID"
    state == BlockVisualState.NoShow -> "NO-SHOW"
    reservation.type == ReservationType.Request -> "REQ"
    else -> ""
}

private fun Modifier.dashedBorder(color: Color, strokeWidth: Dp, cornerRadius: Dp): Modifier =
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

private fun timeToHour(time: String): Float {
    val hour = time.substringBefore(":").toFloatOrNull() ?: 0f
    val minute = time.substringAfter(":", "0").toFloatOrNull() ?: 0f
    return hour + minute / 60f
}

private fun hourOfDay(ms: Long): Float {
    val cal = Calendar.getInstance().apply { timeInMillis = ms }
    return cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60f
}

private fun calendarForOffset(offset: Int): Calendar =
    Calendar.getInstance().zeroTime().apply { add(Calendar.DATE, offset) }

private fun offsetForCalendar(calendar: Calendar): Int =
    ((calendar.cloneAsCalendar().zeroTime().timeInMillis - Calendar.getInstance().zeroTime().timeInMillis) / DayMillis).toInt()

private fun offsetForDate(year: Int, month: Int, day: Int): Int =
    offsetForCalendar(Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        zeroTime()
    })

private fun Calendar.zeroTime(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}

private fun Calendar.cloneAsCalendar(): Calendar =
    (clone() as Calendar)

private fun buildCalendarMonth(year: Int, month: Int): List<List<Int?>> {
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

private fun sameYmd(calendar: Calendar, year: Int, month: Int, day: Int): Boolean =
    calendar.get(Calendar.YEAR) == year &&
        calendar.get(Calendar.MONTH) == month &&
        calendar.get(Calendar.DAY_OF_MONTH) == day

private fun fullDateLabel(offset: Int): String =
    SimpleDateFormat("EEE, MMM d, yyyy", Locale.US).format(Date(calendarForOffset(offset).timeInMillis))

private fun shortDateLabel(offset: Int): String =
    SimpleDateFormat("M/d", Locale.US).format(Date(calendarForOffset(offset).timeInMillis))

private fun dayLabel(offset: Int): String = when (offset) {
    -1 -> "Yesterday"
    0 -> "Today"
    1 -> "Tomorrow"
    else -> if (offset < 0) "${-offset}d ago" else "in ${offset}d"
}

private fun formatHours(hours: Double): String {
    val intPart = hours.toInt()
    val frac = hours - intPart
    return if (frac == 0.0) "${intPart}h" else "${intPart}.${(frac * 10).toInt()}h"
}

private fun Int.floorMod(mod: Int): Int {
    val r = this % mod
    return if (r < 0) r + mod else r
}
