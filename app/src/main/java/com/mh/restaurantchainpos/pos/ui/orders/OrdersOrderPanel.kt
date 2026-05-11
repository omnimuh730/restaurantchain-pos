package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

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
    checkNumber: String,
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
) {
    val activeTables = OrderTables.filter { it.floor == selectedFloorId }
    val orderDisabled = pendingCount == 0
    val allOrdered = currentOrder.isNotEmpty() && pendingCount == 0
    val canViewHistory = role == ActiveRole.Admin || role == ActiveRole.Cashier

    Column(
        modifier
            .background(colors.surface)
            .border(1.dp, colors.border),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DropdownChip(
                text = floorLabel(selectedFloorId),
                expanded = floorMenuOpen,
                colors = colors,
                onExpandedChange = onFloorMenu,
            ) {
                OrderFloors.forEach { floor ->
                    DropdownMenuItem(
                        text = { Text(floor.label, color = colors.text, fontSize = 13.sp) },
                        onClick = { onSelectFloor(floor.id) },
                    )
                }
            }
            DropdownChip(
                text = selectedTable?.label ?: "Select Table",
                expanded = tableMenuOpen,
                colors = colors,
                onExpandedChange = onTableMenu,
            ) {
                activeTables.forEach { table ->
                    val hasOrder = allOrders[table.id].orEmpty().any { !it.deleted }
                    DropdownMenuItem(
                        text = {
                            Row(Modifier.width(168.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(table.label, color = colors.text, fontSize = 13.sp)
                                Spacer(Modifier.weight(1f))
                                if (hasOrder) {
                                    Box(Modifier.size(6.dp).clip(CircleShape).background(Blue500))
                                    Spacer(Modifier.width(6.dp))
                                }
                                Text("${table.seats} seats", color = colors.textMuted, fontSize = 11.sp)
                            }
                        },
                        onClick = { onSelectTable(table.id) },
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (canViewHistory) {
                CompactButton("History", colors, onClick = onHistory)
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(46.dp)
                .border(1.dp, colors.border)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionButton(
                text = if (allOrdered) "Ordered" else "Order",
                active = false,
                enabled = !orderDisabled,
                badge = pendingCount.takeIf { it > 0 },
                colors = colors,
                modifier = Modifier.weight(1f),
                onClick = onOrder,
            )
            ActionButton(
                text = "Pay ${paySummary(totalKrw, totalUsd)}",
                active = true,
                enabled = canPay && currentOrder.isNotEmpty(),
                colors = colors,
                modifier = Modifier.weight(1f),
                onClick = onPay,
            )
        }

        OrderTableHeader(colors)
        if (currentOrder.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No items yet", color = colors.textMuted, fontSize = 13.sp)
                    Text("Select items to add to order", color = colors.textMuted.copy(alpha = 0.75f), fontSize = 11.sp)
                }
            }
        } else {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items(currentOrder, key = { it.id }) { line ->
                    OrderLineRow(
                        colors = colors,
                        line = line,
                        onMinus = { onMinus(line) },
                        onPlus = { onPlus(line) },
                        onRemove = { onRemove(line) },
                    )
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .border(1.dp, colors.border)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TotalRow("Domestic (won):", formatDomesticWon(totalKrw), colors)
            TotalRow("Foreign ($):", formatForeignUsd(totalUsd), colors)
        }
        Text(checkNumber, color = Color.Transparent, fontSize = 1.sp)
    }
}
