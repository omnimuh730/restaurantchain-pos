package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.KitchenStatus
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun KitchenScreen(colors: PosColors, role: ActiveRole, onReceivedCount: (Int) -> Unit) {
    val state = rememberKitchenState()
    val received = state.receivedDistinctItemCount()
    val receivedOrders by remember {
        derivedStateOf { state.orders.count { it.status == KitchenStatus.Received } }
    }
    val inProgressOrders by remember {
        derivedStateOf {
            state.orders.count {
                it.status == KitchenStatus.InProgress &&
                    it.items.any { i -> !i.previouslyCompleted }
            }
        }
    }
    val completedOrders by remember {
        derivedStateOf {
            state.orders.count {
                it.status == KitchenStatus.Completed ||
                    (it.status == KitchenStatus.InProgress &&
                        it.items.any { i -> i.previouslyCompleted })
            }
        }
    }

    LaunchedEffect(received) { onReceivedCount(received) }
    val isMobile = rememberIsMobile()
    val tabCount = KitchenViewTab.entries.size
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = state.activeTab.ordinal.coerceIn(0, tabCount - 1),
        pageCount = { tabCount },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                val tab = KitchenViewTab.entries[page.coerceIn(0, tabCount - 1)]
                if (state.activeTab != tab) state.activeTab = tab
            }
    }

    Row(Modifier.fillMaxSize()) {
        if (!isMobile) {
            TableFilterPanel(
                colors = colors,
                selectedTables = state.selectedTables,
                onToggleTable = { tableId ->
                    if (state.selectedTables.contains(tableId)) state.selectedTables.remove(tableId)
                    else state.selectedTables.add(tableId)
                },
                onToggleFloor = { floor ->
                    val all = floor.tables.all { state.selectedTables.contains(it) }
                    if (all) state.selectedTables.removeAll(floor.tables.toSet())
                    else floor.tables.forEach { if (!state.selectedTables.contains(it)) state.selectedTables.add(it) }
                },
                onToggleAll = {
                    val allTables = PosMockData.kitchenFloors.flatMap { it.tables }
                    val all = allTables.all { state.selectedTables.contains(it) }
                    state.selectedTables.clear()
                    if (!all) state.selectedTables.addAll(allTables)
                },
            )
        }
        Box(Modifier.weight(1f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize()) {
                KitchenHeader(
                    colors = colors,
                    role = role,
                    state = state,
                    isMobile = isMobile,
                    receivedItems = received,
                    inProgressItems = state.inProgressDistinctItemCount(),
                    completedItems = state.completedDistinctItemCount(),
                    receivedOrders = receivedOrders,
                    inProgressOrders = inProgressOrders,
                    completedOrders = completedOrders,
                    selectedTabPage = pagerState.currentPage,
                    onSelectTabPage = { page ->
                        scope.launch { pagerState.animateScrollToPage(page.coerceIn(0, tabCount - 1)) }
                    },
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    beyondViewportPageCount = 1,
                    flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                ) { page ->
                    val tab = KitchenViewTab.entries[page.coerceIn(0, tabCount - 1)]
                    val visible = state.visibleOrdersForTab(tab)
                    KitchenContent(
                        colors = colors,
                        state = state,
                        isMobile = isMobile,
                        visible = visible,
                        viewTab = tab,
                    )
                }
            }

            if (isMobile) {
                TableFilterSidebar(
                    colors = colors,
                    open = state.sidebarOpen,
                    selectedTables = state.selectedTables,
                    onToggleTable = { tableId ->
                        if (state.selectedTables.contains(tableId)) state.selectedTables.remove(tableId)
                        else state.selectedTables.add(tableId)
                    },
                    onToggleFloor = { floor ->
                        val all = floor.tables.all { state.selectedTables.contains(it) }
                        if (all) state.selectedTables.removeAll(floor.tables.toSet())
                        else floor.tables.forEach { if (!state.selectedTables.contains(it)) state.selectedTables.add(it) }
                    },
                    onToggleAll = {
                        val allTables = PosMockData.kitchenFloors.flatMap { it.tables }
                        val all = allTables.all { state.selectedTables.contains(it) }
                        state.selectedTables.clear()
                        if (!all) state.selectedTables.addAll(allTables)
                    },
                    onClose = { state.sidebarOpen = false },
                )
            }

            val detail = state.detailOrderId?.let { id -> state.orders.firstOrNull { it.id == id } }
            if (detail != null) {
                val viewTab = KitchenViewTab.entries[pagerState.currentPage.coerceIn(0, tabCount - 1)]
                OrderDetailModal(
                    colors = colors,
                    order = detail,
                    allOrders = state.orders,
                    viewTab = viewTab,
                    onClose = { state.detailOrderId = null },
                    onAccept = state::acceptOrder,
                    onComplete = state::completeOrder,
                    onRecall = state::recallOrder,
                    onToggleItem = { orderId, itemId -> state.toggleItemDone(orderId, itemId) },
                    onSetItemQty = { orderId, itemId, count -> state.setItemSelectedQty(orderId, itemId, count) },
                )
            }
        }
    }
}
