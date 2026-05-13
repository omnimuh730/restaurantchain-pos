package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.KitchenOrder
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownChip
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownChipVariant
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownMenuRow
import com.mh.restaurantchainpos.pos.ui.i18n.stringLabel
import com.mh.restaurantchainpos.pos.ui.i18n.stringTitle
import com.mh.restaurantchainpos.pos.ui.i18n.stringTrigger
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun KitchenContent(
    colors: PosColors,
    state: KitchenState,
    isMobile: Boolean,
    visible: List<KitchenOrder>,
    viewTab: KitchenViewTab,
) {
    if (visible.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚠", color = colors.textMuted, fontSize = 36.sp)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.kitchen_empty_lane), color = colors.textMuted, fontSize = 13.sp)
            }
        }
        return
    }
    if (isMobile) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(visible, key = { it.id }) { order ->
                OrderCard(colors, state, order, viewTab)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(280.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(visible, key = { it.id }) { order ->
                OrderCard(colors, state, order, viewTab)
            }
        }
    }
}

@Composable
private fun OrderCard(
    colors: PosColors,
    state: KitchenState,
    order: KitchenOrder,
    viewTab: KitchenViewTab,
) {
    KitchenCard(
        colors = colors,
        order = order,
        viewTab = viewTab,
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
    selectedTabPage: Int,
    onSelectTabPage: (Int) -> Unit,
) {
    Column(Modifier.fillMaxWidth().background(colors.surface)) {
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
                        Text(stringResource(R.string.common_tables), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("${state.selectedTables.size}", color = Color(0xCCFFFFFF), fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            // Sort selector — Outlined variant of the unified dropdown chip
            // so it reads as a passive control on the kitchen header surface.
            PosDropdownChip(
                text = state.sortMode.stringTrigger(),
                expanded = state.sortOpen,
                colors = colors,
                onExpandedChange = { state.sortOpen = it },
                leadingIcon = Icons.Outlined.SwapVert,
                leadingIconTint = colors.textMuted,
                labelColor = colors.textMuted,
                chevronTint = colors.textMuted,
                variant = PosDropdownChipVariant.Outlined,
                menuOffset = DpOffset(x = (-58).dp, y = 4.dp),
                menuWidth = 168.dp,
            ) {
                val modes = KitchenSortMode.entries
                modes.forEachIndexed { index, mode ->
                    PosDropdownMenuRow(
                        index = index,
                        totalCount = modes.size,
                        text = mode.stringLabel(),
                        selected = state.sortMode == mode,
                        colors = colors,
                        onClick = {
                            state.sortMode = mode
                            state.sortOpen = false
                        },
                    )
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
                label = KitchenViewTab.Received.stringTitle(),
                tabActive = selectedTabPage == 0,
                items = receivedItems,
                orders = receivedOrders,
                role = role,
                onClick = { onSelectTabPage(0) },
            )
            TabPill(
                colors = colors,
                label = KitchenViewTab.InProgress.stringTitle(),
                tabActive = selectedTabPage == 1,
                items = inProgressItems,
                orders = inProgressOrders,
                role = role,
                onClick = { onSelectTabPage(1) },
            )
            TabPill(
                colors = colors,
                label = KitchenViewTab.Completed.stringTitle(),
                tabActive = selectedTabPage == 2,
                items = completedItems,
                orders = completedOrders,
                role = role,
                onClick = { onSelectTabPage(2) },
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
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (role != ActiveRole.Waiter) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(12.dp),
                )
                Text(orders.toString(), color = colors.textMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Icon(
                imageVector = Icons.Outlined.RestaurantMenu,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(12.dp),
            )
            Text(items.toString(), color = colors.textMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Box(Modifier.height(2.dp).width(36.dp).background(if (tabActive) Blue500 else Color.Transparent))
    }
}
