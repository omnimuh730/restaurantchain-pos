package com.mh.restaurantchainpos.pos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.data.MenuItem
import com.mh.restaurantchainpos.pos.data.OrderItem
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.data.formatMoney
import com.mh.restaurantchainpos.pos.ui.components.PillButton
import com.mh.restaurantchainpos.pos.ui.components.PosCard
import com.mh.restaurantchainpos.pos.ui.components.PosPrimaryButton
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import java.util.UUID

@Composable
fun OrdersScreen(colors: PosColors) {
    var selectedTable by remember { mutableStateOf("T12") }
    var selectedCategoryId by remember { mutableStateOf(PosMockData.menuCategories.first().id) }
    var selectedSubId by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var showPayment by remember { mutableStateOf(false) }
    val orders = remember {
        mutableStateMapOf<String, List<OrderItem>>().apply { putAll(PosMockData.initialOrders) }
    }
    val currentOrder = orders[selectedTable].orEmpty()
    val category = PosMockData.menuCategories.first { it.id == selectedCategoryId }
    val visibleItems = category.subCategories
        .filter { selectedSubId == null || it.id == selectedSubId }
        .flatMap { sub -> sub.items.map { sub.label to it } }
        .filter { (_, item) -> query.isBlank() || item.name.contains(query, ignoreCase = true) }
    val totalUsd = currentOrder.filter { it.currency == CurrencyKind.Foreign && !it.deleted }.sumOf { it.price * it.qty }
    val totalKrw = currentOrder.filter { it.currency == CurrencyKind.Domestic && !it.deleted }.sumOf { it.price * it.qty }

    Row(Modifier.fillMaxSize()) {
        Column(Modifier.weight(0.42f).fillMaxSize().background(colors.surface).padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Current Check", color = colors.text, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))
                Text("Ch. #85", color = colors.textMuted, fontSize = 12.sp)
            }
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PosMockData.tables.forEach { (id, label) ->
                    PillButton(label, selectedTable == id, colors) { selectedTable = id }
                }
            }
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(currentOrder, key = { it.id }) { item ->
                    OrderRow(colors, item, onMinus = {
                        orders[selectedTable] = currentOrder.map { if (it.id == item.id) it.copy(qty = (it.qty - 1).coerceAtLeast(0), deleted = it.qty <= 1) else it }.filterNot { !it.ordered && it.qty == 0 }
                    }, onPlus = {
                        orders[selectedTable] = currentOrder.map { if (it.id == item.id) it.copy(qty = it.qty + 1, deleted = false) else it }
                    }, onRemove = {
                        orders[selectedTable] = currentOrder.map { if (it.id == item.id) it.copy(deleted = true) else it }.filterNot { !it.ordered && it.id == item.id }
                    })
                }
            }
            PosCard(colors, Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    Text("USD", color = colors.textMuted, fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    Text(formatMoney(totalUsd, CurrencyKind.Foreign), color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
                }
                Row(Modifier.fillMaxWidth()) {
                    Text("KRW", color = colors.textMuted, fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    Text(formatMoney(totalKrw, CurrencyKind.Domestic), color = Blue600, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PosPrimaryButton("Order", Modifier.weight(1f), enabled = currentOrder.isNotEmpty()) {
                        orders[selectedTable] = currentOrder.filterNot { it.deleted }.map { it.copy(ordered = true) }
                    }
                    PosPrimaryButton("Pay", Modifier.weight(1f), enabled = currentOrder.isNotEmpty()) { showPayment = true }
                }
            }
        }

        Column(Modifier.weight(0.58f).fillMaxSize().padding(14.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PosMockData.menuCategories.forEach {
                    PillButton(it.label, selectedCategoryId == it.id, colors) {
                        selectedCategoryId = it.id
                        selectedSubId = null
                    }
                }
            }
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillButton("All", selectedSubId == null, colors) { selectedSubId = null }
                category.subCategories.forEach { PillButton(it.label, selectedSubId == it.id, colors) { selectedSubId = it.id } }
            }
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Search menu") })
            LazyColumn(Modifier.fillMaxSize().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visibleItems, key = { it.second.id }) { (sub, item) ->
                    MenuTile(colors, sub, item) {
                        val existing = currentOrder.firstOrNull { it.baseId == item.id && !it.ordered }
                        orders[selectedTable] = if (existing != null) {
                            currentOrder.map { if (it.id == existing.id) it.copy(qty = it.qty + 1) else it }
                        } else {
                            currentOrder + OrderItem(UUID.randomUUID().toString(), item.id, item.name, item.price, 1, sub.uppercase(), item.currency)
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(showPayment) {
        PaymentSheet(colors, totalUsd, totalKrw, "Ch. #85", selectedTable) { showPayment = false }
    }
}

@Composable
private fun OrderRow(colors: PosColors, item: OrderItem, onMinus: () -> Unit, onPlus: () -> Unit, onRemove: () -> Unit) {
    PosCard(colors, Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.name, color = colors.text, fontWeight = FontWeight.Medium, textDecoration = if (item.deleted) TextDecoration.LineThrough else TextDecoration.None)
                Text(item.category, color = colors.textMuted, fontSize = 11.sp)
            }
            Text(formatMoney(item.price * item.qty, item.currency), color = if (item.currency == CurrencyKind.Domestic) Blue600 else Color(0xFFDC2626), fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QtyButton("-", colors, onMinus)
            Text(item.qty.toString(), color = colors.text, fontWeight = FontWeight.Medium)
            QtyButton("+", colors, onPlus)
            Spacer(Modifier.weight(1f))
            Text("Remove", color = Color(0xFFEF4444), modifier = Modifier.clickable(onClick = onRemove), fontSize = 12.sp)
        }
    }
}

@Composable
private fun MenuTile(colors: PosColors, category: String, item: MenuItem, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Blue600).clickable(onClick = onClick).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.name, color = Color.White, fontWeight = FontWeight.Medium)
                Text(category, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
            }
            Text(formatMoney(item.price, item.currency), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QtyButton(label: String, colors: PosColors, onClick: () -> Unit) {
    Box(Modifier.clip(RoundedCornerShape(8.dp)).background(colors.surfaceRaised).clickable(onClick = onClick).padding(horizontal = 11.dp, vertical = 6.dp)) {
        Text(label, color = colors.text, fontWeight = FontWeight.Bold)
    }
}
