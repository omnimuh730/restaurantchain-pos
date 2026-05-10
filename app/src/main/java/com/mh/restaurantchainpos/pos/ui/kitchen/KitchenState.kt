package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mh.restaurantchainpos.pos.data.KitchenItem
import com.mh.restaurantchainpos.pos.data.KitchenOrder
import com.mh.restaurantchainpos.pos.data.KitchenStatus
import com.mh.restaurantchainpos.pos.data.PosMockData

enum class KitchenViewTab(val label: String) {
    Received("Received"), InProgress("In Progress"), Completed("Completed");

    fun toStatus(): KitchenStatus = when (this) {
        Received -> KitchenStatus.Received
        InProgress -> KitchenStatus.InProgress
        Completed -> KitchenStatus.Completed
    }
}

enum class KitchenSortMode(val label: String) { Oldest("Oldest first"), Newest("Newest first") }

enum class KitchenViewMode(val label: String) { ByTable("By table"), ByItem("By item") }

class KitchenState(initial: List<KitchenOrder>) {
    val orders: SnapshotStateList<KitchenOrder> = mutableStateListOf<KitchenOrder>().apply { addAll(initial) }
    var activeTab by mutableStateOf(KitchenViewTab.InProgress)
    var sortMode by mutableStateOf(KitchenSortMode.Oldest)
    var sortOpen by mutableStateOf(false)
    var viewMode by mutableStateOf(KitchenViewMode.ByTable)
    var detailOrderId by mutableStateOf<String?>(null)
    var sidebarOpen by mutableStateOf(false)
    val selectedTables: SnapshotStateList<String> = mutableStateListOf<String>().apply {
        addAll(PosMockData.kitchenFloors.flatMap { it.tables })
    }

