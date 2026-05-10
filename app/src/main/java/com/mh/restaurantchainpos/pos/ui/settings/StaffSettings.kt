package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.data.StaffMember
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Green500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import com.mh.restaurantchainpos.pos.ui.theme.Slate400

@Composable
fun StaffSettings(colors: PosColors) {
    val staff = remember { mutableStateListOf(*PosMockData.staff.toTypedArray()) }
    var openMember by remember { mutableStateOf<StaffMember?>(null) }
    var permissionMember by remember { mutableStateOf<StaffMember?>(null) }

    SettingCard(
        colors = colors,
        title = "Staff",
        subtitle = "Manage who can access the POS.",
        badge = "${staff.size}",
        badgeIcon = "👥",
    ) {
        PrimaryButton("+ Invite staff", { staff.add(StaffMember("New Member", "newmember", "Waiter", "pending", "May 2026", 4)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        staff.forEach { member ->
            StaffRow(
                colors = colors,
                member = member,
                onApprove = {
                    val idx = staff.indexOf(member)
                    if (idx >= 0) staff[idx] = member.copy(status = "active")
                },
                onDeactivate = {
                    val idx = staff.indexOf(member)
                    if (idx >= 0) staff[idx] = member.copy(status = if (member.status == "active") "inactive" else "active")
                },
                onRemove = { staff.remove(member) },
                onPermissions = { permissionMember = member },
                onEdit = { openMember = member },
            )
            Spacer(Modifier.height(8.dp))
        }
    }

    permissionMember?.let { member ->
        PermissionsModal(colors, member) { permissionMember = null }
    }
    openMember?.let { member ->
        EditStaffModal(colors, member, onSave = {
            val idx = staff.indexOf(member)
            if (idx >= 0) staff[idx] = it
            openMember = null
        }, onClose = { openMember = null })
    }
}

@Composable
private fun StaffRow(
    colors: PosColors,
    member: StaffMember,
    onApprove: () -> Unit,
    onDeactivate: () -> Unit,
    onRemove: () -> Unit,
    onPermissions: () -> Unit,
    onEdit: () -> Unit,
) {
    val initials = member.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase()
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(roleColor(member.role)),
                contentAlignment = Alignment.Center,
            ) {
                Text(initials.ifBlank { "—" }, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(member.name, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    SmallChip(member.role.uppercase(), roleColor(member.role))
                    SmallChip(
                        member.status.uppercase(),
                        when (member.status) {
                            "active" -> Green500
                            "inactive" -> Slate400
                            else -> Amber500
                        },
                    )
                }
                Text("@${member.username} · joined ${member.joinDate} · ${member.permissionCount} permissions", color = colors.textMuted, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (member.status == "pending") OutlineButton("Approve", Green500, onClick = onApprove, modifier = Modifier.weight(1f))
            else OutlineButton(if (member.status == "active") "Deactivate" else "Reactivate", Amber500, onClick = onDeactivate, modifier = Modifier.weight(1f))
            OutlineButton("Permissions", Blue500, onClick = onPermissions, modifier = Modifier.weight(1f))
            OutlineButton("Edit", colors.text, onClick = onEdit, modifier = Modifier.weight(1f))
            OutlineButton("Remove", Red500, onClick = onRemove, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PermissionsModal(colors: PosColors, member: StaffMember, onClose: () -> Unit) {
    val granted = remember { mutableStateListOf("orders", "kitchen", "settings-password") }
    val groups = listOf(
        "PAGE ACCESS" to listOf(
            Triple("floor-plan", "Floor Plan", "View floor plan & table layout"),
            Triple("orders", "Orders", "Access orders page"),
            Triple("kitchen", "Kitchen", "Access kitchen page"),
        ),
        "ACTIONS" to listOf(
            Triple("reservations", "Reservations", "Handle table reservations"),
            Triple("take-orders", "Take Orders", "Create & process orders"),
            Triple("process-payment", "Process Payment", "Handle payments & refunds"),
            Triple("menu-management", "Menu Management", "Manage menu items & categories"),
        ),
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .widthIn(max = 360.dp)
                .clickable(enabled = false) {}
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("Permissions for ${member.name}", color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Tap to toggle.", color = colors.textMuted, fontSize = 12.sp)
                }
                Text("✕", color = colors.textMuted, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onClose))
            }
            Spacer(Modifier.height(12.dp))
            groups.forEach { (group, items) ->
                Text(group, color = colors.textMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                items.forEach { (id, label, desc) ->
                    val active = granted.contains(id)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) Blue500.copy(alpha = 0.12f) else colors.surfaceRaised)
                            .clickable {
                                if (active) granted.remove(id) else granted.add(id)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (active) Blue500 else Color.Transparent)
                                .border(2.dp, if (active) Blue500 else colors.border, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (active) Text("✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.size(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(label, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(desc, color = colors.textMuted, fontSize = 10.sp)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton("Done", onClick = onClose, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun EditStaffModal(colors: PosColors, member: StaffMember, onSave: (StaffMember) -> Unit, onClose: () -> Unit) {
    var name by remember { mutableStateOf(member.name) }
    var username by remember { mutableStateOf(member.username) }
    var role by remember { mutableStateOf(member.role) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .widthIn(max = 320.dp)
                .clickable(enabled = false) {}
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text("Edit staff", color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text("✕", color = colors.textMuted, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onClose))
            }
            Spacer(Modifier.height(12.dp))
            SettingLabel(colors, "Full name")
            SettingTextField(colors, name, { name = it })
            Spacer(Modifier.height(8.dp))
            SettingLabel(colors, "Username")
            SettingTextField(colors, username, { username = it })
            Spacer(Modifier.height(8.dp))
            SettingLabel(colors, "Role")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Waiter", "Chef", "Cashier").forEach { r ->
                    val active = role == r
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (active) Blue500 else colors.surfaceRaised)
                            .clickable { role = r }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(r, color = if (active) Color.White else colors.text, fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlineButton("Cancel", colors.text, onClick = onClose, modifier = Modifier.weight(1f))
                PrimaryButton("Save", onClick = { onSave(member.copy(name = name, username = username, role = role)) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

private fun roleColor(role: String): Color = when (role) {
    "Chef" -> Color(0xFF0EA5E9)
    "Waiter" -> Blue500
    "Cashier" -> Color(0xFF6366F1)
    else -> Slate400
}
