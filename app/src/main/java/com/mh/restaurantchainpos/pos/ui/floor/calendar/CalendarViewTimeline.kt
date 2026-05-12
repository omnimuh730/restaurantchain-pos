package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun CalendarTimeline(
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
    onWindowPan: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelWidth = 96.dp
    val rowHeight = if (rememberIsMobile()) 54.dp else 62.dp
    val timeLabels = remember(startHour, windowHours) { buildTimeLabels(startHour, windowHours) }
    var timelineWidthPx by remember { mutableFloatStateOf(1f) }
    val horizontalPanState = rememberDraggableState { deltaPx ->
        if (timelineWidthPx > 1f) {
            onWindowPan(-(deltaPx / timelineWidthPx) * windowHours)
        }
    }
    val horizontalPanModifier = Modifier
        .onSizeChanged { timelineWidthPx = it.width.toFloat().coerceAtLeast(1f) }
        .draggable(
            state = horizontalPanState,
            orientation = Orientation.Horizontal,
        )

    Column(modifier.background(palette.bg)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(palette.bg)
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        ) {
            Spacer(Modifier.width(labelWidth))
            TimeHeader(
                palette,
                startHour,
                windowHours,
                timeLabels,
                Modifier
                    .weight(1f)
                    .height(22.dp)
                    .then(horizontalPanModifier),
            )
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
                            .then(horizontalPanModifier)
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
internal fun TimeHeader(
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
internal fun TableNameCell(
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
internal fun BoxWithConstraintsScope.ClosedHoursOverlay(
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
internal fun BoxWithConstraintsScope.TimelineGrid(
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
internal fun BoxWithConstraintsScope.CalendarReservationBlock(
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
internal fun BoxWithConstraintsScope.NowLine(nowPercent: Float) {
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