    fun toggleItemDone(orderId: String, itemId: String) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx < 0) return
        val order = orders[idx]
        val nextItems = order.items.map { item ->
            if (item.id == itemId) {
                val nowDone = !item.done
                item.copy(done = nowDone, selectedQty = if (nowDone) item.qty else null)
            } else item
        }
        orders[idx] = order.copy(items = nextItems)
    }

    fun setItemSelectedQty(orderId: String, itemId: String, count: Int) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx < 0) return
        val order = orders[idx]
        val nextItems = order.items.map { item ->
            if (item.id == itemId) {
                if (count <= 0) item.copy(done = false, selectedQty = null)
                else item.copy(done = true, selectedQty = minOf(count, item.qty))
            } else item
        }
        orders[idx] = order.copy(items = nextItems)
    }

    fun acceptOrder(orderId: String) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx < 0 || orders[idx].status != KitchenStatus.Received) return
        val order = orders[idx]
        val anyChecked = order.items.any { it.done }
        if (!anyChecked) {
            orders[idx] = order.copy(
                status = KitchenStatus.InProgress,
                items = order.items.map { it.copy(done = false, selectedQty = null) },
            )
            return
        }
        val remaining = mutableListOf<KitchenItem>()
        val accepted = mutableListOf<KitchenItem>()
        order.items.forEach { item ->
            if (!item.done) {
                remaining.add(item)
            } else {
                val selected = (item.selectedQty ?: item.qty).coerceAtMost(item.qty)
                val left = item.qty - selected
                if (left > 0) remaining.add(item.copy(qty = left, done = false, selectedQty = null))
                accepted.add(item.copy(id = "${item.id}-a${ts()}", qty = selected, done = false, selectedQty = null))
            }
        }
        if (remaining.isNotEmpty()) orders[idx] = order.copy(items = remaining) else orders.removeAt(idx)
        orders.add(order.copy(id = "${order.id}-a${ts()}", status = KitchenStatus.InProgress, items = accepted))
    }

    fun completeOrder(orderId: String) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx < 0) return
        val order = orders[idx]
        val checked = order.items.filter { it.done && !it.previouslyCompleted }
        if (checked.isEmpty()) {
            // Promote everything to completed if nothing is checked.
            orders[idx] = order.copy(
                status = KitchenStatus.Completed,
                completedMinutesAgo = 0,
                items = order.items.map { it.copy(previouslyCompleted = true, done = false, selectedQty = null) },
            )
            return
        }
        val nextItems = order.items.map { item ->
            if (item.done && !item.previouslyCompleted) {
                val sel = (item.selectedQty ?: item.qty).coerceAtMost(item.qty)
                val rest = item.qty - sel
                item.copy(qty = sel, done = false, selectedQty = null, previouslyCompleted = true)
                    .let { if (rest > 0) it else it } // placeholder for the no-leftover branch
            } else item
        }
        // Split: when there are leftovers we need to add a new "remaining" item beside it.
        val expanded = mutableListOf<KitchenItem>()
        order.items.forEach { item ->
            if (item.done && !item.previouslyCompleted) {
                val sel = (item.selectedQty ?: item.qty).coerceAtMost(item.qty)
                val rest = item.qty - sel
                if (rest > 0) {
                    expanded.add(item.copy(qty = rest, done = false, selectedQty = null))
                }
                expanded.add(
                    item.copy(
                        id = "${item.id}-c${ts()}",
                        qty = sel,
                        done = false,
                        selectedQty = null,
                        previouslyCompleted = true,
                    ),
                )
            } else expanded.add(item)
        }
        val allCompleted = expanded.all { it.previouslyCompleted }
        orders[idx] = order.copy(
            status = if (allCompleted) KitchenStatus.Completed else order.status,
            completedMinutesAgo = if (allCompleted) 0 else order.completedMinutesAgo,
            items = expanded,
        )
    }

    fun recallOrder(orderId: String) {
        val idx = orders.indexOfFirst { it.id == orderId }
        if (idx < 0) return
        val order = orders[idx]
        val isFully = order.status == KitchenStatus.Completed
        val recallable: (KitchenItem) -> Boolean = { it.done && (isFully || it.previouslyCompleted) }
        val recalled = order.items.filter(recallable)
        if (recalled.isEmpty()) return
        val remainingItems = mutableListOf<KitchenItem>()
        val recalledItems = mutableListOf<KitchenItem>()
        order.items.forEach { item ->
            if (recallable(item)) {
                val sel = (item.selectedQty ?: item.qty).coerceAtMost(item.qty)
                val rest = item.qty - sel
                if (rest > 0) remainingItems.add(item.copy(qty = rest, done = false, selectedQty = null))
                recalledItems.add(
                    item.copy(
                        id = "${item.id}-r${ts()}",
                        qty = sel,
                        done = false,
                        selectedQty = null,
                        previouslyCompleted = false,
                    ),
                )
            } else remainingItems.add(item.copy(done = false, selectedQty = null))
        }
        if (remainingItems.isNotEmpty()) orders[idx] = order.copy(items = remainingItems) else orders.removeAt(idx)
        orders.add(
            order.copy(
                id = "${order.id}-r${ts()}",
                status = KitchenStatus.Received,
                minutesAgo = 0,
                completedMinutesAgo = null,
                items = recalledItems,
            ),
        )
    }

    fun visibleOrders(): List<KitchenOrder> {
        val tab = activeTab
        val byStatus = orders.filter { o ->
            if (!selectedTables.contains(o.table)) return@filter false
            when (tab) {
                KitchenViewTab.Received -> o.status == KitchenStatus.Received
                KitchenViewTab.InProgress -> o.status == KitchenStatus.InProgress
                KitchenViewTab.Completed -> o.status == KitchenStatus.Completed ||
                    o.items.any { it.previouslyCompleted }
            }
        }
        return when (sortMode) {
            KitchenSortMode.Oldest -> byStatus.sortedBy { it.minutesAgo }.reversed()
            KitchenSortMode.Newest -> byStatus.sortedBy { it.minutesAgo }
        }
    }

    fun receivedDistinctItemCount(): Int =
        orders.filter { it.status == KitchenStatus.Received }.flatMap { it.items }.distinctBy { it.name }.size

    fun inProgressDistinctItemCount(): Int =
        orders.filter { it.status == KitchenStatus.InProgress }
            .flatMap { it.items.filter { i -> !i.previouslyCompleted } }
            .distinctBy { it.name }
            .size

    fun completedDistinctItemCount(): Int {
        val a = orders.filter { it.status == KitchenStatus.Completed }.flatMap { it.items }
        val b = orders.filter { it.status == KitchenStatus.InProgress }
            .flatMap { it.items.filter { i -> i.previouslyCompleted } }
        return (a + b).distinctBy { it.name }.size
    }

    private fun ts(): String = System.nanoTime().toString().takeLast(6)
}

@Composable
fun rememberKitchenState(): KitchenState = remember { KitchenState(PosMockData.kitchenOrders) }

data class UrgencyState(val isUrgent: Boolean, val isWarning: Boolean, val label: String)

fun urgencyOf(minutesAgo: Int): UrgencyState = when {
    minutesAgo >= 25 -> UrgencyState(isUrgent = true, isWarning = false, label = "${minutesAgo}m · urgent")
    minutesAgo >= 15 -> UrgencyState(isUrgent = false, isWarning = true, label = "${minutesAgo}m")
    else -> UrgencyState(isUrgent = false, isWarning = false, label = "${minutesAgo}m")
}
