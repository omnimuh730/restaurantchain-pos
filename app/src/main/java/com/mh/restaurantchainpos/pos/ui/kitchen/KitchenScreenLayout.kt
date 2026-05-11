package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.KitchenOrder
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun KitchenContent(
    colors: PosColors,
    state: KitchenState,
    isMobile: Boolean,
    visible: List<KitchenOrder>,
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
    state: KitchenState,
    order: KitchenOrder,
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
internal fun KitchenHeader(
    colors: PosColors,
    role: ActiveRole,
    state: KitchenState,
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
            Box {
                val pillShape = RoundedCornerShape(999.dp)
                Row(
                    Modifier
                        .clip(pillShape)
                        .border(1.dp, colors.border, pillShape)
                        .background(colors.surface)
                        .clickable { state.sortOpen = !state.sortOpen }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwapVert,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colors.textMuted,
                    )
                    Text(
                        state.sortMode.triggerLabel,
                        color = colors.textMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colors.textMuted,
                    )
                }
                DropdownMenu(
                    expanded = state.sortOpen,
                    onDismissRequest = { state.sortOpen = false },
                    modifier = Modifier.width(168.dp),
                    offset = DpOffset(x = (-58).dp, y = 2.dp),
                    shape = RoundedCornerShape(10.dp),
                    containerColor = colors.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 6.dp,
                ) {
                    KitchenSortMode.entries.forEach { mode ->
                        val selected = state.sortMode == mode
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selected) Blue500.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable {
                                    state.sortMode = mode
                                    state.sortOpen = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = mode.label,
                                color = if (selected) Blue500 else colors.text,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                            )
                        }
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
