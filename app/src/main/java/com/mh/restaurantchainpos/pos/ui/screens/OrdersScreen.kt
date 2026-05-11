package com.mh.restaurantchainpos.pos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.ui.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
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

@Composable
private fun OrderPanel(
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

@Composable
private fun MenuPanel(
    colors: PosColors,
    selectedCategory: String,
    selectedSub: String?,
    query: String,
    items: List<Pair<OrderSubCategory, OrderMenuItem>>,
    onCategory: (String) -> Unit,
    onSub: (String) -> Unit,
    onQuery: (String) -> Unit,
    onAdd: (OrderSubCategory, OrderMenuItem) -> Unit,
    columns: Int,
    modifier: Modifier = Modifier,
) {
    val category = OrderMenuCategories.first { it.id == selectedCategory }
    Column(
        modifier
            .background(colors.surfaceRaised)
            .fillMaxHeight(),
    ) {
        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
            SearchBox(value = query, onValueChange = onQuery, colors = colors)
        }
        GridButtons(
            entries = OrderMenuCategories.map { it.id to it.label },
            selected = selectedCategory,
            colors = colors,
            columns = 4,
            activeStrong = true,
            onClick = onCategory,
        )
        GridButtons(
            entries = category.subCategories.map { it.id to it.label },
            selected = selectedSub,
            colors = colors,
            columns = 4,
            activeStrong = false,
            onClick = onSub,
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            gridItems(items, key = { it.second.id }) { (sub, item) ->
                MenuTile(colors = colors, item = item, onClick = { onAdd(sub, item) })
            }
        }
    }
}

@Composable
private fun GridButtons(
    entries: List<Pair<String, String>>,
    selected: String?,
    colors: PosColors,
    columns: Int,
    activeStrong: Boolean,
    onClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (entries.size > columns) 92.dp else 46.dp)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false,
    ) {
        gridItems(entries, key = { it.first }) { (id, label) ->
            val active = selected == id
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when {
                            active && activeStrong -> Blue600
                            active -> Blue500
                            else -> colors.surfaceRaised.copy(alpha = if (activeStrong) 1f else 0.7f)
                        },
                    )
                    .border(
                        1.dp,
                        if (active) Blue500.copy(alpha = 0.7f) else Color.Transparent,
                        RoundedCornerShape(4.dp),
                    )
                    .clickable { onClick(id) }
                    .padding(horizontal = 7.dp, vertical = 5.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                Text(
                    label,
                    color = if (active) Color.White else colors.text,
                    fontSize = 11.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun OrderTableHeader(colors: PosColors) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(28.dp)
            .border(1.dp, colors.border)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Item", color = Blue600, fontSize = 10.sp, modifier = Modifier.weight(1f))
        Text("Qty", color = Blue600, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.width(74.dp))
        Text("Each", color = Blue600, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(58.dp))
        Text("Line", color = Blue600, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(64.dp))
        Spacer(Modifier.width(22.dp))
    }
}

@Composable
private fun OrderLineRow(
    colors: PosColors,
    line: OrderLine,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                line.name,
                color = colors.text,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (line.deleted) TextDecoration.LineThrough else TextDecoration.None,
            )
            if (line.modifiers.isNotEmpty()) {
                Text(
                    line.modifiers.joinToString(" / "),
                    color = colors.textMuted,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(Modifier.width(74.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            QtyButton("-", colors, onMinus)
            Text(line.qty.toString(), color = colors.text, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.width(24.dp))
            QtyButton("+", colors, onPlus)
        }
        Text(formatLineMoney(line.price, line.currency), color = colors.text, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(58.dp))
        Text(formatLineMoney(line.price * line.qty, line.currency), color = colors.text, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(64.dp))
        Text("x", color = colors.textMuted, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.width(22.dp).clickable(onClick = onRemove))
    }
}

@Composable
private fun MenuTile(colors: PosColors, item: OrderMenuItem, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 5.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Text(
            item.name,
            color = colors.text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SearchBox(value: String, onValueChange: (String) -> Unit, colors: PosColors) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = colors.text, fontSize = 12.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                SearchGlyph(colors.textMuted)
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isBlank()) Text("Search", color = colors.textMuted.copy(alpha = 0.72f), fontSize = 11.sp)
                    inner()
                }
            }
        },
    )
}

