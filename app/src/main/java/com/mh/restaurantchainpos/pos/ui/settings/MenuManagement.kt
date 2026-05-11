package com.mh.restaurantchainpos.pos.ui.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.MenuItem
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.data.formatMoney
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
fun MenuManagement(colors: PosColors) {
    val categories = remember { PosMockData.menuCategories }
    var activeCategoryId by remember { mutableStateOf(categories.first().id) }
    var activeSubId by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }

    val active = categories.firstOrNull { it.id == activeCategoryId } ?: categories.first()
    val totalItems by remember(categories) {
        derivedStateOf { categories.sumOf { c -> c.subCategories.sumOf { it.items.size } } }
    }
    val totalSubCategories by remember(categories) {
        derivedStateOf { categories.sumOf { it.subCategories.size } }
    }
    val activeSub = active.subCategories.firstOrNull { it.id == activeSubId }
    val items: List<MenuItem> = (activeSub?.items ?: active.subCategories.flatMap { it.items })
        .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(
            colors = colors,
            title = "Menu Management",
            subtitle = "Select a sub-category and add items from the catalog",
            badge = "$totalItems items",
            badgeIcon = Icons.Outlined.Restaurant,
            headerIcon = Icons.Outlined.Restaurant,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox(colors, "Categories", "${categories.size}", Modifier.weight(1f))
                StatBox(colors, "Sub-Categories", "$totalSubCategories", Modifier.weight(1f))
                StatBox(colors, "Total Items", "$totalItems", Modifier.weight(1f))
            }
        }

        SettingCard(colors = colors, title = "Main Categories") {
            CategoryGrid(
                colors = colors,
                items = categories.map { c -> CategoryCell(id = c.id, label = c.label, count = null) },
                activeId = activeCategoryId,
                onClick = { id ->
                    activeCategoryId = id
                    activeSubId = null
                    query = ""
                },
            )
        }

        SettingCard(colors = colors, title = "${active.label} - Sub-Categories") {
            CategoryGrid(
                colors = colors,
                items = active.subCategories.map { s -> CategoryCell(id = s.id, label = s.label, count = s.items.size) },
                activeId = activeSubId,
                onClick = { id ->
                    activeSubId = if (activeSubId == id) null else id
                },
            )
        }

        SettingCard(colors = colors, title = "All ${active.label} Items") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingTextField(
                    colors = colors,
                    value = query,
                    onChange = { query = it },
                    placeholder = "Search items...",
                    leadingIcon = Icons.Outlined.Search,
                )
                if (activeSub == null) {
                    Text(
                        "Select a sub-category above to add items",
                        color = colors.textMuted,
                        fontSize = 12.sp,
                    )
                }
                if (items.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items.chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                row.forEach { item -> MenuItemTile(colors, item, Modifier.weight(1f)) }
                                if (row.size == 1) Box(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class CategoryCell(val id: String, val label: String, val count: Int?)

@Composable
private fun CategoryGrid(
    colors: PosColors,
    items: List<CategoryCell>,
    activeId: String?,
    onClick: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { cell ->
                    val active = cell.id == activeId
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (active) Blue600 else colors.surfaceRaised)
                            .clickable { onClick(cell.id) }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                    ) {
                        Text(
                            cell.label,
                            color = if (active) Color.White else colors.text,
                            fontSize = 13.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                        )
                        if (cell.count != null) {
                            Text(
                                "${cell.count}",
                                color = if (active) Color.White.copy(alpha = 0.8f) else colors.textMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.CenterEnd),
                            )
                        }
                    }
                }
                if (row.size == 1) Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MenuItemTile(colors: PosColors, item: MenuItem, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable {}
            .padding(14.dp),
    ) {
        Text(item.name, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
        Spacer(Modifier.height(4.dp))
        Text(formatMoney(item.price, item.currency), color = Blue500, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
