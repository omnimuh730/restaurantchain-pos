package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

/**
 * Edit-mode chrome wrapping `FloorCanvas`. On tablet/desktop the inspector
 * lives in a left rail; on mobile it slides up as a bottom sheet (mirrors the
 * React `mobileEditDrawer`). Selecting a table while on mobile auto-opens the
 * sheet, and tapping the canvas background dismisses it.
 */
@Composable
fun FloorEditMode(
    palette: FloorPalette,
    state: FloorPlanState,
) {
    val isMobile = rememberIsMobile()
    var mobileDrawerOpen by remember { mutableStateOf(false) }

    LaunchedEffect(state.selectedTableId, isMobile) {
        if (isMobile && state.selectedTableId != null) mobileDrawerOpen = true
    }
    // Zoom controls live in the top-right corner of the canvas in both edit
    // and preview modes, so they don't need to dance around the bottom drawer
    // any more — a simple top/end inset is enough.
    val zoomControlsTopPadding = 12.dp
    val zoomControlsEndPadding = 12.dp

    Column(Modifier.fillMaxSize().background(palette.editBg)) {
        EditTopBar(palette, state, isMobile)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            Row(Modifier.fillMaxSize()) {
                if (!isMobile) {
                    EditSidebar(palette, state, Modifier.width(232.dp).fillMaxHeight())
                }
                FloorCanvas(
                    palette = palette,
                    tables = state.tables,
                    editMode = true,
                    selectedTableId = state.selectedTableId,
                    showSeats = state.showSeats,
                    zoom = state.zoom,
                    onZoomChange = { state.zoom = it },
                    onSelectTable = {
                        state.selectedTableId = it
                        if (isMobile) mobileDrawerOpen = it != null
                    },
                    onDragTable = { id, x, y, commit ->
                        if (commit) {
                            state.updateTables(commit = true) { list -> list }
                        } else {
                            state.updateTables(commit = false) { list ->
                                list.map { if (it.id == id) it.copy(x = x, y = y) else it }
                            }
                        }
                    },
                    zoomControlsTopPadding = zoomControlsTopPadding,
                    zoomControlsEndPadding = zoomControlsEndPadding,
                    modifier = Modifier.weight(1f),
                )
            }

            if (isMobile) {
                val fabBottom: androidx.compose.ui.unit.Dp by animateDpAsState(
                    targetValue = if (mobileDrawerOpen) 372.dp else 48.dp,
                    animationSpec = tween(durationMillis = 240),
                    label = "fab-bottom",
                )
                MobileFab(
                    onClick = { state.addTable() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = fabBottom),
                )
                MobileEditDrawer(
                    palette = palette,
                    state = state,
                    open = mobileDrawerOpen,
                    onToggle = { mobileDrawerOpen = !mobileDrawerOpen },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}