@Composable
private fun SearchGlyph(color: Color) {
    Canvas(Modifier.size(14.dp)) {
        drawCircle(
            color = color,
            radius = size.minDimension * 0.32f,
            center = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.42f),
            style = Stroke(width = 1.7f),
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.66f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.9f),
            strokeWidth = 1.7f,
        )
    }
}

@Composable
private fun DropdownChip(
    text: String,
    expanded: Boolean,
    colors: PosColors,
    onExpandedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    Box {
        Box(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surfaceRaised)
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 9.dp, vertical = 7.dp),
        ) {
            Text("$text ${if (expanded) "^" else "v"}", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            content()
        }
    }
}

@Composable
private fun CompactButton(text: String, colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ActionButton(
    text: String,
    active: Boolean,
    enabled: Boolean,
    colors: PosColors,
    modifier: Modifier = Modifier,
    badge: Int? = null,
    onClick: () -> Unit,
) {
    val bg = when {
        !enabled -> colors.surfaceRaised
        active -> Blue600
        else -> Color.Transparent
    }
    val fg = when {
        !enabled -> colors.textMuted.copy(alpha = 0.65f)
        active -> Color.White
        else -> Blue600
    }
    Row(
        modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, if (!active && enabled) Blue600 else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (badge != null) {
            Spacer(Modifier.width(6.dp))
            Box(Modifier.size(18.dp).clip(CircleShape).background(Blue600), contentAlignment = Alignment.Center) {
                Text(badge.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun QtyButton(text: String, colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .size(width = 18.dp, height = 18.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = colors.textMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TotalRow(label: String, value: String, colors: PosColors) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text(value, color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SplitHandle(colors: PosColors, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(colors.surface)
            .border(1.dp, colors.border),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(width = 40.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(colors.textMuted.copy(alpha = 0.35f)))
    }
}

@Composable
private fun HistorySheet(colors: PosColors, onClose: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.48f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clickable(enabled = false) {},
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Today's Bill History", color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("${TodayBills.size} bills - ${formatDomesticWon(TodayBills.sumOf { it.krw })} - ${formatForeignUsd(TodayBills.sumOf { it.usd })}", color = colors.textMuted, fontSize = 12.sp)
                }
                Spacer(Modifier.weight(1f))
                Text("Close", color = colors.textMuted, fontSize = 13.sp, modifier = Modifier.clickable(onClick = onClose))
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(TodayBills, key = { it.id }) { bill ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("${bill.id} - ${tableLabel(bill.tableId)}", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("${bill.time} - ${bill.method}", color = colors.textMuted, fontSize = 11.sp)
                        }
                        Text(paySummary(bill.krw, bill.usd), color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatDomesticWon(value: Double): String = "\u20A9" + "%,.0f".format(value)
private fun formatForeignUsd(value: Double): String = "$" + "%,.2f".format(value)

private fun formatLineMoney(value: Double, currency: CurrencyKind): String =
    if (currency == CurrencyKind.Domestic) formatDomesticWon(value) else formatForeignUsd(value)

private fun paySummary(krw: Double, usd: Double): String =
    listOfNotNull(
        if (krw > 0.0) formatDomesticWon(krw) else null,
        if (usd > 0.0 || krw == 0.0) formatForeignUsd(usd) else null,
    ).joinToString(" - ")

private fun floorLabel(id: String): String = OrderFloors.firstOrNull { it.id == id }?.label ?: "All Floors"
private fun tableLabel(id: String): String = OrderTables.firstOrNull { it.id == id }?.label ?: id
private fun checkNumber(id: String): String = CheckNumbers[id] ?: "Ch. #--"

private data class OrderFloor(val id: String, val label: String)
private data class OrderTable(val id: String, val label: String, val seats: Int, val floor: String)
private data class OrderMenuCategory(val id: String, val label: String, val subCategories: List<OrderSubCategory>)
private data class OrderSubCategory(val id: String, val label: String, val items: List<OrderMenuItem>)
private data class OrderMenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val currency: CurrencyKind = CurrencyKind.Foreign,
)

private data class OrderLine(
    val id: String,
    val baseId: String,
    val name: String,
    val price: Double,
    val qty: Int,
    val category: String,
    val currency: CurrencyKind,
    val modifiers: List<String> = emptyList(),
    val ordered: Boolean = false,
    val deleted: Boolean = false,
    val origQty: Int? = null,
)

private data class HistoryBill(
    val id: String,
    val tableId: String,
    val time: String,
    val krw: Double,
    val usd: Double,
    val method: String,
)

private val OrderFloors = listOf(
    OrderFloor("1F", "1st Floor"),
    OrderFloor("2F", "2nd Floor"),
    OrderFloor("bar", "Bar"),
)

private val OrderTables = listOf(
    OrderTable("T1", "Table 1", 2, "1F"),
    OrderTable("T2", "Table 2", 4, "1F"),
    OrderTable("T3", "Table 3", 4, "1F"),
    OrderTable("T4", "Table 4", 6, "1F"),
    OrderTable("T5", "Table 5", 2, "1F"),
    OrderTable("T6", "Table 6", 8, "2F"),
    OrderTable("T7", "Table 7", 4, "2F"),
    OrderTable("T8", "Table 8", 6, "2F"),
    OrderTable("T9", "Table 9", 2, "2F"),
    OrderTable("T10", "Table 10", 4, "2F"),
    OrderTable("T11", "Table 11", 6, "2F"),
    OrderTable("T12", "Table 12", 8, "2F"),
    OrderTable("BAR1", "Bar 1", 1, "bar"),
    OrderTable("BAR2", "Bar 2", 1, "bar"),
    OrderTable("BAR3", "Bar 3", 1, "bar"),
)

private val CheckNumbers = mapOf(
    "T1" to "Ch. #71",
    "T2" to "Ch. #72",
    "T3" to "Ch. #73",
    "T4" to "Ch. #74",
    "T5" to "Ch. #75",
    "T6" to "Ch. #76",
    "T7" to "Ch. #77",
    "T8" to "Ch. #78",
    "T9" to "Ch. #79",
    "T10" to "Ch. #80",
    "T11" to "Ch. #81",
    "T12" to "Ch. #85",
    "BAR1" to "Ch. #90",
    "BAR2" to "Ch. #91",
    "BAR3" to "Ch. #92",
)

private val OrderMenuCategories = listOf(
    OrderMenuCategory(
        "hot-foods",
        "Hot Foods",
        listOf(
            OrderSubCategory("dumplings", "Dumplings", listOf(
                OrderMenuItem("pork-dumplings", "Pork Dumplings", 8.0),
                OrderMenuItem("shrimp-dumplings", "Shrimp Dumplings", 9.0),
                OrderMenuItem("vegetable-dumplings", "Vegetable Dumplings", 7.0),
                OrderMenuItem("chicken-dumplings", "Chicken Dumplings", 8.0),
                OrderMenuItem("soup-dumplings", "Soup Dumplings", 10.0),
            )),
            OrderSubCategory("spring-rolls", "Spring Rolls", listOf(
                OrderMenuItem("veggie-spring-roll", "Vegetable Spring Rolls", 6.0),
                OrderMenuItem("pork-spring-roll", "Pork Spring Rolls", 7.0),
                OrderMenuItem("shrimp-spring-roll", "Shrimp Spring Rolls", 8.0),
                OrderMenuItem("crispy-rolls", "Crispy Egg Rolls", 7.0),
            )),
            OrderSubCategory("bao-buns", "Bao Buns", listOf(
                OrderMenuItem("pork-belly-bao", "Pork Belly Bao", 9000.0, CurrencyKind.Domestic),
                OrderMenuItem("chicken-bao", "Chicken Bao", 8000.0, CurrencyKind.Domestic),
                OrderMenuItem("veggie-bao", "Vegetable Bao", 7000.0, CurrencyKind.Domestic),
                OrderMenuItem("duck-bao", "Duck Bao", 10000.0, CurrencyKind.Domestic),
            )),
            OrderSubCategory("hot-soups", "Hot Soups", listOf(
                OrderMenuItem("miso-soup", "Miso Soup", 5000.0, CurrencyKind.Domestic),
                OrderMenuItem("hot-sour-soup", "Hot & Sour Soup", 6.0),
                OrderMenuItem("wonton-soup", "Wonton Soup", 7.0),
                OrderMenuItem("ramen", "Ramen", 12000.0, CurrencyKind.Domestic),
                OrderMenuItem("pho", "Pho", 13.0),
            )),
            OrderSubCategory("hot-appetizers", "Hot Appetizers", listOf(
                OrderMenuItem("edamame", "Edamame", 5.0),
                OrderMenuItem("gyoza", "Gyoza", 8000.0, CurrencyKind.Domestic),
                OrderMenuItem("takoyaki", "Takoyaki", 9000.0, CurrencyKind.Domestic),
                OrderMenuItem("tempura", "Tempura", 10.0),
            )),
        ),
    ),
    OrderMenuCategory(
        "cold-foods",
        "Cold Foods",
        listOf(
            OrderSubCategory("sushi-sashimi", "Sushi & Sashimi", listOf(
                OrderMenuItem("salmon-sushi", "Salmon Sushi", 8.0),
                OrderMenuItem("tuna-sushi", "Tuna Sushi", 9.0),
                OrderMenuItem("california-roll", "California Roll", 10.0),
                OrderMenuItem("spicy-tuna-roll", "Spicy Tuna Roll", 11.0),
                OrderMenuItem("sashimi-platter", "Sashimi Platter", 20.0),
            )),
            OrderSubCategory("cold-appetizers", "Cold Appetizers", listOf(
                OrderMenuItem("seaweed-salad", "Seaweed Salad", 6.0),
                OrderMenuItem("kimchi", "Kimchi", 5000.0, CurrencyKind.Domestic),
                OrderMenuItem("pickled-vegetables", "Pickled Vegetables", 5000.0, CurrencyKind.Domestic),
            )),
            OrderSubCategory("salads", "Salads", listOf(
                OrderMenuItem("asian-chicken-salad", "Asian Chicken Salad", 10.0),
                OrderMenuItem("cucumber-salad", "Cucumber Salad", 6.0),
                OrderMenuItem("papaya-salad", "Papaya Salad", 8.0),
            )),
            OrderSubCategory("cold-noodles", "Cold Noodles", listOf(
                OrderMenuItem("soba-noodles", "Cold Soba Noodles", 9.0),
                OrderMenuItem("sesame-noodles", "Sesame Noodles", 8.0),
            )),
        ),
    ),
    OrderMenuCategory(
        "main-meal",
        "Main Meal",
        listOf(
            OrderSubCategory("rice-dishes", "Rice Dishes", listOf(
                OrderMenuItem("fried-rice", "Fried Rice", 12.0),
                OrderMenuItem("bibimbap", "Bibimbap", 14000.0, CurrencyKind.Domestic),
                OrderMenuItem("curry-rice", "Curry Rice", 13.0),
                OrderMenuItem("donburi", "Chicken Donburi", 14000.0, CurrencyKind.Domestic),
            )),
            OrderSubCategory("noodle-dishes", "Noodle Dishes", listOf(
                OrderMenuItem("pad-thai", "Pad Thai", 13.0),
                OrderMenuItem("chow-mein", "Chow Mein", 12.0),
                OrderMenuItem("lo-mein", "Lo Mein", 12.0),
                OrderMenuItem("udon", "Udon Noodles", 13000.0, CurrencyKind.Domestic),
                OrderMenuItem("dan-dan-noodles", "Dan Dan Noodles", 11.0),
            )),
            OrderSubCategory("stir-fry", "Stir Fry", listOf(
                OrderMenuItem("kung-pao-chicken", "Kung Pao Chicken", 15.0),
                OrderMenuItem("mongolian-beef", "Mongolian Beef", 16.0),
                OrderMenuItem("cashew-chicken", "Cashew Chicken", 15.0),
                OrderMenuItem("mixed-vegetables", "Mixed Vegetables", 12.0),
            )),
            OrderSubCategory("curry", "Curry", listOf(
                OrderMenuItem("thai-green-curry", "Thai Green Curry", 14.0),
                OrderMenuItem("thai-red-curry", "Thai Red Curry", 14.0),
                OrderMenuItem("japanese-curry", "Japanese Curry", 13.0),
                OrderMenuItem("massaman-curry", "Massaman Curry", 15.0),
            )),
            OrderSubCategory("grilled-bbq", "Grilled & BBQ", listOf(
                OrderMenuItem("teriyaki-chicken", "Teriyaki Chicken", 16000.0, CurrencyKind.Domestic),
                OrderMenuItem("bulgogi", "Bulgogi", 18000.0, CurrencyKind.Domestic),
                OrderMenuItem("grilled-salmon", "Grilled Salmon", 20.0),
                OrderMenuItem("yakitori", "Yakitori", 12000.0, CurrencyKind.Domestic),
            )),
        ),
    ),
    OrderMenuCategory(
        "drinks",
        "Drinks",
        listOf(
            OrderSubCategory("tea", "Tea", listOf(
                OrderMenuItem("green-tea", "Green Tea", 3000.0, CurrencyKind.Domestic),
                OrderMenuItem("jasmine-tea", "Jasmine Tea", 3000.0, CurrencyKind.Domestic),
                OrderMenuItem("oolong-tea", "Oolong Tea", 3500.0, CurrencyKind.Domestic),
                OrderMenuItem("bubble-tea", "Bubble Tea", 5000.0, CurrencyKind.Domestic),
                OrderMenuItem("thai-tea", "Thai Tea", 4.5),
            )),
            OrderSubCategory("soft-drinks", "Soft Drinks", listOf(
                OrderMenuItem("coke", "Coca-Cola", 3.0),
                OrderMenuItem("sprite", "Sprite", 3.0),
                OrderMenuItem("ginger-ale", "Ginger Ale", 3.0),
            )),
            OrderSubCategory("juice", "Juice", listOf(
                OrderMenuItem("lychee-juice", "Lychee Juice", 4.0),
                OrderMenuItem("mango-juice", "Mango Juice", 4.0),
                OrderMenuItem("coconut-water", "Coconut Water", 4.5),
            )),
            OrderSubCategory("beer", "Beer", listOf(
                OrderMenuItem("asahi", "Asahi", 7.0),
                OrderMenuItem("sapporo", "Sapporo", 7.0),
                OrderMenuItem("singha", "Singha", 6.0),
                OrderMenuItem("tsingtao", "Tsingtao", 6.0),
            )),
            OrderSubCategory("wine", "Wine", listOf(
                OrderMenuItem("plum-wine", "Plum Wine", 8.0),
                OrderMenuItem("red-wine", "Red Wine", 9.0),
                OrderMenuItem("white-wine", "White Wine", 9.0),
            )),
            OrderSubCategory("sake-soju", "Sake & Soju", listOf(
                OrderMenuItem("sake-hot", "Hot Sake", 8000.0, CurrencyKind.Domestic),
                OrderMenuItem("sake-cold", "Cold Sake", 8000.0, CurrencyKind.Domestic),
                OrderMenuItem("soju", "Soju", 7000.0, CurrencyKind.Domestic),
                OrderMenuItem("makgeolli", "Makgeolli", 9000.0, CurrencyKind.Domestic),
            )),
            OrderSubCategory("cocktails", "Cocktails", listOf(
                OrderMenuItem("lychee-martini", "Lychee Martini", 12.0),
                OrderMenuItem("sake-bomb", "Sake Bomb", 10.0),
                OrderMenuItem("mai-tai", "Mai Tai", 11.0),
                OrderMenuItem("singapore-sling", "Singapore Sling", 12.0),
            )),
        ),
    ),
)

private fun initialOrderLines(): Map<String, List<OrderLine>> = mapOf(
    "T12" to listOf(
        OrderLine("lychee-martini", "lychee-martini", "Lychee Martini", 12.0, 2, "Cocktails", CurrencyKind.Foreign, ordered = true),
        OrderLine("wings", "wings", "Chicken Wings", 12.0, 1, "Appetizers", CurrencyKind.Foreign, ordered = true),
        OrderLine(
            id = "grilled-salmon",
            baseId = "grilled-salmon",
            name = "Grilled Salmon",
            price = 20.0,
            qty = 1,
            category = "Grilled BBQ",
            currency = CurrencyKind.Foreign,
            modifiers = listOf("NO Garlic", "Side Asparagus"),
            ordered = true,
        ),
        OrderLine(
            id = "bulgogi",
            baseId = "bulgogi",
            name = "Bulgogi",
            price = 18000.0,
            qty = 1,
            category = "Grilled BBQ",
            currency = CurrencyKind.Domestic,
            modifiers = listOf("Medium Rare"),
            ordered = true,
        ),
    ),
)

private val TodayBills = listOf(
    HistoryBill("B-1042", "T8", "08:42", 38000.0, 0.0, "Cash"),
    HistoryBill("B-1043", "T3", "09:15", 0.0, 42.5, "Credit"),
    HistoryBill("B-1044", "T6", "10:03", 124000.0, 18.0, "Mix"),
    HistoryBill("B-1045", "BAR1", "10:51", 22000.0, 0.0, "Cash"),
    HistoryBill("B-1046", "T1", "11:28", 0.0, 96.75, "Credit"),
    HistoryBill("B-1047", "T11", "12:09", 64500.0, 0.0, "Cash"),
    HistoryBill("B-1048", "T4", "12:47", 88000.0, 24.0, "Mix"),
)
