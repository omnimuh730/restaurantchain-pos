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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.MenuCategory
import com.mh.restaurantchainpos.pos.data.MenuItem
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.data.formatMoney
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
fun MenuManagement(colors: PosColors) {
    val categories = remember { PosMockData.menuCategories }
    var activeCategoryId by remember { mutableStateOf(categories.first().id) }
    var activeSubId by remember { mutableStateOf<String?>(categories.first().subCategories.firstOrNull()?.id) }
    val active = categories.firstOrNull { it.id == activeCategoryId } ?: categories.first()
    val activeSub = active.subCategories.firstOrNull { it.id == activeSubId }
    val items = activeSub?.items.orEmpty()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(colors = colors, title = "Categories", subtitle = "Tap to view, long-press to edit.") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { c ->
                    CategoryPill(
                        label = c.label,
                        active = c.id == activeCategoryId,
                        onClick = {
                            activeCategoryId = c.id
                            activeSubId = c.subCategories.firstOrNull()?.id
                        },
                    )
                }
            }
        }

        SettingCard(colors = colors, title = "Subcategories", subtitle = active.label) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                active.subCategories.forEach { sub ->
                    CategoryPill(
                        label = sub.label,
                        active = sub.id == activeSubId,
                        onClick = { activeSubId = sub.id },
                    )
                }
            }
        }

        SettingCard(
            colors = colors,
            title = "Items",
            subtitle = activeSub?.label ?: "—",
            badge = "${items.size}",
            badgeIcon = "🍽",
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(280.dp),
            ) {
                items(items) { item -> MenuTile(colors, item) }
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surfaceRaised)
                            .border(2.dp, colors.border, RoundedCornerShape(10.dp))
                            .clickable {},
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("+ Add item", color = colors.textMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) Blue500 else Color.Transparent)
            .border(1.dp, if (active) Blue500 else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(label, color = if (active) Color.White else Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MenuTile(colors: PosColors, item: MenuItem) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Blue500)
            .clickable {}
            .padding(12.dp),
    ) {
        Text(item.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 2)
        Spacer(Modifier.weight(1f))
        Text(formatMoney(item.price, item.currency), color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}
