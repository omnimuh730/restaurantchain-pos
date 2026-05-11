package com.mh.restaurantchainpos.pos.ui.layout.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.PosPage
import com.mh.restaurantchainpos.pos.ui.components.CountBadge
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosDimens

@Composable
fun PosAppBottomBar(
    colors: PosColors,
    pages: List<PosPage>,
    selected: PosPage,
    badges: Map<String, Int>,
    onSelect: (PosPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(PosDimens.BottomNavHeight)
            .background(colors.navBackground)
            .border(1.dp, colors.headerBorder),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        pages.forEach { item ->
            val active = item == selected
            val badge = badges[item.badgeKey] ?: 0
            Column(
                Modifier
                    .weight(1f)
                    .clickable { onSelect(item) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(Modifier.size(width = 34.dp, height = 26.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = posPageNavIcon(item),
                        contentDescription = item.label,
                        tint = if (active) colors.text else colors.navInactive,
                        modifier = Modifier.size(22.dp),
                    )
                    if (badge > 0) {
                        CountBadge(count = badge, modifier = Modifier.align(Alignment.TopEnd))
                    }
                }
                Text(item.label, color = if (active) colors.text else colors.navInactive, fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}
