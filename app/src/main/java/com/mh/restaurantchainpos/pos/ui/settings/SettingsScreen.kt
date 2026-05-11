package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
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
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

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
    var language by remember { mutableStateOf("EN") }

    LaunchedEffect(role) {
        if (active !in sections) active = sections.first()
    }

    Box(Modifier.fillMaxSize().background(colors.surfaceRaised)) {
        Column(Modifier.fillMaxSize()) {
            SettingsHeader(
                colors = colors,
                showMenuButton = sections.size > 1,
                isAdmin = isAdmin,
                language = language,
                onLanguageChange = { language = it },
                onMenuClick = { drawerOpen = true },
            )
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
            enter = fadeIn(),
            exit = fadeOut(),
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
                    .width(296.dp)
                    .background(colors.surface)
                    .border(1.dp, colors.border)
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Settings", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { drawerOpen = false },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.size(12.dp))
                sections.forEach { section ->
                    SettingsSidebarItem(
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
