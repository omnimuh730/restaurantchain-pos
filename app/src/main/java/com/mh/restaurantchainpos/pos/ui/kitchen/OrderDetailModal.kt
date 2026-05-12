package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.KitchenItem
import com.mh.restaurantchainpos.pos.data.KitchenOrder
import com.mh.restaurantchainpos.pos.data.KitchenStatus
import com.mh.restaurantchainpos.pos.ui.i18n.ordersMenuLineTitle
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Green500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

private data class DetailLine(val orderId: String, val item: KitchenItem)

@Composable
fun OrderDetailModal(
    colors: PosColors,
    order: KitchenOrder,
    allOrders: List<KitchenOrder>,
    viewTab: KitchenViewTab,
    onClose: () -> Unit,
    onAccept: (String) -> Unit,
    onComplete: (String) -> Unit,
    onRecall: (String) -> Unit,
    onToggleItem: (orderId: String, itemId: String) -> Unit,
    onSetItemQty: (orderId: String, itemId: String, count: Int) -> Unit,
) {
    val tableOrders = allOrders.filter { it.table == order.table }
    val lines = detailLinesForTable(tableOrders, viewTab)
    val receivedCount = tableOrders.filter { it.status == KitchenStatus.Received }.flatMap { it.items }.distinctBy { it.titleKey + it.modifier }.size
    val inProgressCount = tableOrders.filter { it.status == KitchenStatus.InProgress }
        .flatMap { it.items.filter { i -> !i.previouslyCompleted } }
        .distinctBy { it.titleKey + it.modifier }
        .size
    val completedCount = (tableOrders.filter { it.status == KitchenStatus.Completed }.flatMap { it.items } +
        tableOrders.filter { it.status == KitchenStatus.InProgress }.flatMap { it.items.filter { i -> i.previouslyCompleted } })
        .distinctBy { it.titleKey + it.modifier }
        .size

    val checkedToCompleteCount = lines.count { it.item.done && !it.item.previouslyCompleted }
    val recallSelectedCount = lines.count { line ->
        val o = tableOrders.firstOrNull { it.id == line.orderId } ?: return@count false
        when (o.status) {
            KitchenStatus.Completed -> line.item.done
            else -> line.item.done && line.item.previouslyCompleted
        }
    }

    var countModalTarget by remember { mutableStateOf<Pair<String, String>?>(null) }

    ModalScaffold(onDismiss = onClose) {
        Column(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .widthIn(max = 360.dp)
                .heightIn(max = 600.dp),
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        Text(order.table, color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(stringResource(R.string.kitchen_ordered_minutes_ago, order.minutesAgo), color = colors.textMuted, fontSize = 11.sp)
                    }
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✕", color = colors.textMuted, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (receivedCount > 0) StatusBadge(stringResource(R.string.kitchen_badge_received, receivedCount), Amber500)
                    if (inProgressCount > 0) StatusBadge(stringResource(R.string.kitchen_badge_in_progress, inProgressCount), Blue500)
                    if (completedCount > 0) StatusBadge(stringResource(R.string.kitchen_badge_completed, completedCount), Green500)
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            LazyColumn(
                Modifier.weight(1f).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(lines, key = { "${it.orderId}_${it.item.id}" }) { line ->
                    DetailItemRow(
                        colors = colors,
                        viewTab = viewTab,
                        item = line.item,
                        onClick = {
                            if (line.item.qty > 1 && !line.item.done) {
                                countModalTarget = line.orderId to line.item.id
                            } else {
                                onToggleItem(line.orderId, line.item.id)
                            }
                        },
                    )
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (viewTab) {
                    KitchenViewTab.Received -> {
                        OutlineActionButton("Accept", Blue500, Modifier.weight(1f)) {
                            tableOrders.filter { it.status == KitchenStatus.Received }.forEach { onAccept(it.id) }
                            onClose()
                        }
                        SolidActionButton("Complete", Modifier.weight(1f), enabled = true) {
                            tableOrders.filter { it.status == KitchenStatus.Received }.forEach { onComplete(it.id) }
                            onClose()
                        }
                    }
                    KitchenViewTab.InProgress -> {
                        val label = if (checkedToCompleteCount == 0) "Complete" else "Complete ($checkedToCompleteCount)"
                        SolidActionButton(label, Modifier.weight(1f), enabled = checkedToCompleteCount > 0) {
                            tableOrders.filter { it.status == KitchenStatus.InProgress }.forEach { onComplete(it.id) }
                            onClose()
                        }
                    }
                    KitchenViewTab.Completed -> {
                        val label = if (recallSelectedCount == 0) "Recall" else "Recall ($recallSelectedCount)"
                        SolidActionButton(label, Modifier.weight(1f), enabled = recallSelectedCount > 0) {
                            tableOrders.forEach { onRecall(it.id) }
                            onClose()
                        }
                    }
                }
            }
        }
    }

    countModalTarget?.let { (targetOrderId, itemId) ->
        val parentOrder = tableOrders.firstOrNull { it.id == targetOrderId } ?: return@let
        val target = parentOrder.items.firstOrNull { it.id == itemId } ?: return@let
        ItemCountModal(
            colors = colors,
            item = target,
            action = if (viewTab == KitchenViewTab.Completed) "recall" else "complete",
            onConfirm = { count ->
                onSetItemQty(targetOrderId, itemId, count)
                countModalTarget = null
            },
            onCancel = { countModalTarget = null },
        )
    }
}

private fun detailLinesForTable(tableOrders: List<KitchenOrder>, viewTab: KitchenViewTab): List<DetailLine> =
    tableOrders.flatMap { o ->
        val itemsForDisplay = when {
            viewTab == KitchenViewTab.Completed && o.status == KitchenStatus.Completed -> o.items
            viewTab == KitchenViewTab.Completed -> o.items.filter { it.previouslyCompleted }
            else -> o.items.filter { !it.previouslyCompleted }
        }
        itemsForDisplay.map { DetailLine(o.id, it) }
    }

@Composable
private fun StatusBadge(label: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DetailItemRow(
    colors: PosColors,
    viewTab: KitchenViewTab,
    item: KitchenItem,
    onClick: () -> Unit,
) {
    val isCompletedTab = viewTab == KitchenViewTab.Completed
    val checked = item.done
    val checkedFill = when {
        isCompletedTab -> Amber500
        else -> Green500
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (checked) checkedFill else Color.Transparent)
                .border(2.dp, if (checked) checkedFill else Blue500, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Text(
                ordersMenuLineTitle(item.titleKey),
                color = if (checked) colors.textMuted else colors.text,
                fontSize = 13.sp,
                textDecoration = if (checked) TextDecoration.LineThrough else null,
            )
            if (item.modifier.isNotBlank()) {
                Text("∟ ${item.modifier}", color = colors.textMuted, fontSize = 10.sp)
            }
        }
        Text(
            text = if (checked && (item.selectedQty ?: item.qty) < item.qty) {
                "${item.selectedQty ?: item.qty}/${item.qty}"
            } else {
                item.qty.toString()
            },
            color = if (checked) colors.textMuted else colors.text,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun SolidActionButton(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) Blue500 else Blue500.copy(alpha = 0.4f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OutlineActionButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, color, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
