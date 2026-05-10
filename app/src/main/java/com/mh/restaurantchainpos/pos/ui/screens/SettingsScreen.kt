package com.mh.restaurantchainpos.pos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.ui.settings.AmenitiesSettings
import com.mh.restaurantchainpos.pos.ui.settings.GeneralSettings
import com.mh.restaurantchainpos.pos.ui.settings.MenuManagement
import com.mh.restaurantchainpos.pos.ui.settings.SecurityPaymentsSettings
import com.mh.restaurantchainpos.pos.ui.settings.StaffSettings
import com.mh.restaurantchainpos.pos.ui.settings.UpgradePlans
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

private enum class SettingsSection(val id: String, val label: String, val description: String, val icon: String) {
    General("general", "General", "Restaurant info, hours, contact", "🏬"),
    Menu("menu", "Menu Management", "Categories, sub-categories, items", "🍽"),
    Amenities("amenities", "Amenities", "Cuisine, occasion, seating", "✨"),
    Security("security", "Security & Payments", "Password, saved cards", "🔒"),
    Staff("staff", "Staff", "Team & permissions", "👥"),
    Upgrade("upgrade", "Upgrade", "Plans & billing", "⭐"),
}

@Composable
fun SettingsScreen(colors: PosColors, role: ActiveRole) {
    val isAdmin = role == ActiveRole.Admin
    val canManageMenu = isAdmin || role == ActiveRole.Cashier
    val sections = when {
        isAdmin -> SettingsSection.entries.toList()
        canManageMenu -> listOf(SettingsSection.Menu, SettingsSection.Security)
        else -> listOf(SettingsSection.Security)
    }
    var active by remember { mutableStateOf(sections.first()) }
    var drawerOpen by remember { mutableStateOf(false) }
    LaunchedEffect(role) {
        if (active !in sections) active = sections.first()
    }

    Box(Modifier.fillMaxSize().background(colors.surfaceRaised)) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .border(1.dp, colors.border)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (sections.size > 1) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceRaised)
                            .clickable { drawerOpen = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    ) {
                        Text("☰", color = colors.text, fontSize = 14.sp)
                    }
                    Spacer(Modifier.size(12.dp))
                }
                Text("Settings", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                if (!isAdmin) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Amber500.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text("🔒 Password only", color = Amber500, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                Box(Modifier.padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
                    when (active) {
                        SettingsSection.General -> GeneralSettings(colors)
                        SettingsSection.Menu -> MenuManagement(colors)
                        SettingsSection.Amenities -> AmenitiesSettings(colors)
                        SettingsSection.Security -> SecurityPaymentsSettings(colors, passwordOnly = !isAdmin && !canManageMenu)
                        SettingsSection.Staff -> StaffSettings(colors)
                        SettingsSection.Upgrade -> UpgradePlans(colors)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = drawerOpen,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut(),
        ) {
            Box(Modifier.fillMaxSize().background(Color(0x80000000)).clickable { drawerOpen = false })
        }
        AnimatedVisibility(
            visible = drawerOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .width(288.dp)
                    .background(colors.surface)
                    .border(1.dp, colors.border)
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Settings", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Text("✕", color = colors.textMuted, fontSize = 14.sp, modifier = Modifier.clickable { drawerOpen = false })
                }
                Spacer(Modifier.size(12.dp))
                sections.forEach { section ->
                    SidebarItem(
                        colors = colors,
                        section = section,
                        active = section == active,
                        onClick = { active = section; drawerOpen = false },
                    )
                    Spacer(Modifier.size(4.dp))
                }
            }
        }
    }
}

@Composable
private fun SidebarItem(colors: PosColors, section: SettingsSection, active: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) Blue500.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(section.icon, fontSize = 16.sp)
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(section.label, color = if (active) Blue500 else colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(section.description, color = colors.textMuted, fontSize = 10.sp)
        }
        Text("›", color = colors.textMuted, fontSize = 14.sp)
    }
}
