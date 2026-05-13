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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.data.MenuItem
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.data.formatMoney
import com.mh.restaurantchainpos.pos.ui.components.PosElevatedSurface
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

private data class CatalogEntry(val id: String, val name: String, val price: Double)

private val FoodCatalog = listOf(
    CatalogEntry("wagyu-burger", "Wagyu Burger", 32.0),
    CatalogEntry("grilled-salmon", "Grilled Salmon", 28.0),
    CatalogEntry("ribeye-steak", "Ribeye Steak", 45.0),
    CatalogEntry("truffle-fries", "Truffle Fries", 12.0),
    CatalogEntry("caesar-salad", "Caesar Salad", 14.0),
    CatalogEntry("garlic-bread", "Garlic Bread", 7.0),
    CatalogEntry("sparkling-water", "Sparkling Water", 4.0),
    CatalogEntry("fresh-juice", "Fresh Juice", 7.0),
    CatalogEntry("craft-beer", "Craft Beer", 10.0),
    CatalogEntry("tiramisu", "Tiramisu", 13.0),
    CatalogEntry("margherita-pizza", "Margherita Pizza", 18.0),
    CatalogEntry("spaghetti-carbonara", "Spaghetti Carbonara", 22.0),
    CatalogEntry("lobster-roll", "Lobster Roll", 34.0),
    CatalogEntry("chicken-tikka", "Chicken Tikka", 19.0),
    CatalogEntry("pad-thai-cat", "Pad Thai", 17.0),
    CatalogEntry("edamame", "Edamame", 8.0),
    CatalogEntry("espresso", "Espresso", 4.0),
    CatalogEntry("cappuccino", "Cappuccino", 5.0),
    CatalogEntry("iced-latte", "Iced Latte", 6.0),
    CatalogEntry("red-wine-glass", "Red Wine (Glass)", 12.0),
    CatalogEntry("white-wine-glass", "White Wine (Glass)", 11.0),
    CatalogEntry("cheesecake", "Cheesecake", 11.0),
    CatalogEntry("chocolate-lava", "Chocolate Lava Cake", 12.0),
    CatalogEntry("fruit-platter", "Fruit Platter", 10.0),
)

