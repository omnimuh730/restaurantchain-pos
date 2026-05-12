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
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownChip
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownMenuRow
import com.mh.restaurantchainpos.pos.ui.i18n.stringTitle
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosDimens
import com.mh.restaurantchainpos.pos.ui.theme.PosSizes

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
    val rolesPlusSignOutCount = ActiveRole.entries.size + 1
    Row(
        modifier
            .fillMaxWidth()
            .height(PosDimens.HeaderHeight)
            .background(colors.surface)
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(PosSizes.HeaderLogo)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.accent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Apps,
                contentDescription = stringResource(R.string.auth_cd_apps_logo),
                tint = colors.onAccent,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(10.dp))

        Column(Modifier.weight(1f, fill = true)) {
            Text(
                stringResource(R.string.brand_title),
                color = colors.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                stringResource(R.string.brand_subtitle),
                color = colors.textMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(6.dp))

        HeaderIconButton(
            colors = colors,
            icon = Icons.Outlined.Lock,
            contentDescription = stringResource(R.string.header_lock),
            onClick = onLock,
        )
        Spacer(Modifier.width(6.dp))
        HeaderIconButton(
            colors = colors,
            icon = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
            contentDescription = if (isDark) {
                stringResource(R.string.header_toggle_light)
            } else {
                stringResource(R.string.header_toggle_dark)
            },
            onClick = onToggleDark,
        )
        Spacer(Modifier.width(6.dp))

        PosDropdownChip(
            text = "",
            expanded = expanded,
            colors = colors,
            onExpandedChange = { expanded = it },
            leadingIcon = Icons.Outlined.Shield,
            leadingIconTint = colors.accent,
            chevronTint = colors.accent,
            labelColor = colors.accent,
        ) {
            val total = rolesPlusSignOutCount
            ActiveRole.entries.forEachIndexed { index, option ->
                PosDropdownMenuRow(
                    index = index,
                    totalCount = total,
                    text = option.stringTitle(),
                    selected = option == role,
                    colors = colors,
                    onClick = {
                        onRole(option)
                        expanded = false
                    },
                )
            }
            SignOutMenuRow(
                index = total - 1,
                totalCount = total,
                colors = colors,
                onClick = {
                    expanded = false
                    onSignOut()
                },
            )
        }
    }
}

@Composable
private fun SignOutMenuRow(
    index: Int,
    totalCount: Int,
    colors: PosColors,
    onClick: () -> Unit,
) {
    val danger = Color(0xFFEF4444)
    val shape = com.mh.restaurantchainpos.pos.ui.components.posMenuRowShape(index, totalCount)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.Logout,
            contentDescription = stringResource(R.string.header_sign_out),
            tint = danger,
            modifier = Modifier.size(16.dp),
        )
        Text(stringResource(R.string.header_sign_out), color = danger, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HeaderIconButton(
    colors: PosColors,
    icon: ImageVector,
    contentDescription: String,
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
        Icon(icon, contentDescription = contentDescription, tint = colors.text, modifier = Modifier.size(16.dp))
    }
}

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
