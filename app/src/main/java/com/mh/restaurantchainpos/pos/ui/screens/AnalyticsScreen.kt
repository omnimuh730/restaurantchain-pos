package com.mh.restaurantchainpos.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.ui.components.Badge
import com.mh.restaurantchainpos.pos.ui.components.MetricCard
import com.mh.restaurantchainpos.pos.ui.components.PillButton
import com.mh.restaurantchainpos.pos.ui.components.PosCard
import com.mh.restaurantchainpos.pos.ui.components.SimpleBars
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.Green500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

@Composable
fun AnalyticsScreen(colors: PosColors) {
    var section by remember { mutableStateOf("dashboard") }
    var period by remember { mutableStateOf("week") }
    var domestic by remember { mutableStateOf(true) }
    Row(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth(0.26f).fillMaxSize().background(colors.surface).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Analytics", color = colors.text, fontWeight = FontWeight.Medium, fontSize = 18.sp)
            listOf("dashboard" to "Dashboard", "menu" to "Menu Analysis", "customer" to "Customer Analysis", "history" to "History").forEach { (id, label) ->
                PillButton(label, section == id, colors, Modifier.fillMaxWidth()) { section = id }
            }
            Spacer(Modifier.weight(1f))
            PillButton(if (domestic) "KRW" else "USD", true, colors, Modifier.fillMaxWidth()) { domestic = !domestic }
        }
        Column(Modifier.weight(1f).fillMaxSize().padding(16.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("today", "week", "month", "3month", "custom").forEach { PillButton(it, period == it, colors) { period = it } }
            }
            when (section) {
                "dashboard" -> Dashboard(colors, domestic)
                "menu" -> MenuAnalysis(colors, domestic)
                "customer" -> CustomerAnalysis(colors)
                else -> History(colors)
            }
        }
    }
}

@Composable
private fun Dashboard(colors: PosColors, domestic: Boolean) {
    val revenue = if (domestic) "₩28,960,000" else "$9,920"
    LazyColumn(Modifier.fillMaxSize().padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Revenue", revenue, "+24.6%", colors, Blue600, Modifier.weight(1f))
                MetricCard("Orders", "312", "+3.3%", colors, Green500, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Avg Ticket", if (domestic) "₩92,820" else "$53.34", "+20.6%", colors, Amber500, Modifier.weight(1f))
                MetricCard("Cancels", "11", "-18%", colors, Red500, Modifier.weight(1f))
            }
        }
        item {
            PosCard(colors, Modifier.fillMaxWidth()) {
                Text("Sales Trend", color = colors.text, fontWeight = FontWeight.Medium)
                SimpleBars(PosMockData.analytics.map { it.label to if (domestic) it.domestic / 100000 else it.foreign }, Blue600, colors, Modifier.padding(top = 12.dp))
            }
        }
        item {
            PosCard(colors, Modifier.fillMaxWidth()) {
                Text("Payment Split", color = colors.text, fontWeight = FontWeight.Medium)
                SimpleBars(listOf("Credit" to if (domestic) 41 else 78, "Cash" to if (domestic) 59 else 22), Green500, colors, Modifier.padding(top = 12.dp))
            }
        }
    }
}

@Composable
private fun MenuAnalysis(colors: PosColors, domestic: Boolean) {
    val items = if (domestic) {
        listOf("Bibimbap" to 212, "Soju" to 288, "Kimchi" to 196, "Ramen" to 176, "Gyoza" to 158)
    } else {
        listOf("Grilled Salmon" to 128, "Caesar Salad" to 158, "Truffle Fries" to 182, "Lychee Martini" to 94, "Tiramisu" to 74)
    }
    LazyColumn(Modifier.fillMaxSize().padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            PosCard(colors, Modifier.fillMaxWidth()) {
                Text("Top category loved by guests", color = colors.text, fontWeight = FontWeight.Medium, fontSize = 17.sp)
                Text(if (domestic) "Sake & Soju leads the KRW pool." else "Sides and seafood lead the USD pool.", color = colors.textMuted, fontSize = 12.sp)
            }
        }
        item { SimpleBars(items, Blue600, colors, Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(colors.surface).padding(16.dp)) }
        items(items) { (name, qty) ->
            PosCard(colors, Modifier.fillMaxWidth()) {
                Row {
                    Text(name, color = colors.text, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Badge("$qty sold", Green500)
                }
            }
        }
    }
}

@Composable
private fun CustomerAnalysis(colors: PosColors) {
    LazyColumn(Modifier.fillMaxSize().padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Customers", "1,284", "+8.3%", colors, Blue600, Modifier.weight(1f))
                MetricCard("New", "186", "+14.2%", colors, Green500, Modifier.weight(1f))
            }
        }
        item {
            PosCard(colors, Modifier.fillMaxWidth()) {
                Text("Visit Frequency", color = colors.text, fontWeight = FontWeight.Medium)
                SimpleBars(listOf("1x" to 486, "2-3x" to 384, "4-6x" to 228, "7-10x" to 124, "10x+" to 62), Blue600, colors, Modifier.padding(top = 12.dp))
            }
        }
        item {
            PosCard(colors, Modifier.fillMaxWidth()) {
                Text("Party Size", color = colors.text, fontWeight = FontWeight.Medium)
                SimpleBars(listOf("1" to 8, "2" to 32, "3-4" to 38, "5-6" to 15, "7+" to 7), Amber500, colors, Modifier.padding(top = 12.dp))
            }
        }
    }
}

@Composable
private fun History(colors: PosColors) {
    var tab by remember { mutableStateOf("all") }
    val tabs = listOf("all", "order", "reservation", "payment", "no-show", "walk-in")
    val rows = PosMockData.history.filter { tab == "all" || it.kind == tab }
    LazyColumn(Modifier.fillMaxSize().padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tabs.forEach { PillButton(it, tab == it, colors) { tab = it } }
            }
        }
        items(rows) { event ->
            PosCard(colors, Modifier.fillMaxWidth().clickable { }) {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text(event.id + " · " + event.guest, color = colors.text, fontWeight = FontWeight.Medium)
                        Text("${event.kind} · ${event.table} · ${event.note}", color = colors.textMuted, fontSize = 12.sp)
                    }
                    Badge(event.status, if (event.status == "no-show") Red500 else Green500)
                }
            }
        }
    }
}
