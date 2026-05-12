package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue300
import com.mh.restaurantchainpos.pos.ui.theme.Blue400
import com.mh.restaurantchainpos.pos.ui.i18n.orderCatalogString
import com.mh.restaurantchainpos.pos.ui.i18n.rememberOrderCatalogString
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.posBackground

@Composable
internal fun MenuPanel(
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
    val ctx = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val category = OrderMenuCategories.first { it.id == selectedCategory }
    val catEntries = remember(locale) {
        OrderMenuCategories.map { it.id to ctx.orderCatalogString("orders_cat", it.id) }
    }
    val subEntries = remember(locale, selectedCategory) {
        category.subCategories.map { it.id to ctx.orderCatalogString("orders_sub", it.id) }
    }
    Column(
        modifier
            .background(posBackground(colors))
            .fillMaxHeight(),
    ) {
        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
            SearchBox(value = query, onValueChange = onQuery, colors = colors)
        }
        OrdersCategoryCarousel(
            entries = catEntries,
            selected = selectedCategory,
            colors = colors,
            activeStrong = true,
            selectedBorder = Blue400,
            onClick = onCategory,
            resetKey = "main_categories",
        )
        OrdersCategoryCarousel(
            entries = subEntries,
            selected = selectedSub,
            colors = colors,
            activeStrong = false,
            selectedBorder = Blue300,
            onClick = onSub,
            resetKey = selectedCategory,
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
private fun MenuTile(colors: PosColors, item: OrderMenuItem, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.chip)
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 5.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Text(
            rememberOrderCatalogString("orders_item", item.id, item.id),
            color = colors.text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
