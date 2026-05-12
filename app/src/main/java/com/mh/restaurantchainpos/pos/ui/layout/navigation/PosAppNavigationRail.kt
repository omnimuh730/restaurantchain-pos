package com.mh.restaurantchainpos.pos.ui.layout.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.PosPage
import com.mh.restaurantchainpos.pos.ui.components.CountBadge
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
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
                        .background(if (active) Blue600.copy(alpha = 0.10f) else Color.Transparent)
                        .clickable { onSelect(item) }
                        .padding(horizontal = 6.dp, vertical = 10.dp),
                ) {
                    Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = posPageNavPainter(item),
                            contentDescription = item.label,
<<<<<<< HEAD
                            tint = if (active) colors.navActive else colors.navInactive,
                            modifier = Modifier.size(22.dp),
=======
                            tint = if (active) Blue600 else colors.navInactive,
                            modifier = Modifier.size(if (active) 26.dp else 24.dp),
>>>>>>> 1cf3ad398a1be8959d53b6e63646849759eedb57
                        )
                        if (badge > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .clip(CircleShape)
                                    .background(colors.navBackground)
                                    .padding(2.dp),
                            ) {
                                CountBadge(count = badge, size = 16.dp)
                            }
                        }
                    }
                    Text(
                        item.label,
<<<<<<< HEAD
                        color = if (active) colors.navActive else colors.navInactive,
=======
                        color = if (active) Blue600 else colors.navInactive,
>>>>>>> 1cf3ad398a1be8959d53b6e63646849759eedb57
                        fontSize = 10.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
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
