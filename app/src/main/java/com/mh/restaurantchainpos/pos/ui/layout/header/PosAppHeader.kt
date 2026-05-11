package com.mh.restaurantchainpos.pos.ui.layout.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.roleNavAccess
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
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
            .background(colors.surface)
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Brand logo — blue rounded tile with apps icon
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Blue600),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Apps,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(10.dp))

        // Brand text
        Column(Modifier.weight(1f, fill = true)) {
            Text(
                "Restaurant Chain",
                color = colors.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            Text(
                "POINT OF SALE",
                color = colors.textMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(6.dp))

        // Compact icon buttons
        HeaderIconButton(colors = colors, icon = Icons.Outlined.Lock, onClick = onLock)
        Spacer(Modifier.width(6.dp))
        HeaderIconButton(
            colors = colors,
            icon = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
            onClick = onToggleDark,
        )
        Spacer(Modifier.width(6.dp))

        // Role button — blue-tinted with shield + chevron
        Box {
            Row(
                Modifier
                    .height(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Blue500.copy(alpha = 0.12f))
                    .clickable { expanded = true }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(
                    Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = Blue600,
                    modifier = Modifier.size(16.dp),
                )
                Icon(
                    Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Blue600,
                    modifier = Modifier.size(16.dp),
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ActiveRole.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    option.label,
                                    fontWeight = if (option == role) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (option == role) Blue600 else colors.text,
                                )
                                Text(
                                    "${roleNavAccess.getValue(option).size} pages",
                                    fontSize = 10.sp,
                                    color = colors.textMuted,
                                )
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
private fun HeaderIconButton(
    colors: PosColors,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = colors.text, modifier = Modifier.size(16.dp))
    }
}

// Retained for compatibility with any other callers that still use the old text-button helper.
@Composable
fun PosHeaderButton(text: String, colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}
