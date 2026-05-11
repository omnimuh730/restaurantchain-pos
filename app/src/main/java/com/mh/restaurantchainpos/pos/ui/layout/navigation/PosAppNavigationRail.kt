package com.mh.restaurantchainpos.pos.ui.layout.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.PosPage
import com.mh.restaurantchainpos.pos.ui.components.CountBadge
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosDimens

@Composable
fun PosAppNavigationRail(
    colors: PosColors,
    pages: List<PosPage>,
    selected: PosPage,
    badges: Map<String, Int>,
    onSelect: (PosPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .width(PosDimens.NavigationRailWidth)
            .fillMaxHeight()
            .background(colors.navBackground)
            .border(1.dp, colors.headerBorder)
            .padding(vertical = PosDimens.SpaceMd),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            pages.forEach { item ->
                val active = item == selected
                val badge = badges[item.badgeKey] ?: 0
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(PosDimens.RadiusMd))
                        .clickable { onSelect(item) }
                        .padding(horizontal = 6.dp, vertical = 10.dp),
                ) {
                    Box(Modifier.size(width = 34.dp, height = 26.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = posPageNavIcon(item),
                            contentDescription = item.label,
                            tint = if (active) colors.text else colors.navInactive,
                            modifier = Modifier.size(22.dp),
                        )
                        if (badge > 0) {
                            CountBadge(count = badge, modifier = Modifier.align(Alignment.TopEnd), size = 16.dp)
                        }
                    }
                    Text(
                        item.label,
                        color = if (active) colors.text else colors.navInactive,
                        fontSize = 10.sp,
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        lineHeight = 11.sp,
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
