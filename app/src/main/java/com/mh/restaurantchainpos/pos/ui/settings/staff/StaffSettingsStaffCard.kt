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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.StaffMember
import com.mh.restaurantchainpos.pos.ui.components.PosElevatedSurface
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import com.mh.restaurantchainpos.pos.ui.theme.Slate400

@Composable
internal fun StaffCard(
    colors: PosColors,
    member: StaffMember,
    tempPin: String?,
    onPermissions: () -> Unit,
    onToggleStatus: () -> Unit,
    onResetPin: () -> Unit,
    onRemove: () -> Unit,
) {
    val isActive = member.status == "active"
    PosElevatedSurface(colors, Modifier.fillMaxWidth(), RoundedCornerShape(12.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Avatar(colors, member.name, 44.dp)
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isActive) Blue500 else Slate400)
                            .border(2.dp, colors.surface, CircleShape),
                    )
                }
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(member.name, color = colors.text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("@${member.username}", color = colors.textMuted, fontSize = 11.sp)
                    Spacer(Modifier.height(4.dp))
                    RoleBadge(role = member.role)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.staff_card_joined, member.joinDate), color = colors.textMuted, fontSize = 11.sp)
                Spacer(Modifier.size(8.dp))
                StatusChip(member.status)
            }
            if (tempPin != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Blue500.copy(alpha = 0.12f))
                        .border(1.dp, Blue500.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.staff_card_temp_pin), color = Blue500, fontSize = 11.sp)
                    Spacer(Modifier.size(8.dp))
                    Text(tempPin, color = Blue500, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.staff_card_perm_line, member.permissionCount, TotalPermCount),
                    color = colors.textMuted,
                    fontSize = 11.sp,
                )
                Spacer(Modifier.size(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    PermIcons.forEach { p ->
                        val enabled = member.permissions[p.id] == true
                        Box(
                            Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(
                                    if (enabled) Blue500.copy(alpha = 0.18f) else colors.surfaceRaised,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                p.icon,
                                contentDescription = null,
                                tint = if (enabled) Blue600 else colors.textMuted,
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        Row(
            Modifier.fillMaxWidth().height(44.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable(onClick = onPermissions),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Outlined.Shield, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(15.dp))
                Spacer(Modifier.size(6.dp))
                Text(stringResource(R.string.staff_card_permissions), color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Box(Modifier.width(1.dp).height(44.dp).background(colors.border))
            ActionIconButton(
                icon = if (isActive) Icons.Outlined.Block else Icons.Outlined.CheckCircle,
                tint = if (isActive) colors.textMuted else Blue500,
                onClick = onToggleStatus,
            )
            Box(Modifier.width(1.dp).height(44.dp).background(colors.border))
            ActionIconButton(
                icon = Icons.Outlined.VpnKey,
                tint = colors.textMuted,
                onClick = onResetPin,
            )
            Box(Modifier.width(1.dp).height(44.dp).background(colors.border))
            ActionIconButton(
                icon = Icons.Outlined.Delete,
                tint = Red500,
                onClick = onRemove,
            )
        }
        }
    }
}

@Composable
internal fun ActionIconButton(icon: ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .width(46.dp)
            .height(44.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    }
}

@Composable
internal fun StatusChip(status: String) {
    val (bg, fg, labelRes) = when (status) {
        "active" -> Triple(Blue600, Color.White, R.string.staff_status_active)
        "inactive" -> Triple(Slate400, Color.White, R.string.staff_status_inactive)
        else -> Triple(Amber500, Color.White, R.string.staff_status_pending)
    }
    val label = stringResource(labelRes)
    Box(
        Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(label, color = fg, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun RoleBadge(role: String) {
    val icon = RoleConfigs[role]?.icon
    val roleRes = roleTitleRes(role)
    val roleLabel = if (roleRes != 0) stringResource(roleRes) else role
    Row(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Blue600)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
            Spacer(Modifier.size(4.dp))
        }
        Text(roleLabel, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun Avatar(colors: PosColors, name: String, size: androidx.compose.ui.unit.Dp) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .joinToString("")
        .take(2)
        .uppercase()
    val fontSize = if (size >= 40.dp) 13.sp else 11.sp
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(Blue500.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials.ifBlank { "—" }, color = Blue600, fontSize = fontSize, fontWeight = FontWeight.SemiBold)
    }
}