@Composable
fun MenuManagement(colors: PosColors) {
    val categories = remember { PosMockData.menuCategories }
    var activeCategoryId by remember { mutableStateOf(categories.first().id) }
    var activeSubId by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var addItemSubId by remember { mutableStateOf<String?>(null) }

    // Locally-added items per sub-category id. Lets the user grow each sub-category beyond
    // the mock data without rebuilding the whole catalog tree.
    val addedItems: SnapshotStateMap<String, List<MenuItem>> = remember { mutableStateMapOf() }

    val active = categories.firstOrNull { it.id == activeCategoryId } ?: categories.first()
    val totalItems by remember(categories, addedItems) {
        derivedStateOf {
            categories.sumOf { c -> c.subCategories.sumOf { it.items.size + (addedItems[it.id]?.size ?: 0) } }
        }
    }
    val totalSubCategories by remember(categories) {
        derivedStateOf { categories.sumOf { it.subCategories.size } }
    }
    val activeSub = active.subCategories.firstOrNull { it.id == activeSubId }
    val itemsForActiveSub: List<MenuItem> = activeSub?.let { sub ->
        sub.items + (addedItems[sub.id] ?: emptyList())
    } ?: active.subCategories.flatMap { sub ->
        sub.items + (addedItems[sub.id] ?: emptyList())
    }
    val filteredItems = itemsForActiveSub.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(
            colors = colors,
            title = stringResource(R.string.settings_menu_page_title),
            subtitle = stringResource(R.string.settings_menu_page_subtitle),
            badge = stringResource(R.string.settings_menu_badge_items, totalItems),
            badgeIcon = Icons.Outlined.Restaurant,
            headerIcon = Icons.Outlined.Restaurant,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox(colors, stringResource(R.string.settings_menu_stat_categories), "${categories.size}", Modifier.weight(1f))
                StatBox(colors, stringResource(R.string.settings_menu_stat_subcategories), "$totalSubCategories", Modifier.weight(1f))
                StatBox(colors, stringResource(R.string.settings_menu_stat_total_items), "$totalItems", Modifier.weight(1f))
            }
        }

        SettingCard(colors = colors, title = stringResource(R.string.settings_menu_main_categories)) {
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

        SettingCard(colors = colors, title = stringResource(R.string.settings_menu_subcategories_format, active.label)) {
            CategoryGrid(
                colors = colors,
                items = active.subCategories.map { s ->
                    val totalForSub = s.items.size + (addedItems[s.id]?.size ?: 0)
                    CategoryCell(id = s.id, label = s.label, count = totalForSub)
                },
                activeId = activeSubId,
                onClick = { id ->
                    activeSubId = if (activeSubId == id) null else id
                },
            )
        }

        val itemsCardTitle = if (activeSub != null) {
            stringResource(R.string.settings_menu_sub_items_title, activeSub.label)
        } else {
            stringResource(R.string.settings_menu_all_cat_items, active.label)
        }
        SettingCard(colors = colors, title = itemsCardTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.weight(1f)) {
                        SettingTextField(
                            colors = colors,
                            value = query,
                            onChange = { query = it },
                            placeholder = stringResource(R.string.settings_menu_search_ph),
                            leadingIcon = Icons.Outlined.Search,
                        )
                    }
                    if (activeSub != null) {
                        PrimaryButton(
                            label = stringResource(R.string.settings_menu_add_item),
                            onClick = { addItemSubId = activeSub.id },
                            leadingIcon = Icons.Outlined.Add,
                        )
                    }
                }

                if (activeSub == null) {
                    Text(
                        stringResource(R.string.settings_menu_select_sub_hint),
                        color = colors.textMuted,
                        fontSize = 12.sp,
                    )
                } else if (filteredItems.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            if (query.isNotBlank()) {
                                stringResource(R.string.settings_menu_no_search)
                            } else {
                                stringResource(R.string.settings_menu_empty_sub)
                            },
                            color = colors.textMuted,
                            fontSize = 12.sp,
                        )
                    }
                }

                if (filteredItems.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filteredItems.chunked(2).forEach { row ->
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

    val targetSub = addItemSubId?.let { id -> active.subCategories.firstOrNull { it.id == id } }
    if (targetSub != null) {
        val existingIds = (targetSub.items + (addedItems[targetSub.id] ?: emptyList())).map { it.id }.toSet()
        AddCatalogItemsDialog(
            colors = colors,
            subLabel = targetSub.label,
            existingIds = existingIds,
            onDismiss = { addItemSubId = null },
            onConfirm = { selectedIds ->
                val newItems = FoodCatalog
                    .filter { it.id in selectedIds && it.id !in existingIds }
                    .map { MenuItem(id = it.id, name = it.name, price = it.price, currency = CurrencyKind.Foreign) }
                if (newItems.isNotEmpty()) {
                    val current = addedItems[targetSub.id] ?: emptyList()
                    addedItems[targetSub.id] = current + newItems
                }
                addItemSubId = null
            },
        )
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
    PosElevatedSurface(
        colors,
        modifier.clickable {},
        RoundedCornerShape(10.dp),
        fillColor = colors.surfaceRaised,
        borderWidth = 1.dp,
        borderColor = colors.border,
    ) {
        Column(Modifier.padding(14.dp).fillMaxWidth()) {
            Text(item.name, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(formatMoney(item.price, item.currency), color = Blue500, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AddCatalogItemsDialog(
    colors: PosColors,
    subLabel: String,
    existingIds: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit,
) {
    var search by remember { mutableStateOf("") }
    val selected = remember { mutableStateOf(setOf<String>()) }
    val filteredCatalog = FoodCatalog.filter {
        search.isBlank() || it.name.contains(search, ignoreCase = true)
    }

    ModalScrim(onDismiss = onDismiss) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .widthIn(max = 460.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                .consumeModalTaps(),
        ) {
            // Header
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.settings_menu_add_title, subLabel), color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(stringResource(R.string.settings_menu_add_subtitle), color = colors.textMuted, fontSize = 12.sp)
                }
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(18.dp))
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))

            // Body
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SettingTextField(
                    colors = colors,
                    value = search,
                    onChange = { search = it },
                    placeholder = stringResource(R.string.settings_menu_search_catalog_ph),
                    leadingIcon = Icons.Outlined.Search,
                )

                Column(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (filteredCatalog.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.settings_menu_no_search), color = colors.textMuted, fontSize = 12.sp)
                        }
                    } else {
                        filteredCatalog.chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                row.forEach { entry ->
                                    val alreadyAdded = entry.id in existingIds
                                    val isSelected = entry.id in selected.value
                                    CatalogTile(
                                        colors = colors,
                                        entry = entry,
                                        alreadyAdded = alreadyAdded,
                                        isSelected = isSelected,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        if (alreadyAdded) return@CatalogTile
                                        selected.value = if (isSelected) {
                                            selected.value - entry.id
                                        } else {
                                            selected.value + entry.id
                                        }
                                    }
                                }
                                if (row.size == 1) Box(Modifier.weight(1f))
                            }
                        }
                    }
                }

                Text(
                    stringResource(R.string.settings_menu_selected_count, selected.value.size),
                    color = colors.textMuted,
                    fontSize = 12.sp,
                )
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))

            // Footer buttons
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlineButton(stringResource(R.string.common_cancel), colors.text, onClick = onDismiss)
                Spacer(Modifier.size(8.dp))
                val count = selected.value.size
                val label = when (count) {
                    0 -> stringResource(R.string.settings_menu_add_btn_zero)
                    1 -> stringResource(R.string.settings_menu_add_btn_one)
                    else -> stringResource(R.string.settings_menu_add_btn_n, count)
                }
                PrimaryButton(
                    label = label,
                    onClick = { onConfirm(selected.value) },
                    enabled = count > 0,
                )
            }
        }
    }
}

@Composable
private fun CatalogTile(
    colors: PosColors,
    entry: CatalogEntry,
    alreadyAdded: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val borderColor = when {
        alreadyAdded -> Color.Transparent
        isSelected -> Blue600
        else -> Color.Transparent
    }
    val background = when {
        isSelected && !alreadyAdded -> Blue500.copy(alpha = 0.10f)
        else -> colors.surfaceRaised
    }
    val alpha = if (alreadyAdded) 0.5f else 1f

    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !alreadyAdded, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    entry.name,
                    color = colors.text.copy(alpha = alpha),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "$%.2f".format(entry.price),
                    color = colors.textMuted.copy(alpha = alpha),
                    fontSize = 12.sp,
                )
                if (alreadyAdded) {
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.settings_menu_already_added), color = colors.textMuted, fontSize = 10.sp)
                }
            }
            Spacer(Modifier.size(8.dp))
            CatalogCheckCircle(colors = colors, selected = isSelected, dim = alreadyAdded)
        }
    }
}

@Composable
private fun CatalogCheckCircle(colors: PosColors, selected: Boolean, dim: Boolean) {
    val borderColor = when {
        selected -> Blue600
        dim -> colors.border
        else -> colors.textMuted
    }
    val background = if (selected) Blue600 else Color.Transparent
    Box(
        Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(background)
            .border(2.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
        }
    }
}
