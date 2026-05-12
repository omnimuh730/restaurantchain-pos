package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue300
import com.mh.restaurantchainpos.pos.ui.theme.Blue400
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

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
            selectedBorder = Blue400,
            onClick = onCategory,
        )
        GridButtons(
            entries = category.subCategories.map { it.id to it.label },
            selected = selectedSub,
            colors = colors,
            columns = 4,
            activeStrong = false,
            selectedBorder = Blue300,
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
    selectedBorder: Color,
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
                            else -> colors.chip
                        },
                    )
                    .border(
                        1.dp,
                        if (active) selectedBorder else Color.Transparent,
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
