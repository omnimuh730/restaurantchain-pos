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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

/**
 * Sliding sidebar for the Analytics screen. On tablet/desktop it stays
 * docked on the left; on mobile it slides over the content from the left
 * with a backdrop, mirroring the React `AnalyticsSidebar` component.
 */
@Composable
fun AnalyticsSidebar(
    active: AnalyticsSection,
    onSelect: (AnalyticsSection) -> Unit,
    isDark: Boolean,
    docked: Boolean,
    drawerOpen: Boolean,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (docked) {
        Box(modifier.width(220.dp).fillMaxHeight()) {
            SidebarContent(active = active, onSelect = onSelect, isDark = isDark)
        }
    } else {
        Box(Modifier.fillMaxSize()) {
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
                        .background(if (isDark) Color(0xFF141820) else Color.White),
                ) {
                    SidebarContent(active = active, onSelect = {
                        onSelect(it)
                        onCloseDrawer()
                    }, isDark = isDark)
                }
            }
        }
    }
}

@Composable
private fun SidebarContent(
    active: AnalyticsSection,
    onSelect: (AnalyticsSection) -> Unit,
    isDark: Boolean,
) {
    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val border = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)
    Column(
        Modifier
            .fillMaxHeight()
            .background(if (isDark) Color(0xFF141820) else Color.White)
            .border(1.dp, border)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("⚙", color = text2, fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Text("Analytics", color = text1, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        AnalyticsSection.entries.forEach { section ->
            val isActive = section == active
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isActive) Blue600 else Color.Transparent)
                    .clickable { onSelect(section) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(section.icon, color = if (isActive) Color.White else text2, fontSize = 14.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    sectionMenuLabel(section),
                    color = if (isActive) Color.White else text2,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private fun sectionMenuLabel(s: AnalyticsSection): String = when (s) {
    AnalyticsSection.SalesDashboard -> "Sales Dashboard"
    AnalyticsSection.Menu -> "Menu Analysis"
    AnalyticsSection.Customer -> "Customer Analysis"
    AnalyticsSection.History -> "History"
}
