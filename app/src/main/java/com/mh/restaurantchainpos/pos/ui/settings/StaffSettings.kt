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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.data.StaffMember
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.Orange500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import com.mh.restaurantchainpos.pos.ui.theme.Slate400

// ─── Role config ─────────────────────────────────────────
private data class RoleConfig(val icon: ImageVector)

private val Roles = listOf("Waiter", "Chef", "Cashier")
private val RoleConfigs: Map<String, RoleConfig> = mapOf(
    "Waiter" to RoleConfig(Icons.Outlined.HowToReg),
    "Chef" to RoleConfig(Icons.Outlined.SoupKitchen),
    "Cashier" to RoleConfig(Icons.Outlined.Wallet),
)

// ─── Permissions catalog ─────────────────────────────────
private data class PermItem(val id: String, val label: String, val desc: String, val icon: ImageVector)

private val PermGroups: List<Pair<String, List<PermItem>>> = listOf(
    "PAGE ACCESS" to listOf(
        PermItem("floor-plan", "Floor Plan", "View floor plan & table layout", Icons.Outlined.Store),
        PermItem("orders", "Orders", "Access orders page", Icons.Outlined.RestaurantMenu),
        PermItem("kitchen", "Kitchen", "Access kitchen page", Icons.Outlined.SoupKitchen),
    ),
    "ACTIONS" to listOf(
        PermItem("reservations", "Reservations", "Handle table reservations", Icons.Outlined.CalendarToday),
        PermItem("take-orders", "Take Orders", "Create & process orders", Icons.Outlined.RestaurantMenu),
        PermItem("process-payment", "Process Payment", "Handle payments & refunds", Icons.Outlined.CreditCard),
        PermItem("menu-management", "Menu Management", "Manage menu items & categories", Icons.Outlined.RestaurantMenu),
    ),
)
private val AllPerms: List<PermItem> = PermGroups.flatMap { it.second }
private val PermIcons: List<PermItem> = listOf(
    AllPerms[0], // floor-plan
    AllPerms[1], // orders
    AllPerms[2], // kitchen
    AllPerms[3], // reservations
    PermItem("settings-password", "Settings Password", "", Icons.Outlined.Shield),
)
private val RoleDefaults: Map<String, Map<String, Boolean>> = mapOf(
    "Waiter" to mapOf("orders" to true, "kitchen" to true, "take-orders" to true),
    "Chef" to mapOf("kitchen" to true),
    "Cashier" to mapOf(
        "floor-plan" to true,
        "reservations" to true,
        "orders" to true,
        "kitchen" to true,
        "take-orders" to true,
        "process-payment" to true,
        "menu-management" to true,
    ),
)
private const val TotalPermCount = 7

// ─── Confirm-action descriptor ──────────────────────────
private enum class ConfirmKind(
    val title: String,
    val description: String,
    val btnLabel: String,
    val btnColor: Color,
) {
    Deactivate(
        "Deactivate Staff",
        "This staff member will no longer be able to log in.",
        "Deactivate",
        Orange500,
    ),
    Activate(
        "Activate Staff",
        "This staff member will regain access to the POS.",
        "Activate",
        Blue600,
    ),
    Remove(
        "Remove Staff",
        "This permanently removes the staff member and their permissions.",
        "Remove",
        Red500,
    ),
    ResetPin(
        "Reset PIN",
        "This will generate a temporary PIN for the staff member.",
        "Reset PIN",
        Orange500,
    ),
}

private data class ConfirmRequest(val kind: ConfirmKind, val member: StaffMember)

