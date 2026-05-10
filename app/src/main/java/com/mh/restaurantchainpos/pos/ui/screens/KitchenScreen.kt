package com.mh.restaurantchainpos.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.ui.kitchen.ByItemView
import com.mh.restaurantchainpos.pos.ui.kitchen.KitchenCard
import com.mh.restaurantchainpos.pos.ui.kitchen.KitchenSortMode
import com.mh.restaurantchainpos.pos.ui.kitchen.KitchenViewMode
import com.mh.restaurantchainpos.pos.ui.kitchen.KitchenViewTab
import com.mh.restaurantchainpos.pos.ui.kitchen.OrderDetailModal
import com.mh.restaurantchainpos.pos.ui.kitchen.TableFilterPanel
import com.mh.restaurantchainpos.pos.ui.kitchen.TableFilterSidebar
import com.mh.restaurantchainpos.pos.ui.kitchen.rememberKitchenState
import com.mh.restaurantchainpos.pos.ui.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
fun KitchenScreen(colors: PosColors, role: ActiveRole, onReceivedCount: (Int) -> Unit) {
    val state = rememberKitchenState()
    val visible = state.visibleOrders()
    val received = state.receivedDistinctItemCount()
    val inProgress = state.inProgressDistinctItemCount()
    val completed = state.completedDistinctItemCount()
    val receivedOrders by remember {
        derivedStateOf { state.orders.count { it.status == com.mh.restaurantchainpos.pos.data.KitchenStatus.Received } }
    }
    val inProgressOrders by remember {
        derivedStateOf {
            state.orders.count {
                it.status == com.mh.restaurantchainpos.pos.data.KitchenStatus.InProgress &&
                    it.items.any { i -> !i.previouslyCompleted }
            }
        }
    }
    val completedOrders by remember {
        derivedStateOf {
            state.orders.count {
                it.status == com.mh.restaurantchainpos.pos.data.KitchenStatus.Completed ||
                    (it.status == com.mh.restaurantchainpos.pos.data.KitchenStatus.InProgress &&
                        it.items.any { i -> i.previouslyCompleted })
            }
        }
    }

    LaunchedEffect(received) { onReceivedCount(received) }
    val isMobile = rememberIsMobile()

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
                    inProgressItems = inProgress,
                    completedItems = completed,
                    receivedOrders = receivedOrders,
                    inProgressOrders = inProgressOrders,
                    completedOrders = completedOrders,
                )
                KitchenContent(
                    colors = colors,
                    state = state,
                    isMobile = isMobile,
                    visible = visible,
                )
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
                OrderDetailModal(
                    colors = colors,
                    order = detail,
                    allOrders = state.orders,
                    viewTab = state.activeTab,
                    onClose = { state.detailOrderId = null },
                    onAccept = state::acceptOrder,
                    onComplete = state::completeOrder,
                    onRecall = state::recallOrder,
                )
            }
        }
    }
}

@Composable
private fun KitchenContent(
    colors: PosColors,
    state: com.mh.restaurantchainpos.pos.ui.kitchen.KitchenState,
    isMobile: Boolean,
    visible: List<com.mh.restaurantchainpos.pos.data.KitchenOrder>,
) {
    if (visible.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚠", color = colors.textMuted, fontSize = 36.sp)
                Spacer(Modifier.height(8.dp))
                Text("No tickets in this lane.", color = colors.textMuted, fontSize = 13.sp)
            }
        }
        return
    }
    if (state.viewMode == KitchenViewMode.ByItem) {
        Box(Modifier.fillMaxSize().padding(16.dp)) {
            ByItemView(colors = colors, sorted = visible)
        }
        return
    }
    if (isMobile) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(visible, key = { it.id }) { order ->
                OrderCard(colors, state, order)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(280.dp),
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(visible, key = { it.id }) { order ->
                OrderCard(colors, state, order)
            }
        }
    }
}

