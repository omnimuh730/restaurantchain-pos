package com.mh.restaurantchainpos.pos.ui.layout.header

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
            Text("POS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f, fill = false)) {
            Text(
                "Restaurant Chain",
                color = colors.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
            )
            Text(
                "POINT OF SALE",
                color = colors.textMuted.copy(alpha = 0.8f),
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                maxLines = 1,
            )
        }
        PosHeaderButton(if (isDark) "Light" else "Dark", colors, onToggleDark)
        Spacer(Modifier.width(8.dp))
        PosHeaderButton("Lock", colors, onLock)
        Spacer(Modifier.width(8.dp))
        Box {
            PosHeaderButton(role.label, colors) { expanded = true }
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
fun PosHeaderButton(text: String, colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}
