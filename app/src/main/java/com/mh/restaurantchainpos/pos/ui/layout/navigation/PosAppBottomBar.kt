package com.mh.restaurantchainpos.pos.ui.layout.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.PosPage
import com.mh.restaurantchainpos.pos.ui.components.CountBadge
import com.mh.restaurantchainpos.pos.ui.i18n.stringTitle
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosDimens
import com.mh.restaurantchainpos.pos.ui.theme.PosSizes

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
            .background(colors.navBackground),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        pages.forEach { item ->
            val active = item == selected
            val badge = badges[item.badgeKey] ?: 0
            val label = item.stringTitle()
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (active) colors.navSelectedBackground else Color.Transparent)
                    .clickable { onSelect(item) },
            ) {
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 3.dp)
                        .width(PosSizes.NavIndicatorWidth)
                        .height(PosSizes.NavIndicatorHeight)
                        .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                        .background(if (active) colors.navSelectedIndicator else Color.Transparent),
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 6.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = posPageNavPainter(item),
                            contentDescription = label,
                            tint = if (active) colors.navSelectedForeground else colors.navInactive,
                            modifier = Modifier.size(if (active) 26.dp else 24.dp),
                        )
                        if (badge > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .clip(CircleShape)
                                    .background(colors.navBackground)
                                    .padding(2.dp),
                            ) {
                                CountBadge(count = badge, accentColor = colors.accent)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        label,
                        color = if (active) colors.navSelectedForeground else colors.navInactive,
                        fontSize = if (active) 12.sp else 11.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