@Composable
fun StaffSettings(colors: PosColors) {
    val staff = remember { mutableStateListOf(*PosMockData.staff.toTypedArray()) }
    var search by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf("all") }
    var permissionsTarget by remember { mutableStateOf<StaffMember?>(null) }
    var confirmRequest by remember { mutableStateOf<ConfirmRequest?>(null) }
    var tempPinFor by remember { mutableStateOf<String?>(null) }

    val pending = staff.filter { it.status == "pending" }
    val nonPending = staff.filter { it.status != "pending" }
    val filtered = nonPending
        .filter { roleFilter == "all" || it.role == roleFilter }
        .filter {
            search.isBlank() ||
                it.name.contains(search, true) ||
                it.username.contains(search, true)
        }
    val activeCount = staff.count { it.status == "active" }
    val inactiveCount = staff.count { it.status == "inactive" }
    val pendingCount = pending.size

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Header card
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Group, contentDescription = null, tint = Blue500, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Staff Management", color = colors.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text("Register staff and manage permissions", color = colors.textMuted, fontSize = 12.sp)
            }
            PrimaryButton(
                label = "Register Staff",
                onClick = {
                    // Stub: add a placeholder pending staff for demo purposes
                    val nextId = (staff.size + 10).toString()
                    val defaults = RoleDefaults["Waiter"] ?: emptyMap()
                    staff.add(
                        StaffMember(
                            id = nextId,
                            name = "New Staff",
                            username = "new.staff.$nextId",
                            role = "Waiter",
                            status = "active",
                            joinDate = "May 2026",
                            permissionCount = defaults.values.count { it },
                            permissions = defaults,
                        ),
                    )
                },
                leadingIcon = Icons.Outlined.Add,
            )
        }

        // Stats row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StaffStatCard(colors, "Total", staff.size, Icons.Outlined.Group, Blue500, Modifier.weight(1f))
            StaffStatCard(colors, "Active", activeCount, Icons.Outlined.Check, Blue500, Modifier.weight(1f))
            StaffStatCard(colors, "Inactive", inactiveCount, Icons.Outlined.Close, Slate400, Modifier.weight(1f))
            StaffStatCard(colors, "Pending", pendingCount, Icons.Outlined.Schedule, Amber500, Modifier.weight(1f))
        }

        // Pending Requests
        if (pending.isNotEmpty()) {
            PendingRequestsCard(
                colors = colors,
                pending = pending,
                onApprove = { member ->
                    val idx = staff.indexOfFirst { it.id == member.id }
                    if (idx >= 0) staff[idx] = staff[idx].copy(status = "active")
                },
                onReject = { member -> staff.removeAll { it.id == member.id } },
            )
        }

        // Search
        SettingTextField(
            colors = colors,
            value = search,
            onChange = { search = it },
            placeholder = "Search by name, username or card ID...",
            leadingIcon = Icons.Outlined.Search,
        )

        // Role filters
        RoleFilters(
            colors = colors,
            roleCounts = Roles.associateWith { r -> nonPending.count { it.role == r } },
            allCount = nonPending.size,
            active = roleFilter,
            onChange = { roleFilter = it },
        )

        // Staff cards
        filtered.forEach { member ->
            StaffCard(
                colors = colors,
                member = member,
                tempPin = if (tempPinFor == member.id) "123456" else null,
                onPermissions = { permissionsTarget = member },
                onToggleStatus = {
                    confirmRequest = ConfirmRequest(
                        if (member.status == "active") ConfirmKind.Deactivate else ConfirmKind.Activate,
                        member,
                    )
                },
                onResetPin = { confirmRequest = ConfirmRequest(ConfirmKind.ResetPin, member) },
                onRemove = { confirmRequest = ConfirmRequest(ConfirmKind.Remove, member) },
            )
        }
        if (filtered.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("No matching staff", color = colors.textMuted, fontSize = 13.sp)
            }
        }
    }

    permissionsTarget?.let { member ->
        PermissionsModal(
            colors = colors,
            member = member,
            onSave = { newPerms ->
                val idx = staff.indexOfFirst { it.id == member.id }
                if (idx >= 0) {
                    staff[idx] = staff[idx].copy(
                        permissions = newPerms,
                        permissionCount = newPerms.values.count { it },
                    )
                }
                permissionsTarget = null
            },
            onClose = { permissionsTarget = null },
        )
    }

    confirmRequest?.let { req ->
        ConfirmActionDialog(
            colors = colors,
            request = req,
            onCancel = { confirmRequest = null },
            onConfirm = {
                when (req.kind) {
                    ConfirmKind.Deactivate -> {
                        val idx = staff.indexOfFirst { it.id == req.member.id }
                        if (idx >= 0) staff[idx] = staff[idx].copy(status = "inactive")
                    }
                    ConfirmKind.Activate -> {
                        val idx = staff.indexOfFirst { it.id == req.member.id }
                        if (idx >= 0) staff[idx] = staff[idx].copy(status = "active")
                    }
                    ConfirmKind.Remove -> staff.removeAll { it.id == req.member.id }
                    ConfirmKind.ResetPin -> tempPinFor = req.member.id
                }
                confirmRequest = null
            },
        )
    }

    // Auto-hide the temp PIN after a moment.
    if (tempPinFor != null) {
        val pinId = tempPinFor
        LaunchedEffect(pinId) {
            kotlinx.coroutines.delay(5000)
            if (tempPinFor == pinId) tempPinFor = null
        }
    }
}

