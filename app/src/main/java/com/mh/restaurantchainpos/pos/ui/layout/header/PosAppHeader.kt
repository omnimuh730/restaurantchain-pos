package com.mh.restaurantchainpos.pos.ui.layout.header

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.GppGood
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.roleNavAccess
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosDimens

@Composable
fun PosAppHeader(
    colors: PosColors,
    isDark: Boolean,
    role: ActiveRole,
    horizontalPadding: Dp,
    onToggleDark: () -> Unit,
    onRole: (ActiveRole) -> Unit,
    onLock: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier
            .fillMaxWidth()
            .height(PosDimens.HeaderHeight)
            .border(1.dp, colors.headerBorder.copy(alpha = 0.8f))
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(Blue600), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Outlined.GridView,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f, fill = false)) {
            Text(
                "Restaurant Chain",
                color = colors.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
            )
            Text(
                "POS SYSTEM",
                color = colors.textMuted.copy(alpha = 0.85f),
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
        PosHeaderIconChip(colors, onClick = onToggleDark) {
            Icon(
                imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                contentDescription = if (isDark) "Light mode" else "Dark mode",
                tint = colors.text,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        PosHeaderIconChip(colors, onClick = onLock) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Lock",
                tint = colors.text,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Box {
            PosHeaderIconChip(colors, onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Outlined.GppGood,
                    contentDescription = role.label,
                    tint = colors.text,
                    modifier = Modifier.size(22.dp),
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ActiveRole.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(option.label, fontWeight = if (option == role) FontWeight.Medium else FontWeight.Normal)
                                Text("${roleNavAccess.getValue(option).size} pages", fontSize = 10.sp, color = colors.textMuted)
                            }
                        },
                        onClick = {
                            onRole(option)
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Sign out", color = Color(0xFFEF4444)) },
                    onClick = {
                        expanded = false
                        onSignOut()
                    },
                )
            }
        }
    }
}

@Composable
private fun PosHeaderIconChip(colors: PosColors, onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = content,
    )
}