@Composable
private fun OrderCard(
    colors: PosColors,
    state: com.mh.restaurantchainpos.pos.ui.kitchen.KitchenState,
    order: com.mh.restaurantchainpos.pos.data.KitchenOrder,
) {
    KitchenCard(
        colors = colors,
        order = order,
        viewTab = state.activeTab,
        onAccept = { state.acceptOrder(order.id) },
        onComplete = { state.completeOrder(order.id) },
        onRecall = { state.recallOrder(order.id) },
        onToggleItem = { itemId -> state.toggleItemDone(order.id, itemId) },
        onSetItemQty = { itemId, count -> state.setItemSelectedQty(order.id, itemId, count) },
        onShowDetail = { state.detailOrderId = order.id },
    )
}

@Composable
private fun KitchenHeader(
    colors: PosColors,
    role: ActiveRole,
    state: com.mh.restaurantchainpos.pos.ui.kitchen.KitchenState,
    isMobile: Boolean,
    receivedItems: Int,
    inProgressItems: Int,
    completedItems: Int,
    receivedOrders: Int,
    inProgressOrders: Int,
    completedOrders: Int,
) {
    Column(Modifier.fillMaxWidth().background(colors.surface).border(1.dp, colors.border)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isMobile) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Blue500)
                        .clickable { state.sidebarOpen = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("☰", color = Color.White, fontSize = 12.sp)
                        Text("Tables", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("${state.selectedTables.size}", color = Color(0xCCFFFFFF), fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Row(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceRaised)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                KitchenViewMode.entries.forEach { mode ->
                    val active = state.viewMode == mode
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (active) Blue500 else Color.Transparent)
                            .clickable { state.viewMode = mode }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            mode.label,
                            color = if (active) Color.White else colors.textMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
            Spacer(Modifier.size(8.dp))
            Box {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceRaised)
                        .clickable { state.sortOpen = !state.sortOpen }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("↕", color = colors.textMuted, fontSize = 11.sp)
                        Text(state.sortMode.label, color = colors.textMuted, fontSize = 11.sp)
                        Text("▾", color = colors.textMuted, fontSize = 9.sp)
                    }
                }
                DropdownMenu(expanded = state.sortOpen, onDismissRequest = { state.sortOpen = false }) {
                    KitchenSortMode.entries.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.label) },
                            onClick = {
                                state.sortMode = mode
                                state.sortOpen = false
                            },
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TabPill(
                colors = colors,
                label = "Received",
                tabActive = state.activeTab == KitchenViewTab.Received,
                items = receivedItems,
                orders = receivedOrders,
                role = role,
                onClick = { state.activeTab = KitchenViewTab.Received },
            )
            TabPill(
                colors = colors,
                label = "In Progress",
                tabActive = state.activeTab == KitchenViewTab.InProgress,
                items = inProgressItems,
                orders = inProgressOrders,
                role = role,
                onClick = { state.activeTab = KitchenViewTab.InProgress },
            )
            TabPill(
                colors = colors,
                label = "Completed",
                tabActive = state.activeTab == KitchenViewTab.Completed,
                items = completedItems,
                orders = completedOrders,
                role = role,
                onClick = { state.activeTab = KitchenViewTab.Completed },
            )
        }
    }
}

@Composable
private fun TabPill(
    colors: PosColors,
    label: String,
    tabActive: Boolean,
    items: Int,
    orders: Int,
    role: ActiveRole,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, color = if (tabActive) Blue500 else colors.textMuted, fontWeight = FontWeight.Medium, fontSize = 13.sp)
        Spacer(Modifier.size(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (role != ActiveRole.Waiter) {
                Text("⊞ $orders", color = colors.textMuted, fontSize = 10.sp)
            }
            Text("☷ $items", color = colors.textMuted, fontSize = 10.sp)
        }
        Box(Modifier.height(2.dp).width(36.dp).background(if (tabActive) Blue500 else Color.Transparent))
    }
}
