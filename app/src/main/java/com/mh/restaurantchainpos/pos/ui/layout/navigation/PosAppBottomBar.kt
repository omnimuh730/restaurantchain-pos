package com.mh.restaurantchainpos.pos.ui.layout.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.mh.restaurantchainpos.pos.data.PosPage
import com.mh.restaurantchainpos.pos.ui.components.CountBadge
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
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
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (active) Blue600.copy(alpha = 0.08f) else Color.Transparent)
                    .clickable { onSelect(item) },
            ) {
<<<<<<< HEAD
                Box(Modifier.size(width = 34.dp, height = 26.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = posPageNavIcon(item),
                        contentDescription = item.label,
                        tint = if (active) colors.navActive else colors.navInactive,
                        modifier = Modifier.size(22.dp),
                    )
                    if (badge > 0) {
                        CountBadge(count = badge, modifier = Modifier.align(Alignment.TopEnd))
=======
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 3.dp)
                        .width(30.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                        .background(if (active) Blue600 else Color.Transparent),
                )
                // The (icon + label) block is sized to its own content and
                // centered as a unit inside the tab. Previously the icon lived
                // inside a 40dp square that dwarfed the 24-26dp glyph and
                // shifted the perceived visual centre upwards (visible as the
                // icons drifting toward the top of the bar).
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 6.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // Tight icon container — just slightly taller than the
                    // glyph so the floating badge has room to bleed out of the
                    // top-right without changing the visual centre.
                    Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = posPageNavPainter(item),
                            contentDescription = item.label,
                            tint = if (active) Blue600 else colors.navInactive,
                            modifier = Modifier.size(if (active) 26.dp else 24.dp),
                        )
                        if (badge > 0) {
                            // A small ring in the bar's own background colour visually
                            // detaches the badge from the icon glyph behind it.
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .clip(CircleShape)
                                    .background(colors.navBackground)
                                    .padding(2.dp),
                            ) {
                                CountBadge(count = badge)
                            }
                        }
>>>>>>> 1cf3ad398a1be8959d53b6e63646849759eedb57
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        item.label,
                        color = if (active) Blue600 else colors.navInactive,
                        fontSize = if (active) 12.sp else 11.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                    )
                }
<<<<<<< HEAD
                Text(item.label, color = if (active) colors.navActive else colors.navInactive, fontSize = 11.sp, maxLines = 1)
=======
>>>>>>> 1cf3ad398a1be8959d53b6e63646849759eedb57
            }
        }
    }
}
