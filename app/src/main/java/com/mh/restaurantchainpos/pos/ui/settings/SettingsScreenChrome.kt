package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

enum class SettingsSection(
    val id: String,
    val label: String,
    val description: String,
    val icon: ImageVector,
) {
    General("general", "General", "Restaurant info, hours, contact", Icons.Outlined.Store),
    Menu("menu", "Menu Management", "Categories, sub-categories, items", Icons.Outlined.Restaurant),
    Amenities("amenities", "Amenities", "Cuisine, occasion, seating", Icons.Outlined.Tune),
    Security("security", "Security & Payments", "Password, saved cards", Icons.Outlined.Shield),
    Staff("staff", "Staff", "Team & permissions", Icons.Outlined.Group),
    Upgrade("upgrade", "Upgrade", "Plans & billing", Icons.Outlined.AutoAwesome),
}

@Composable
internal fun SettingsHeader(
    colors: PosColors,
    showMenuButton: Boolean,
    isAdmin: Boolean,
    language: String,
    onLanguageChange: (String) -> Unit,
    onMenuClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(width = 1.dp, color = colors.border)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showMenuButton) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onMenuClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Menu, contentDescription = null, tint = colors.text, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.size(8.dp))
        }
        Text("Settings", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        if (!isAdmin) {
            Spacer(Modifier.size(8.dp))
            Row(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Amber500.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = Amber500, modifier = Modifier.size(11.dp))
                Spacer(Modifier.size(4.dp))
                Text("Password only", color = Amber500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.weight(1f))
        LanguageToggle(colors = colors, language = language, onChange = onLanguageChange)
    }
}

@Composable
private fun LanguageToggle(colors: PosColors, language: String, onChange: (String) -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, colors.border, RoundedCornerShape(8.dp)),
    ) {
        LanguageOption(colors, "KO", language == "KO") { onChange("KO") }
        Box(Modifier.size(width = 1.dp, height = 28.dp).background(colors.border))
        LanguageOption(colors, "EN", language == "EN") { onChange("EN") }
    }
}

@Composable
private fun LanguageOption(colors: PosColors, label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clickable(onClick = onClick)
            .background(if (active) Blue600 else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (active) Color.White else colors.textMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun SettingsSidebarItem(colors: PosColors, section: SettingsSection, active: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) Blue500.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            section.icon,
            contentDescription = null,
            tint = if (active) Blue600 else colors.textMuted,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(section.label, color = if (active) Blue600 else colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(section.description, color = colors.textMuted, fontSize = 10.sp)
        }
        Icon(
            Icons.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(16.dp),
        )
    }
}
