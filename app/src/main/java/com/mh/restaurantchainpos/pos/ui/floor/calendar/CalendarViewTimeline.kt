package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.data.Reservation
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import androidx.compose.ui.graphics.luminance
import kotlin.math.abs
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
    reassignDrag: ReassignDrag?,
    showNowLine: Boolean,
    nowPercent: Float,
    nowHour: Float,
    isToday: Boolean,
    isTableAvailable: (String, Reservation) -> Boolean,
    onPickTable: (String) -> Unit,
    onWindowPan: (Float) -> Unit,
    onTableRowRectUpdate: (String, Rect) -> Unit,
    onReassignDragStart: (Reservation, Rect) -> Unit,
    onReassignDragMove: (deltaY: Float) -> Unit,
    onReassignDragEnd: () -> Unit,
    onReassignDragCancel: () -> Unit,
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
    // Row-body horizontal pan. Custom direction-aware pointerInput that
    // explicitly DEFERS to the reservation block's vertical-dominant slop
    // wait: it only consumes once horizontal motion has been the dominant
    // axis past touch slop, and bails immediately if the user is doing a
    // vertical drag (so the block's reassign gesture, or the ancestor
    // `verticalScroll` over empty rows, can claim instead). Standard
    // `Modifier.draggable(Horizontal)` here would race the block's wait
    // and steal vertical drags. While a reassign drag is in flight we
    // disable pan entirely so the world geometry stays stable for the
    // ghost / drop-target math.
    val onWindowPanState = rememberUpdatedState(onWindowPan)
    val windowHoursState = rememberUpdatedState(windowHours)
    val reassignDragState = rememberUpdatedState(reassignDrag)
    val rowHorizontalPanModifier: Modifier = Modifier.pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            if (reassignDragState.value != null) return@awaitEachGesture
            val slop = viewConfiguration.touchSlop
            var totalDx = 0f
            var totalDy = 0f
            var claimed = false
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Main)
                val change = event.changes.firstOrNull { it.id == down.id }
                    ?: return@awaitEachGesture
                if (!change.pressed) return@awaitEachGesture
                if (change.isConsumed || reassignDragState.value != null) return@awaitEachGesture
                val delta = change.positionChange()
                totalDx += delta.x
                totalDy += delta.y
                val absDx = abs(totalDx)
                val absDy = abs(totalDy)
                if (absDx > slop && absDx > absDy) {
                    change.consume()
                    val w = timelineWidthPx
                    if (w > 1f) {
                        onWindowPanState.value(
                            -(totalDx / w) * windowHoursState.value,
                        )
                    }
                    claimed = true
                    break
                }
                if (absDy > slop && absDy >= absDx) {
                    return@awaitEachGesture
                }
            }
            if (!claimed) return@awaitEachGesture
            drag(down.id) { change ->
                if (reassignDragState.value != null) return@drag
                val w = timelineWidthPx
                if (w > 1f) {
                    onWindowPanState.value(
                        -(change.positionChange().x / w) * windowHoursState.value,
                    )
                }
                change.consume()
            }
        }
    }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    // We read these state holders inside the gesture coroutine *without*
    // keying `pointerInput` on their identity — keying on them would tear the
    // running coroutine down mid-drag (see comment on the gesture below) and
    // leave reassignDrag stuck without ever firing onDragEnd/onDragCancel.
    val assigningRezState = rememberUpdatedState(assigningRez)
    var dragOverlayCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val onStart by rememberUpdatedState(onReassignDragStart)
    val onMove by rememberUpdatedState(onReassignDragMove)
    val onEnd by rememberUpdatedState(onReassignDragEnd)
    val onCancelDrag by rememberUpdatedState(onReassignDragCancel)

    Column(modifier.background(palette.bg)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(palette.bg)
                .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
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
        // Horizontal time-window pan lives only on the header + this strip so it
        // never competes with vertical slot reassignment on table rows.
        Row(
            Modifier
                .fillMaxWidth()
                .background(palette.bg)
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(labelWidth))
            Box(
                Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(palette.border.copy(alpha = 0.28f))
                    .then(horizontalPanModifier),
            )
        }

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .onGloballyPositioned { dragOverlayCoords = it },
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    // Keep the scroll modifier structurally stable while a slot
                    // drag is active; the slot pointerInput consumes drag
                    // movement, so the list will not scroll during reassignment.
                    .verticalScroll(scrollState)
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                for (table in tables) {
                    val activeReservation = assigningRez ?: reassignDrag?.reservation
                    val inTablePickMode = activeReservation != null
                    val isAvailableForAssign = activeReservation?.let { isTableAvailable(table.id, it) } == true
                    val isPending = when {
                        assigningRez != null -> previewTableId == table.id
                        reassignDrag != null -> reassignDrag.hoverTableId == table.id
                        else -> false
                    }
                    val isFlashing = flashTableId == table.id
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(rowHeight)
                            .onGloballyPositioned { coords ->
                                onTableRowRectUpdate(table.id, coords.boundsInRoot())
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TableNameCell(
                            palette = palette,
                            table = table,
                            isAvailableForAssign = isAvailableForAssign,
                            isPending = isPending,
                            assigning = inTablePickMode,
                            modifier = Modifier.width(labelWidth).height(rowHeight),
                        )
                        BoxWithConstraints(
                            Modifier
                                .weight(1f)
                                .height(rowHeight)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (inTablePickMode || isPending || isFlashing) {
                                        rowBackground(palette, inTablePickMode, isAvailableForAssign, isPending, isFlashing)
                                    } else {
                                        // Outside operating hours: same tone as the calendar page (disabled).
                                        palette.bg
                                    },
                                )
                                .then(rowBorder(palette, inTablePickMode, isAvailableForAssign, isPending, isFlashing))
                                .then(
                                    if (assigningRez != null && isAvailableForAssign) {
                                        Modifier.clickable { onPickTable(table.id) }
                                    } else {
                                        Modifier
                                    },
                                )
                                .then(rowHorizontalPanModifier),
                        ) {
                            if (!inTablePickMode && !isPending && !isFlashing) {
                                OperatingHoursOpenStrip(
                                    palette = palette,
                                    startHour = startHour,
                                    windowHours = windowHours,
                                )
                            }
                            TimelineGrid(palette, startHour, windowHours, timeLabels)
                            dayReservations
                                .filter { it.tableId == table.id }
                                .forEach { reservation ->
                                    key(reservation.id) {
                                        var blockLayoutCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
                                        val dimmed = reassignDrag?.reservation?.id == reservation.id
                                        val interactionModifier = Modifier
                                            .onGloballyPositioned { blockLayoutCoords = it }
                                            // IMPORTANT: keys must stay stable for the entire lifetime of
                                                // a drag. Earlier we keyed on `reassignDrag?.reservation?.id`
                                                // which flipped from null to this-id the instant onStart fired,
                                                // tearing the running coroutine down before onDragEnd could
                                                // run. The result was a "ghost" assign-mode display that the
                                                // user could only escape by tapping the chip a second time.
                                                .pointerInput(reservation.id) {
                                                    if (!calendarReservationIsReassignable(reservation)) return@pointerInput
                                                    awaitEachGesture {
                                                        val down = awaitFirstDown(requireUnconsumed = false)
                                                        // Re-check at the point of the down event — `pointerInput`
                                                        // is NOT keyed on these, so we read them via
                                                        // rememberUpdatedState to get the live value.
                                                        if (assigningRezState.value != null) return@awaitEachGesture
                                                        val active = reassignDragState.value
                                                        if (active != null && active.reservation.id != reservation.id) return@awaitEachGesture

                                                        // A down that starts on a reassignable slot belongs to
                                                        // that slot. Start immediately instead of waiting for
                                                        // slop so the ancestor vertical scroll and row time-pan
                                                        // recognizers cannot steal the first drag frames.
                                                        val coords = blockLayoutCoords ?: return@awaitEachGesture
                                                        down.consume()
                                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        onStart(reservation, coords.boundsInRoot())
                                                        var completed = false
                                                        try {
                                                            while (true) {
                                                                val event = awaitPointerEvent(PointerEventPass.Main)
                                                                val change = event.changes.firstOrNull { it.id == down.id }
                                                                    ?: return@awaitEachGesture
                                                                if (!change.pressed) {
                                                                    completed = true
                                                                    break
                                                                }
                                                                val delta = change.position - change.previousPosition
                                                                if (delta != Offset.Zero) {
                                                                    onMove(delta.y)
                                                                }
                                                                change.consume()
                                                            }
                                                        } finally {
                                                            // try/finally guarantees state always clears, even
                                                            // if the coroutine is cancelled externally.
                                                            if (completed) onEnd() else onCancelDrag()
                                                        }
                                                    }
                                                }
                                        CalendarReservationBlock(
                                            palette = palette,
                                            reservation = reservation,
                                            allReservations = allReservations,
                                            startHour = startHour,
                                            windowHours = windowHours,
                                            nowHour = nowHour,
                                            isToday = isToday,
                                            blockInteractionModifier = interactionModifier,
                                            dimmed = dimmed,
                                        )
                                    }
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

            reassignDrag?.let { drag ->
                val overlay = dragOverlayCoords
                if (overlay != null && overlay.isAttached) {
                    val overlayBounds = overlay.boundsInRoot()
                    val ghostTopLeftRoot = Offset(
                        drag.blockRectRoot.left,
                        drag.blockRectRoot.top + drag.accumulatedY,
                    )
                    val topLeftInOverlay = ghostTopLeftRoot - overlayBounds.topLeft
                    val wDp = with(density) { drag.blockRectRoot.width.toDp() }
                    val hDp = with(density) { drag.blockRectRoot.height.toDp() }
                    val xDp = with(density) { topLeftInOverlay.x.toDp() }
                    val yDp = with(density) { topLeftInOverlay.y.toDp() }
                    ReassignDragGhost(
                        palette = palette,
                        reservation = drag.reservation,
                        allReservations = allReservations,
                        nowHour = nowHour,
                        isToday = isToday,
                        modifier = Modifier
                            .zIndex(36f)
                            .offset(x = xDp, y = yDp)
                            .width(wDp)
                            .height(hDp),
                    )
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

/**
 * Fills only the intersection of [BusinessOpen]..[BusinessClose] with the
 * visible window with a light "bookable" surface; the row root is already
 * [FloorPalette.bg] so hours outside that range read as disabled gray.
 */
@Composable
private fun BoxWithConstraintsScope.OperatingHoursOpenStrip(
    palette: FloorPalette,
    startHour: Float,
    windowHours: Float,
) {
    val windowEnd = startHour + windowHours
    val openStart = max(startHour, BusinessOpen)
    val openEnd = min(windowEnd, BusinessClose)
    if (openEnd <= openStart) return
    val leftPct = (openStart - startHour) / windowHours
    val widthPct = (openEnd - openStart) / windowHours
    val openFill = if (palette.bg.luminance() < 0.18f) {
        palette.raised
    } else {
        Color.White
    }
    Box(
        Modifier
            .offset(x = maxWidth * leftPct)
            .width(maxWidth * widthPct)
            .fillMaxHeight()
            .background(openFill),
    )
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
    blockInteractionModifier: Modifier = Modifier,
    dimmed: Boolean = false,
) {
    val ctx = LocalContext.current
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
            .graphicsLayer { alpha = if (dimmed) 0.38f else 1f }
            .then(blockInteractionModifier)
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
                val tag = ctx.reservationBlockTag(reservation, visualState)
                if (tag.isNotBlank()) {
                    Spacer(Modifier.width(4.dp))
                    Text(tag, color = visuals.text.copy(alpha = 0.85f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (!preview) {
                val partyLabel = stringResource(R.string.floor_cal_party_size, reservation.partySize)
                val dur = reservationDurationHoursLabel(reservation.durationHours)
                Text(
                    stringResource(R.string.floor_cal_party_duration_line, partyLabel, dur),
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
private fun ReassignDragGhost(
    palette: FloorPalette,
    reservation: Reservation,
    allReservations: List<Reservation>,
    nowHour: Float,
    isToday: Boolean,
    modifier: Modifier = Modifier,
) {
    val visualState = blockVisualState(reservation, nowHour, isToday)
    val visuals = reservationVisual(palette, reservation, visualState, preview = false, allReservations)
    val blockModifier = if (visuals.dashed) {
        Modifier.dashedBorder(visuals.border, visuals.strokeWidth, 5.dp)
    } else {
        Modifier.border(visuals.strokeWidth, visuals.border, RoundedCornerShape(5.dp))
    }
    Row(
        modifier
            .graphicsLayer { alpha = 0.95f }
            .clip(RoundedCornerShape(5.dp))
            .background(visuals.fill)
            .then(blockModifier)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                reservation.guestName,
                color = visuals.text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val partyLabel = stringResource(R.string.floor_cal_party_size, reservation.partySize)
            val dur = reservationDurationHoursLabel(reservation.durationHours)
            Text(
                stringResource(R.string.floor_cal_party_duration_line, partyLabel, dur),
                color = visuals.subText,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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

