package com.mh.restaurantchainpos.pos.ui.analytics

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.i18n.stringTitle
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.DarkPosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.posBackground

@Composable
fun AnalyticsScreen(colors: PosColors) {
    var section by remember { mutableStateOf(AnalyticsSection.SalesDashboard) }
    var period by remember { mutableStateOf(Period.Week) }
    var range by remember { mutableStateOf<DateRange?>(null) }
    var drawerOpen by remember { mutableStateOf(false) }

    val isDark = colors === DarkPosColors
    val isMobile = rememberIsMobile()
    val text1 = colors.text
    val border = colors.border
    val cardBg = colors.surface

    // Outer Box gives the mobile drawer overlay the full screen body height —
    // it spans the analytics section header AND the content below, rather than
    // only the area underneath the header that triggered it.
    Box(
        Modifier
            .fillMaxSize()
            .then(
                if (isDark) Modifier.background(Color(0xFF0F172A))
                else Modifier.background(posBackground(colors)),
            ),
    ) {
        Row(Modifier.fillMaxSize()) {
            if (!isMobile) {
                AnalyticsSidebar(
                    colors = colors,
                    active = section,
                    onSelect = { section = it },
                    docked = true,
                    drawerOpen = false,
                    onCloseDrawer = {},
                )
                Box(Modifier.width(1.dp).fillMaxHeight().background(border))
            }
            Column(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(cardBg)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (isMobile) {
                            Box(
                                Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { drawerOpen = true },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Menu,
                                    contentDescription = stringResource(R.string.analytics_menu_title),
                                    tint = text1,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            section.stringTitle(),
                            color = text1,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(border))
                }
                Box(Modifier.weight(1f)) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        when (section) {
                            AnalyticsSection.SalesDashboard -> SalesDashboardView(
                                period = period,
                                onPeriodChange = { period = it },
                                range = range,
                                onRangeChange = { range = it },
                                isDark = isDark,
                            )
                            AnalyticsSection.Menu -> MenuAnalysisView(
                                period = period,
                                onPeriodChange = { period = it },
                                range = range,
                                onRangeChange = { range = it },
                                isDark = isDark,
                            )
                            AnalyticsSection.Customer -> CustomerAnalysisView(
                                period = period,
                                onPeriodChange = { period = it },
                                range = range,
                                onRangeChange = { range = it },
                                isDark = isDark,
                            )
                            AnalyticsSection.History -> HistoryView(
                                period = period,
                                onPeriodChange = { period = it },
                                range = range,
                                onRangeChange = { range = it },
                                isDark = isDark,
                            )
                        }
                    }
                }
            }
        }
        // Mobile drawer overlays the whole screen body, not just the inner content area.
        if (isMobile) {
            AnalyticsSidebar(
                colors = colors,
                active = section,
                onSelect = { section = it },
                docked = false,
                drawerOpen = drawerOpen,
                onCloseDrawer = { drawerOpen = false },
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}
