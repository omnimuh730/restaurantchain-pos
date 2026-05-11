package com.mh.restaurantchainpos.pos.ui.layout.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    .fillMaxHeight()
                    .background(if (active) colors.surfaceRaised else Color.Transparent)
                    .clickable { onSelect(item) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Square icon container with enough headroom that the badge floats
                // above the icon's top-right corner instead of being pressed against
                // the top edge of the bar.
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = posPageNavPainter(item),
                        contentDescription = item.label,
                        tint = if (active) colors.text else colors.navInactive,
                        modifier = Modifier.size(24.dp),
                    )
                    if (badge > 0) {
                        // A small ring in the bar's own background colour visually
                        // detaches the badge from the icon glyph behind it.
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .clip(CircleShape)
                                .background(colors.navBackground)
                                .padding(2.dp),
                        ) {
                            CountBadge(count = badge)
                        }
                    }
                }
                Text(item.label, color = if (active) colors.text else colors.navInactive, fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}
