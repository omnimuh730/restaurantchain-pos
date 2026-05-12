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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import com.mh.restaurantchainpos.pos.ui.theme.Orange500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

/**
 * One ticket card for the kitchen wall. Mirrors the React `KitchenCard` —
 * urgency badge, ticket actions, line-item rows with circular checkboxes that
 * open a quantity modal when tapped on multi-quantity items.
 */
@Composable
fun KitchenCard(
    colors: PosColors,
    order: KitchenOrder,
    viewTab: KitchenViewTab,
    onAccept: () -> Unit,
    onComplete: () -> Unit,
    onRecall: () -> Unit,
    onToggleItem: (String) -> Unit,
    onSetItemQty: (String, Int) -> Unit,
    onShowDetail: () -> Unit,
) {
    val isReceived = viewTab == KitchenViewTab.Received
    val isInProgress = viewTab == KitchenViewTab.InProgress
    val isCompleted = viewTab == KitchenViewTab.Completed
    val urgency = urgencyOf(order.minutesAgo)
    val displayItems = when {
        isCompleted && order.status == KitchenStatus.Completed -> order.items
        isCompleted -> order.items.filter { it.previouslyCompleted }
        else -> order.items.filter { !it.previouslyCompleted }
    }
    val checkedItems = order.items.filter { it.done && !it.previouslyCompleted }
    val recallableItems = if (order.status == KitchenStatus.Completed)
        order.items.filter { it.done }
    else order.items.filter { it.done && it.previouslyCompleted }

    var confirm by remember { mutableStateOf<String?>(null) }
    var countModalItemId by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.kitchen_ordered_minutes_ago, order.minutesAgo), color = colors.textMuted, fontSize = 11.sp, modifier = Modifier.weight(1f))
            if (!isCompleted) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    urgency.isUrgent -> Red500
                                    urgency.isWarning -> Orange500
                                    else -> Blue500
                                },
                            ),
                    )
                    Text(
                        urgency.label,
                        color = when {
                            urgency.isUrgent -> Red500
                            urgency.isWarning -> Orange500
                            else -> Blue500
                        },
                        fontSize = 10.sp,
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(order.table, color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            when {
                isReceived -> {
                    OutlineButton("Accept", Blue500, Modifier.weight(1f)) { confirm = "accept" }
                    PrimaryButton("Complete", Modifier.weight(1f)) { confirm = "received-complete" }
                }
                isInProgress -> {
                    val label = if (checkedItems.isEmpty()) "Complete" else "Complete (${checkedItems.size})"
                    PrimaryButton(label, Modifier.weight(1f), enabled = checkedItems.isNotEmpty()) { confirm = "complete" }
                }
                isCompleted -> {
                    val label = if (recallableItems.isEmpty()) "Recall" else "Recall (${recallableItems.size})"
                    PrimaryButton(label, Modifier.weight(1f), enabled = recallableItems.isNotEmpty()) { confirm = "recall" }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            displayItems.forEach { item ->
                ItemRow(
                    colors = colors,
                    item = item,
                    viewTab = viewTab,
                    onClick = {
                        when {
                            isReceived -> onToggleItem(item.id)
                            item.qty > 1 && !item.done -> countModalItemId = item.id
                            else -> onToggleItem(item.id)
                        }
                    },
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surfaceRaised)
                .clickable(onClick = onShowDetail)
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.kitchen_order_details), color = colors.textMuted, fontSize = 12.sp)
        }
    }

    when (confirm) {
        "accept" -> ConfirmActionModal(
            colors = colors,
            action = "accept",
            items = if (checkedItems.isNotEmpty()) checkedItems else order.items,
            onConfirm = { onAccept(); confirm = null },
            onCancel = { confirm = null },
        )
        "complete", "received-complete" -> ConfirmActionModal(
            colors = colors,
            action = "complete",
            items = if (checkedItems.isNotEmpty()) checkedItems else order.items,
            onConfirm = { onComplete(); confirm = null },
            onCancel = { confirm = null },
        )
        "recall" -> ConfirmActionModal(
            colors = colors,
            action = "recall",
            items = recallableItems,
            onConfirm = { onRecall(); confirm = null },
            onCancel = { confirm = null },
        )
    }
    countModalItemId?.let { id ->
        val target = order.items.firstOrNull { it.id == id } ?: return@let
        ItemCountModal(
            colors = colors,
            item = target,
            action = if (isCompleted) "recall" else "complete",
            onConfirm = { count ->
                onSetItemQty(target.id, count)
                countModalItemId = null
            },
            onCancel = { countModalItemId = null },
        )
    }
}

@Composable
private fun ItemRow(
    colors: PosColors,
    item: KitchenItem,
    viewTab: KitchenViewTab,
    onClick: () -> Unit,
) {
    val isReceived = viewTab == KitchenViewTab.Received
    val isCompleted = viewTab == KitchenViewTab.Completed
    val checkedColor = when {
        isCompleted -> Amber500
        isReceived -> Green500
        else -> Green500
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                ordersMenuLineTitle(item.titleKey),
                color = if (item.done) colors.textMuted else colors.text,
                fontSize = 13.sp,
                textDecoration = if (item.done) TextDecoration.LineThrough else null,
            )
            if (item.modifier.isNotBlank()) {
                Text("∟ ${item.modifier}", color = colors.textMuted, fontSize = 10.sp)
            }
        }
        Text(
            text = if (item.done && (item.selectedQty ?: item.qty) < item.qty)
                "${item.selectedQty ?: item.qty}/${item.qty}" else item.qty.toString(),
            color = if (item.done) colors.textMuted else colors.text,
            fontSize = 13.sp,
        )
        Box(
            Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (item.done) checkedColor else Color.Transparent)
                .border(2.dp, if (item.done) checkedColor else colors.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (item.done) Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PrimaryButton(label: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) Blue500 else Blue500.copy(alpha = 0.4f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OutlineButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, color, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
