package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
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
    val isMobile = rememberIsMobile()
    // Even if more than one section is available, the menu button only appears
    // on narrow layouts — wide layouts have a docked sidebar so the button is
    // redundant (matches the Analytics screen's behaviour).
    val showMenuButton = sections.size > 1 && isMobile

    LaunchedEffect(role) {
        if (active !in sections) active = sections.first()
    }

    // Outer Box gives the mobile drawer overlay full screen-body height —
    // the same pattern Analytics uses so the drawer can cover both the
    // section header and the content area below it.
    Box(Modifier.fillMaxSize().background(colors.surfaceRaised)) {
        Row(Modifier.fillMaxSize()) {
            // Wide-mode docked sidebar. Only render when the user actually has
            // more than one section, otherwise it's wasted chrome.
            if (!isMobile && sections.size > 1) {
                SettingsSidebar(
                    colors = colors,
                    sections = sections,
                    active = active,
                    onSelect = { active = it },
                    docked = true,
                    drawerOpen = false,
                    onCloseDrawer = {},
                )
            }
            Column(Modifier.fillMaxSize()) {
                SettingsHeader(
                    colors = colors,
                    showMenuButton = showMenuButton,
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
        }

        // Mobile drawer overlays the whole screen body, just like Analytics.
        if (isMobile && sections.size > 1) {
            SettingsSidebar(
                colors = colors,
                sections = sections,
                active = active,
                onSelect = { active = it },
                docked = false,
                drawerOpen = drawerOpen,
                onCloseDrawer = { drawerOpen = false },
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}