// ─── Stat card ───────────────────────────────────────────
@Composable
private fun StaffStatCard(
    colors: PosColors,
    label: String,
    value: Int,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
            Text(label, color = colors.textMuted, fontSize = 11.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text("$value", color = colors.text, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Pending Requests card ───────────────────────────────
@Composable
private fun PendingRequestsCard(
    colors: PosColors,
    pending: List<StaffMember>,
    onApprove: (StaffMember) -> Unit,
    onReject: (StaffMember) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp)),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.PersonAdd, contentDescription = null, tint = Blue500, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(8.dp))
            Text("Pending Requests", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(Blue600)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text("${pending.size}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        pending.forEachIndexed { idx, member ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar(colors, member.name, 36.dp)
                Spacer(Modifier.size(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(member.name, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("@${member.username}", color = colors.textMuted, fontSize = 11.sp)
                        Spacer(Modifier.size(6.dp))
                        RoleBadge(member.role)
                    }
                }
                Spacer(Modifier.size(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Blue600)
                        .clickable { onApprove(member) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("Approve", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.size(6.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Blue600, RoundedCornerShape(8.dp))
                        .clickable { onReject(member) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = Blue600, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("Reject", color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            if (idx < pending.lastIndex) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            }
        }
    }
}

// ─── Role filters ────────────────────────────────────────
@Composable
private fun RoleFilters(
    colors: PosColors,
    roleCounts: Map<String, Int>,
    allCount: Int,
    active: String,
    onChange: (String) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        RolePill(
            colors = colors,
            label = "All Roles ($allCount)",
            icon = null,
            selected = active == "all",
            onClick = { onChange("all") },
            modifier = Modifier.weight(1f, fill = false),
        )
        Roles.forEach { r ->
            RolePill(
                colors = colors,
                label = "$r (${roleCounts[r] ?: 0})",
                icon = RoleConfigs[r]?.icon,
                selected = active == r,
                onClick = { onChange(if (active == r) "all" else r) },
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

@Composable
private fun RolePill(
    colors: PosColors,
    label: String,
    icon: ImageVector?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (selected) Blue600 else Color.Transparent
    val borderColor = if (selected) Blue600 else colors.border
    val textColor = if (selected) Color.White else colors.text
    val iconTint = if (selected) Color.White else colors.textMuted
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(13.dp))
                Spacer(Modifier.size(5.dp))
            }
            Text(label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Staff card ──────────────────────────────────────────
@Composable
private fun StaffCard(
    colors: PosColors,
    member: StaffMember,
    tempPin: String?,
    onPermissions: () -> Unit,
    onToggleStatus: () -> Unit,
    onResetPin: () -> Unit,
    onRemove: () -> Unit,
) {
    val isActive = member.status == "active"
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp)),
    ) {
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
                    RoleBadge(member.role)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Joined ${member.joinDate}", color = colors.textMuted, fontSize = 11.sp)
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
                    Text("Temporary PIN:", color = Blue500, fontSize = 11.sp)
                    Spacer(Modifier.size(8.dp))
                    Text(tempPin, color = Blue500, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${member.permissionCount}/$TotalPermCount permissions",
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
        // Action row
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
                Text("Permissions", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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

@Composable
private fun ActionIconButton(icon: ImageVector, tint: Color, onClick: () -> Unit) {
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
private fun StatusChip(status: String) {
    val (bg, fg, label) = when (status) {
        "active" -> Triple(Blue600, Color.White, "Active")
        "inactive" -> Triple(Slate400, Color.White, "Inactive")
        else -> Triple(Amber500, Color.White, "Pending")
    }
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
private fun RoleBadge(role: String) {
    val icon = RoleConfigs[role]?.icon
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
        Text(role, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Avatar(colors: PosColors, name: String, size: androidx.compose.ui.unit.Dp) {
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

// ─── Permissions modal ───────────────────────────────────
@Composable
private fun PermissionsModal(
    colors: PosColors,
    member: StaffMember,
    onSave: (Map<String, Boolean>) -> Unit,
    onClose: () -> Unit,
) {
    val perms = remember(member.id) {
        mutableStateListOf<Pair<String, Boolean>>().apply {
            AllPerms.forEach { add(it.id to (member.permissions[it.id] == true)) }
        }
    }
    val mapState: Map<String, Boolean> = perms.toMap()
    val enabledCount = perms.count { it.second }

    fun setPerm(id: String, value: Boolean) {
        val idx = perms.indexOfFirst { it.first == id }
        if (idx >= 0) perms[idx] = id to value
    }

    fun resetToRoleDefaults() {
        val defaults = RoleDefaults[member.role] ?: emptyMap()
        AllPerms.forEach { p -> setPerm(p.id, defaults[p.id] == true) }
    }

    fun selectAll() {
        AllPerms.forEach { p -> setPerm(p.id, true) }
    }

    fun clearAll() {
        AllPerms.forEach { p -> setPerm(p.id, false) }
    }

    ModalScrim(onDismiss = onClose) {
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
            // Header
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar(colors, member.name, 40.dp)
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(member.name, color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("$enabledCount of $TotalPermCount permissions", color = colors.textMuted, fontSize = 12.sp)
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))

            // Scrollable body
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickActionPill(colors, "Reset to ${member.role} defaults") { resetToRoleDefaults() }
                    QuickActionPill(colors, "Select All") { selectAll() }
                    QuickActionPill(colors, "Clear All") { clearAll() }
                }
                Spacer(Modifier.height(4.dp))

                PermGroups.forEach { (groupName, group) ->
                    val groupEnabled = group.count { mapState[it.id] == true }
                    Row(
                        Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.Settings, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.size(6.dp))
                        Text(
                            groupName,
                            color = colors.textMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                        Text("$groupEnabled/${group.size}", color = colors.textMuted, fontSize = 11.sp)
                    }
                    group.forEach { perm ->
                        val on = mapState[perm.id] == true
                        PermissionRow(colors, perm, on) { setPerm(perm.id, !on) }
                    }
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            // Footer
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlineButton("Cancel", colors.text, onClick = onClose)
                Spacer(Modifier.size(8.dp))
                PrimaryButton(
                    label = "Save Permissions",
                    onClick = { onSave(mapState) },
                    leadingIcon = Icons.Outlined.Check,
                )
            }
        }
    }
}

@Composable
private fun PermissionRow(colors: PosColors, perm: PermItem, on: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (on) Blue500.copy(alpha = 0.10f) else Color.Transparent)
            .clickable(onClick = onToggle)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(if (on) Blue500.copy(alpha = 0.18f) else colors.surfaceRaised),
            contentAlignment = Alignment.Center,
        ) {
            Icon(perm.icon, contentDescription = null, tint = if (on) Blue600 else colors.textMuted, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(perm.label, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            if (perm.desc.isNotEmpty()) {
                Text(perm.desc, color = colors.textMuted, fontSize = 11.sp)
            }
        }
        ToggleSwitch(checked = on, onChange = onToggle)
    }
}

@Composable
private fun QuickActionPill(colors: PosColors, label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
    ) {
        Text(label, color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// ─── Confirm action dialog ───────────────────────────────
@Composable
private fun ConfirmActionDialog(
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
