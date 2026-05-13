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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.ui.i18n.orderCatalogString
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.posBackground
import kotlin.math.min
import java.util.UUID
import kotlinx.coroutines.delay

@Composable
fun OrdersScreen(
    colors: PosColors,
    role: ActiveRole,
    floorPaymentTableId: String? = null,
    floorPaymentNonce: Long = 0L,
    onConsumedFloorPayment: () -> Unit = {},
) {
    val density = LocalDensity.current
    val ctx = LocalContext.current
    var selectedFloorId by remember { mutableStateOf("1F") }
    var selectedTableId by remember { mutableStateOf("T12") }
    var floorMenuOpen by remember { mutableStateOf(false) }
    var tableMenuOpen by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf(OrderMenuCategories.first().id) }
    var selectedSubId by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var showPayment by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showOrderConfirm by remember { mutableStateOf(false) }
    var pendingModify by remember { mutableStateOf<Pair<OrderLine, OrderedLineModifyKind>?>(null) }
    var splitFraction by remember { mutableFloatStateOf(0.35f) }
    var orderScrollNonce by remember { mutableIntStateOf(0) }
    var orderHighlightLineId by remember { mutableStateOf<String?>(null) }
    val orders = remember {
        mutableStateMapOf<String, List<OrderLine>>().apply { putAll(initialOrderLines()) }
    }

    fun setCurrentOrder(next: List<OrderLine>) {
        orders[selectedTableId] = next
    }

    val currentOrder = orders[selectedTableId].orEmpty()
    val visibleOrder = currentOrder.filterNot { it.deleted }
    val (totalKrw, totalUsd) = orderCurrencyTotals(currentOrder)
    val selectedCategory = OrderMenuCategories.first { it.id == selectedCategoryId }
    val currentItems = selectedCategory.subCategories
        .filter { selectedSubId == null || it.id == selectedSubId }
        .flatMap { sub -> sub.items.map { item -> sub to item } }
        .filter { (_, item) ->
            query.isBlank() ||
                ctx.orderCatalogString("orders_item", item.id, item.id).contains(query.trim(), ignoreCase = true) ||
                item.id.contains(query.trim(), ignoreCase = true)
        }
    val pendingCount = currentOrder.count { line ->
        when {
            line.deleted && line.ordered -> true
            !line.deleted && !line.ordered -> true
            !line.deleted && line.ordered && line.origQty != null && line.qty != line.origQty -> true
            else -> false
        }
    }
    val pendingNewItems = currentOrder.filter { !it.ordered && !it.deleted }
    val selectedTable = OrderTables.firstOrNull { it.id == selectedTableId }
    val canPay = role == ActiveRole.Admin || role == ActiveRole.Cashier

    LaunchedEffect(floorPaymentNonce) {
        if (floorPaymentNonce == 0L) return@LaunchedEffect
        val tableId = floorPaymentTableId ?: return@LaunchedEffect
        OrderTables.firstOrNull { it.id == tableId }?.let { t -> selectedFloorId = t.floor }
        selectedTableId = tableId
        showPayment = true
        onConsumedFloorPayment()
    }

    LaunchedEffect(orderHighlightLineId) {
        val id = orderHighlightLineId ?: return@LaunchedEffect
        delay(1_100)
        if (orderHighlightLineId == id) {
            orderHighlightLineId = null
        }
    }

    LaunchedEffect(selectedTableId) {
        orderHighlightLineId = null
    }

    fun applyAdjustQty(lineId: String, delta: Int) {
        val current = orders[selectedTableId].orEmpty()
        orders[selectedTableId] = current.mapNotNull { existing ->
            if (existing.id != lineId) return@mapNotNull existing
            val nextQty = (existing.qty + delta).coerceAtLeast(0)
            if (!existing.ordered && nextQty == 0) return@mapNotNull null
            if (existing.ordered) {
                existing.copy(
                    qty = nextQty,
                    origQty = existing.origQty ?: existing.qty,
                    deleted = nextQty == 0,
                )
            } else {
                existing.copy(qty = nextQty)
            }
        }
    }

    fun applyRemoveLine(lineId: String) {
        val current = orders[selectedTableId].orEmpty()
        orders[selectedTableId] = current.mapNotNull { existing ->
            if (existing.id != lineId) return@mapNotNull existing
            if (existing.ordered) existing.copy(deleted = true, origQty = existing.origQty ?: existing.qty) else null
        }
    }

    // Already-ordered lines confirm before mutation; new lines mutate directly so
    // users aren't blocked on every tap while still building up the new section.
    fun requestAdjustQty(line: OrderLine, delta: Int) {
        if (!line.ordered) {
            applyAdjustQty(line.id, delta)
            return
        }
        val kind = if (delta >= 0) OrderedLineModifyKind.Increase else OrderedLineModifyKind.Decrease
        pendingModify = line to kind
    }

    fun requestRemoveLine(line: OrderLine) {
        if (!line.ordered) {
            applyRemoveLine(line.id)
            return
        }
        pendingModify = line to OrderedLineModifyKind.Remove
    }

    fun addItem(sub: OrderSubCategory, item: OrderMenuItem) {
        // Read fresh state from the snapshot map so rapid clicks across recompositions
        // don't accidentally overwrite each other with a stale `currentOrder` capture.
        val current = orders[selectedTableId].orEmpty()
        val existingIndex = current.indexOfFirst { it.baseId == item.id && !it.ordered && !it.deleted }
        val next = if (existingIndex >= 0) {
            current.toMutableList().also { list ->
                val existing = list[existingIndex]
                list[existingIndex] = existing.copy(qty = existing.qty + 1)
            }
        } else {
            current + OrderLine(
                id = "${item.id}-${UUID.randomUUID()}",
                baseId = item.id,
                price = item.price,
                qty = 1,
                categoryId = sub.id,
                currency = item.currency,
            )
        }
        orders[selectedTableId] = next
        val touchedId = if (existingIndex >= 0) {
            current[existingIndex].id
        } else {
            next.last().id
        }
        orderHighlightLineId = touchedId
        orderScrollNonce++
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

    Box(Modifier.fillMaxSize().background(posBackground(colors))) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            /** Use layout width so the order + menu split matches the real content area (not just screen config). */
            val useVerticalSplit = maxWidth < 768.dp
            if (useVerticalSplit) {
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
                        totalUsd = totalUsd,
                        totalKrw = totalKrw,
                        pendingCount = pendingCount,
                        canPay = canPay,
                        onMinus = { requestAdjustQty(it, -1) },
                        onPlus = { requestAdjustQty(it, +1) },
                        onRemove = { requestRemoveLine(it) },
                        onOrder = {
                            if (pendingNewItems.isNotEmpty()) {
                                showOrderConfirm = true
                            } else {
                                handleOrder()
                            }
                        },
                        onPay = { showPayment = true },
                        onHistory = { showHistory = true },
                        orderScrollNonce = orderScrollNonce,
                        highlightLineId = orderHighlightLineId,
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
                val orderPanelWidth = min(430f, maxWidth.value * 0.42f).dp
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
                        totalUsd = totalUsd,
                        totalKrw = totalKrw,
                        pendingCount = pendingCount,
                        canPay = canPay,
                        onMinus = { requestAdjustQty(it, -1) },
                        onPlus = { requestAdjustQty(it, +1) },
                        onRemove = { requestRemoveLine(it) },
                        onOrder = {
                            if (pendingNewItems.isNotEmpty()) {
                                showOrderConfirm = true
                            } else {
                                handleOrder()
                            }
                        },
                        onPay = { showPayment = true },
                        onHistory = { showHistory = true },
                        orderScrollNonce = orderScrollNonce,
                        highlightLineId = orderHighlightLineId,
                        modifier = Modifier.width(orderPanelWidth).fillMaxHeight(),
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
                tableLabel = selectedTable?.let { ctx.tableOrderLabel(it.id) } ?: selectedTableId,
                onClose = { showPayment = false },
            )
        }

        AnimatedVisibility(showOrderConfirm, modifier = Modifier.fillMaxSize()) {
            ConfirmOrderDialog(
                colors = colors,
                tableLabel = selectedTable?.let { ctx.tableOrderLabel(it.id) } ?: selectedTableId,
                newItems = pendingNewItems,
                onCancel = { showOrderConfirm = false },
                onConfirm = {
                    handleOrder()
                    showOrderConfirm = false
                },
            )
        }

        AnimatedVisibility(pendingModify != null, modifier = Modifier.fillMaxSize()) {
            val request = pendingModify
            if (request != null) {
                val (line, kind) = request
                ConfirmModifyOrderedDialog(
                    colors = colors,
                    line = line,
                    kind = kind,
                    onCancel = { pendingModify = null },
                    onConfirm = {
                        when (kind) {
                            OrderedLineModifyKind.Increase -> applyAdjustQty(line.id, +1)
                            OrderedLineModifyKind.Decrease -> applyAdjustQty(line.id, -1)
                            OrderedLineModifyKind.Remove -> applyRemoveLine(line.id)
                        }
                        pendingModify = null
                    },
                )
            }
        }
    }
}
