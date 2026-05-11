package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.ui.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import java.util.UUID

@Composable
fun OrdersScreen(colors: PosColors, role: ActiveRole) {
    val isMobile = rememberIsMobile()
    val density = LocalDensity.current
    var selectedFloorId by remember { mutableStateOf("1F") }
    var selectedTableId by remember { mutableStateOf("T12") }
    var floorMenuOpen by remember { mutableStateOf(false) }
    var tableMenuOpen by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf(OrderMenuCategories.first().id) }
    var selectedSubId by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var showPayment by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var splitFraction by remember { mutableFloatStateOf(0.35f) }
    val orders = remember {
        mutableStateMapOf<String, List<OrderLine>>().apply { putAll(initialOrderLines()) }
    }

    fun setCurrentOrder(next: List<OrderLine>) {
        orders[selectedTableId] = next
    }

    val currentOrder = orders[selectedTableId].orEmpty()
    val visibleOrder = currentOrder.filterNot { it.deleted }
    val selectedCategory = OrderMenuCategories.first { it.id == selectedCategoryId }
    val currentItems = selectedCategory.subCategories
        .filter { selectedSubId == null || it.id == selectedSubId }
        .flatMap { sub -> sub.items.map { item -> sub to item } }
        .filter { (_, item) -> query.isBlank() || item.name.contains(query.trim(), ignoreCase = true) }
    val totalUsd = visibleOrder
        .filter { it.currency == CurrencyKind.Foreign }
        .sumOf { it.price * it.qty }
    val totalKrw = visibleOrder
        .filter { it.currency == CurrencyKind.Domestic }
        .sumOf { it.price * it.qty }
    val pendingCount = currentOrder.count { !it.ordered || it.deleted || (it.origQty != null && it.origQty != it.qty) }
    val selectedTable = OrderTables.firstOrNull { it.id == selectedTableId }
    val canPay = role == ActiveRole.Admin || role == ActiveRole.Cashier

    fun updateQty(lineId: String, qty: Int) {
        setCurrentOrder(
            currentOrder.mapNotNull { line ->
                if (line.id != lineId) return@mapNotNull line
                val nextQty = qty.coerceAtLeast(0)
                if (!line.ordered && nextQty == 0) return@mapNotNull null
                if (line.ordered) {
                    line.copy(
                        qty = nextQty,
                        origQty = line.origQty ?: line.qty,
                        deleted = nextQty == 0,
                    )
                } else {
                    line.copy(qty = nextQty)
                }
            },
        )
    }

    fun removeLine(lineId: String) {
        setCurrentOrder(
            currentOrder.mapNotNull { line ->
                if (line.id != lineId) return@mapNotNull line
                if (line.ordered) line.copy(deleted = true, origQty = line.origQty ?: line.qty) else null
            },
        )
    }

    fun addItem(sub: OrderSubCategory, item: OrderMenuItem) {
        val existing = currentOrder.firstOrNull { it.baseId == item.id && !it.ordered && !it.deleted }
        setCurrentOrder(
            if (existing != null) {
                currentOrder.map { if (it.id == existing.id) it.copy(qty = it.qty + 1) else it }
            } else {
                currentOrder + OrderLine(
                    id = "${item.id}-${UUID.randomUUID()}",
                    baseId = item.id,
                    name = item.name,
                    price = item.price,
                    qty = 1,
                    category = sub.label,
                    currency = item.currency,
                )
            },
        )
    }

    fun handleOrder() {
        if (pendingCount == 0) return
        val survivors = currentOrder.filterNot { it.deleted }
        val merged = mutableListOf<OrderLine>()
        survivors.forEach { line ->
            if (line.ordered) {
                merged += line.copy(origQty = null, deleted = false)
            } else {
                val existingIndex = merged.indexOfFirst { it.baseId == line.baseId && it.ordered }
                if (existingIndex >= 0) {
                    val existing = merged[existingIndex]
                    merged[existingIndex] = existing.copy(qty = existing.qty + line.qty)
                } else {
                    merged += line.copy(ordered = true, origQty = null, deleted = false)
                }
            }
        }
        setCurrentOrder(merged)
    }

    Box(Modifier.fillMaxSize().background(colors.surfaceRaised)) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            if (isMobile) {
                val availableHeight = maxHeight
                val totalHeightPx = with(density) { availableHeight.toPx() }.coerceAtLeast(1f)
                Column(Modifier.fillMaxSize()) {
                    OrderPanel(
                        colors = colors,
                        role = role,
                        selectedFloorId = selectedFloorId,
                        selectedTableId = selectedTableId,
                        selectedTable = selectedTable,
                        floorMenuOpen = floorMenuOpen,
                        tableMenuOpen = tableMenuOpen,
                        onFloorMenu = { floorMenuOpen = it; if (it) tableMenuOpen = false },
                        onTableMenu = { tableMenuOpen = it; if (it) floorMenuOpen = false },
                        onSelectFloor = { selectedFloorId = it; floorMenuOpen = false },
                        onSelectTable = { selectedTableId = it; tableMenuOpen = false },
                        currentOrder = visibleOrder,
                        allOrders = orders,
                        checkNumber = checkNumber(selectedTableId),
                        totalUsd = totalUsd,
                        totalKrw = totalKrw,
                        pendingCount = pendingCount,
                        canPay = canPay,
                        onMinus = { updateQty(it.id, it.qty - 1) },
                        onPlus = { updateQty(it.id, it.qty + 1) },
                        onRemove = { removeLine(it.id) },
                        onOrder = ::handleOrder,
                        onPay = { showPayment = true },
                        onHistory = { showHistory = true },
                        modifier = Modifier.height(availableHeight * splitFraction),
                    )
                    SplitHandle(
                        colors = colors,
                        modifier = Modifier.draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                splitFraction = (splitFraction + delta / totalHeightPx).coerceIn(0.2f, 0.8f)
                            },
                        ),
                    )
                    MenuPanel(
                        colors = colors,
                        selectedCategory = selectedCategoryId,
                        selectedSub = selectedSubId,
                        query = query,
                        items = currentItems,
                        onCategory = {
                            selectedCategoryId = it
                            selectedSubId = null
                        },
                        onSub = { selectedSubId = if (selectedSubId == it) null else it },
                        onQuery = { query = it },
                        onAdd = ::addItem,
                        columns = 4,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                Row(Modifier.fillMaxSize()) {
                    OrderPanel(
                        colors = colors,
                        role = role,
                        selectedFloorId = selectedFloorId,
                        selectedTableId = selectedTableId,
                        selectedTable = selectedTable,
                        floorMenuOpen = floorMenuOpen,
                        tableMenuOpen = tableMenuOpen,
                        onFloorMenu = { floorMenuOpen = it; if (it) tableMenuOpen = false },
                        onTableMenu = { tableMenuOpen = it; if (it) floorMenuOpen = false },
                        onSelectFloor = { selectedFloorId = it; floorMenuOpen = false },
                        onSelectTable = { selectedTableId = it; tableMenuOpen = false },
                        currentOrder = visibleOrder,
                        allOrders = orders,
                        checkNumber = checkNumber(selectedTableId),
                        totalUsd = totalUsd,
                        totalKrw = totalKrw,
                        pendingCount = pendingCount,
                        canPay = canPay,
                        onMinus = { updateQty(it.id, it.qty - 1) },
                        onPlus = { updateQty(it.id, it.qty + 1) },
                        onRemove = { removeLine(it.id) },
                        onOrder = ::handleOrder,
                        onPay = { showPayment = true },
                        onHistory = { showHistory = true },
                        modifier = Modifier.width(430.dp).fillMaxHeight(),
                    )
                    MenuPanel(
                        colors = colors,
                        selectedCategory = selectedCategoryId,
                        selectedSub = selectedSubId,
                        query = query,
                        items = currentItems,
                        onCategory = {
                            selectedCategoryId = it
                            selectedSubId = null
                        },
                        onSub = { selectedSubId = if (selectedSubId == it) null else it },
                        onQuery = { query = it },
                        onAdd = ::addItem,
                        columns = 5,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        AnimatedVisibility(showHistory, modifier = Modifier.fillMaxSize()) {
            HistorySheet(colors = colors, onClose = { showHistory = false })
        }

        AnimatedVisibility(showPayment, modifier = Modifier.fillMaxSize()) {
            PaymentSheet(
                colors = colors,
                totalUsd = totalUsd,
                totalKrw = totalKrw,
                checkNumber = checkNumber(selectedTableId),
                tableLabel = selectedTable?.label ?: selectedTableId,
                onClose = { showPayment = false },
            )
        }
    }
}
