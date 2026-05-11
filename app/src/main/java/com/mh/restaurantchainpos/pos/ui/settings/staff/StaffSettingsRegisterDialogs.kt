package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

@Composable
internal fun ConfirmActionDialog(
    colors: PosColors,
    request: ConfirmRequest,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    ModalScrim(onDismiss = onCancel) {
        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 380.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .consumeModalTaps(),
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(request.kind.title, color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Staff: ${request.member.name} (@${request.member.username})",
                    color = colors.textMuted,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(request.kind.description, color = colors.textMuted, fontSize = 12.sp)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlineButton("Cancel", colors.text, onClick = onCancel)
                Spacer(Modifier.size(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(request.kind.btnColor)
                        .clickable(onClick = onConfirm)
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                ) {
                    Text(request.kind.btnLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
internal fun RegisterStaffDialog(
    colors: PosColors,
    onDismiss: () -> Unit,
    onRegister: (name: String, username: String, role: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Waiter") }
    val canSubmit = name.isNotBlank() && username.isNotBlank()
    val defaults = RoleDefaults[role] ?: emptyMap()
    val activePerms = AllPerms.filter { defaults[it.id] == true }

    ModalScrim(onDismiss = onDismiss) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .widthIn(max = 480.dp)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                .consumeModalTaps(),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = Blue500, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(10.dp))
                Text("Register New Staff", color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column {
                    RequiredLabel(colors, "Full Name")
                    Spacer(Modifier.height(6.dp))
                    SettingTextField(
                        colors = colors,
                        value = name,
                        onChange = { name = it },
                        placeholder = "e.g. John Smith",
                    )
                }
                Column {
                    RequiredLabel(colors, "Username")
                    Spacer(Modifier.height(6.dp))
                    SettingTextField(
                        colors = colors,
                        value = username,
                        onChange = { username = it.replace(' ', '.') },
                        placeholder = "e.g. john.smith",
                    )
                }
                Column {
                    RequiredLabel(colors, "Role")
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Roles.forEach { r ->
                            val cfg = RoleConfigs[r]
                            val rDefaults = RoleDefaults[r] ?: emptyMap()
                            val permCount = rDefaults.values.count { it }
                            RoleSelectCard(
                                colors = colors,
                                label = r,
                                icon = cfg?.icon,
                                permCount = permCount,
                                selected = role == r,
                                modifier = Modifier.weight(1f),
                            ) { role = r }
                        }
                    }
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceRaised)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    Text(
                        "DEFAULT PERMISSIONS FOR ${role.uppercase()}",
                        color = colors.textMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    if (activePerms.isEmpty()) {
                        Text("No default permissions", color = colors.textMuted, fontSize = 11.sp)
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            activePerms.forEach { p -> PermChip(colors, p) }
                        }
                    }
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlineButton("Cancel", colors.text, onClick = onDismiss)
                Spacer(Modifier.size(8.dp))
                PrimaryButton(
                    label = "Register",
                    onClick = { if (canSubmit) onRegister(name.trim(), username.trim(), role) },
                    enabled = canSubmit,
                    leadingIcon = Icons.Outlined.Check,
                )
            }
        }
    }
}

@Composable
internal fun RequiredLabel(colors: PosColors, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(" *", color = Red500, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun RoleSelectCard(
    colors: PosColors,
    label: String,
    icon: ImageVector?,
    permCount: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Blue600 else colors.border
    val background = if (selected) Blue500.copy(alpha = 0.08f) else colors.surface
    val iconTint = if (selected) Blue600 else colors.textMuted
    val labelColor = if (selected) Blue600 else colors.text

    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
        }
        Text(label, color = labelColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(2.dp))
        Text("$permCount perms", color = colors.textMuted, fontSize = 10.sp)
    }
}

@Composable
internal fun PermChip(colors: PosColors, perm: PermItem) {
    Row(
        Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(Blue500.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(perm.icon, contentDescription = null, tint = Blue600, modifier = Modifier.size(11.dp))
        Spacer(Modifier.size(4.dp))
        Text(perm.label, color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
