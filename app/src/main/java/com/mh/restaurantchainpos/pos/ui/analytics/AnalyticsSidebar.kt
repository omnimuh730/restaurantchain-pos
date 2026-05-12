package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.i18n.stringTitle
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

/**
 * Sliding sidebar for the Analytics screen. On tablet/desktop it stays
 * docked on the left; on mobile it slides over the content from the left
 * with a backdrop, mirroring the React `AnalyticsSidebar` component.
 */
@Composable
fun AnalyticsSidebar(
    colors: PosColors,
    active: AnalyticsSection,
    onSelect: (AnalyticsSection) -> Unit,
    docked: Boolean,
    drawerOpen: Boolean,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (docked) {
        Box(modifier.width(220.dp).fillMaxHeight()) {
            SidebarContent(colors = colors, active = active, onSelect = onSelect)
        }
    } else {
        Box(modifier.fillMaxSize()) {
            AnimatedVisibility(visible = drawerOpen, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCloseDrawer,
                        ),
                )
            }
            AnimatedVisibility(
                visible = drawerOpen,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it },
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(260.dp)
                        .background(colors.surface),
                ) {
                    SidebarContent(colors = colors, active = active, onSelect = {
                        onSelect(it)
                        onCloseDrawer()
                    })
                }
            }
        }
    }
}

@Composable
private fun SidebarContent(
    colors: PosColors,
    active: AnalyticsSection,
    onSelect: (AnalyticsSection) -> Unit,
) {
    Column(
        Modifier
            .fillMaxHeight()
            .background(colors.surface)
            .border(1.dp, colors.border)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        AnalyticsSection.entries.forEach { section ->
            val isActive = section == active
            val title = section.stringTitle()
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) colors.accent else Color.Transparent)
                    .clickable { onSelect(section) }
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(30.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = title,
                        tint = if (isActive) colors.onAccent else colors.textMuted,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    title,
                    color = if (isActive) colors.onAccent else colors.textMuted,
                    fontSize = 15.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}
