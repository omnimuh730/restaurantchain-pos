package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownChip
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownMenuRow
import com.mh.restaurantchainpos.pos.ui.i18n.orderCatalogString
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlinx.coroutines.delay

@Composable
internal fun OrderPanel(
    colors: PosColors,
    role: ActiveRole,
    selectedFloorId: String,
    selectedTableId: String,
    selectedTable: OrderTable?,
    floorMenuOpen: Boolean,
    tableMenuOpen: Boolean,
    onFloorMenu: (Boolean) -> Unit,
    onTableMenu: (Boolean) -> Unit,
    onSelectFloor: (String) -> Unit,
    onSelectTable: (String) -> Unit,
    currentOrder: List<OrderLine>,
    allOrders: Map<String, List<OrderLine>>,
    totalUsd: Double,
    totalKrw: Double,
    pendingCount: Int,
    canPay: Boolean,
    onMinus: (OrderLine) -> Unit,
    onPlus: (OrderLine) -> Unit,
    onRemove: (OrderLine) -> Unit,
    onOrder: () -> Unit,
    onPay: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier,
    /** Bumped after each menu add so the list can scroll/highlight the affected line. */
    orderScrollNonce: Int = 0,
    highlightLineId: String? = null,
) {
    val ctx = LocalContext.current
    val activeTables = OrderTables.filter { it.floor == selectedFloorId }
    val orderDisabled = pendingCount == 0
    val allOrdered = currentOrder.isNotEmpty() && pendingCount == 0
    val canViewHistory = role == ActiveRole.Admin || role == ActiveRole.Cashier
    val sentLines = currentOrder.filter { it.ordered }
    val newLines = currentOrder.filter { !it.ordered }

    Column(
        modifier
            .background(colors.surface),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            PosDropdownChip(
                text = ctx.floorOrderLabel(selectedFloorId),
                expanded = floorMenuOpen,
                colors = colors,
                onExpandedChange = onFloorMenu,
            ) {
                OrderFloors.forEachIndexed { index, floor ->
                    PosDropdownMenuRow(
                        index = index,
                        totalCount = OrderFloors.size,
                        text = ctx.orderCatalogString("orders_floor", floor.id, floor.id),
                        selected = floor.id == selectedFloorId,
                        colors = colors,
                        onClick = { onSelectFloor(floor.id) },
                    )
                }
            }
            PosDropdownChip(
                text = selectedTable?.let { ctx.tableOrderLabel(it.id) } ?: stringResource(R.string.orders_select_table),
                expanded = tableMenuOpen,
                colors = colors,
                onExpandedChange = onTableMenu,
            ) {
                activeTables.forEachIndexed { index, table ->
                    val hasOrder = allOrders[table.id].orEmpty().any { !it.deleted }
                    OrdersDropdownTableRow(
                        index = index,
                        totalCount = activeTables.size,
                        label = ctx.tableOrderLabel(table.id),
                        seats = table.seats,
                        hasOrder = hasOrder,
                        selected = table.id == selectedTableId,
                        colors = colors,
                        onClick = { onSelectTable(table.id) },
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (canViewHistory) {
                HistoryButton(colors, onClick = onHistory)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))

        Row(
            Modifier
                .fillMaxWidth()
                .height(46.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionButton(
                text = if (allOrdered) stringResource(R.string.orders_action_ordered) else stringResource(R.string.orders_action_order),
                active = false,
                enabled = !orderDisabled,
                badge = pendingCount.takeIf { it > 0 },
                colors = colors,
                modifier = Modifier.weight(1f),
                onClick = onOrder,
            )
            ActionButton(
                text = stringResource(R.string.orders_pay_with_totals, paySummary(totalKrw, totalUsd)),
                active = true,
                enabled = canPay && currentOrder.isNotEmpty(),
                colors = colors,
                modifier = Modifier.weight(1f),
                onClick = onPay,
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))

        OrderTableHeader(colors)
        if (currentOrder.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.orders_empty_title), color = colors.textMuted, fontSize = 13.sp)
                    Text(stringResource(R.string.orders_empty_subtitle), color = colors.textMuted.copy(alpha = 0.75f), fontSize = 11.sp)
                }
            }
        } else {
            val listState = rememberLazyListState()
            LaunchedEffect(orderScrollNonce) {
                if (orderScrollNonce == 0) return@LaunchedEffect
                val targetId = highlightLineId ?: return@LaunchedEffect
                val idxInNew = newLines.indexOfFirst { it.id == targetId }
                if (idxInNew < 0 || newLines.isEmpty()) return@LaunchedEffect
                delay(48)
                val headerSlots = 1
                val itemIndex = sentLines.size + headerSlots + idxInNew
                val lastIndex = sentLines.size + headerSlots + newLines.size - 1
                val safeIndex = itemIndex.coerceIn(0, lastIndex.coerceAtLeast(0))
                runCatching { listState.animateScrollToItem(safeIndex) }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(sentLines, key = { it.id }) { line ->
                    OrderLineRow(
                        colors = colors,
                        line = line,
                        onMinus = { onMinus(line) },
                        onPlus = { onPlus(line) },
                        onRemove = { onRemove(line) },
                    )
                }
                if (newLines.isNotEmpty()) {
                    item(key = "__new_items_header__") {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(colors.newItemsBg),
                        ) {
                            NewItemsSectionLabel(colors = colors)
                        }
                    }
                    items(newLines, key = { it.id }) { line ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(colors.newItemsBg),
                        ) {
                            OrderLineRow(
                                colors = colors,
                                line = line,
                                onMinus = { onMinus(line) },
                                onPlus = { onPlus(line) },
                                onRemove = { onRemove(line) },
                                highlighted = line.id == highlightLineId,
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun NewItemsSectionLabel(colors: PosColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.orders_new_items),
            color = colors.accent,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(colors.border),
        )
    }
}
