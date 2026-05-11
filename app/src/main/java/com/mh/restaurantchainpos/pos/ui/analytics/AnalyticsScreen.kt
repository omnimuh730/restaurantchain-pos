package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
fun AnalyticsScreen(colors: PosColors) {
    var section by remember { mutableStateOf(AnalyticsSection.Dashboard) }
    var period by remember { mutableStateOf(Period.Today) }
    var range by remember { mutableStateOf<DateRange?>(null) }
    var drawerOpen by remember { mutableStateOf(false) }

    val isDark = colors.text == Color(0xFFE5E7EB)
    val isMobile = rememberIsMobile()
    val text1 = colors.text
    val text2 = colors.textMuted
    val border = colors.border
    val cardBg = colors.surface

    val pageBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)

    Row(Modifier.fillMaxSize().background(pageBg)) {
        if (!isMobile) {
            AnalyticsSidebar(
                active = section,
                onSelect = { section = it },
                isDark = isDark,
                docked = true,
                drawerOpen = false,
                onCloseDrawer = {},
            )
        }
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(cardBg)
                    .border(1.dp, border)
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
                    ) { Text("≡", color = text1, fontSize = 22.sp, fontWeight = FontWeight.SemiBold) }
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    section.label,
                    color = text1,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
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
                        AnalyticsSection.Dashboard -> DashboardView(
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
                    Spacer(Modifier.size(80.dp))
                }
                if (isMobile) {
                    AnalyticsSidebar(
                        active = section,
                        onSelect = { section = it },
                        isDark = isDark,
                        docked = false,
                        drawerOpen = drawerOpen,
                        onCloseDrawer = { drawerOpen = false },
                    )
                }
            }
        }
    }
    @Suppress("UNUSED_EXPRESSION") text2
}
