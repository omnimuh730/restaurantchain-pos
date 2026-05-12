package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.data.StaffMember
import com.mh.restaurantchainpos.pos.ui.components.PosNotificationHost
import com.mh.restaurantchainpos.pos.ui.components.rememberPosNotificationHostState
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Slate400

@Composable
fun StaffSettings(colors: PosColors) {
    val staff = remember { mutableStateListOf(*PosMockData.staff.toTypedArray()) }
    var search by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf("all") }
    var permissionsTarget by remember { mutableStateOf<StaffMember?>(null) }
    var confirmRequest by remember { mutableStateOf<ConfirmRequest?>(null) }
    var tempPinFor by remember { mutableStateOf<String?>(null) }
    var registerOpen by remember { mutableStateOf(false) }
    val notifications = rememberPosNotificationHostState()
    val resources = LocalResources.current

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

    Box(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    Text(stringResource(R.string.staff_mgmt_title), color = colors.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(stringResource(R.string.staff_mgmt_subtitle), color = colors.textMuted, fontSize = 12.sp)
                }
                PrimaryButton(
                    label = stringResource(R.string.staff_action_register),
                    onClick = { registerOpen = true },
                    leadingIcon = Icons.Outlined.Add,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StaffStatCard(colors, stringResource(R.string.staff_stat_total), staff.size, Icons.Outlined.Group, Blue500, Modifier.weight(1f))
                StaffStatCard(colors, stringResource(R.string.staff_stat_active), activeCount, Icons.Outlined.Check, Blue500, Modifier.weight(1f))
                StaffStatCard(colors, stringResource(R.string.staff_stat_inactive), inactiveCount, Icons.Outlined.Close, Slate400, Modifier.weight(1f))
                StaffStatCard(colors, stringResource(R.string.staff_stat_pending), pendingCount, Icons.Outlined.Schedule, Amber500, Modifier.weight(1f))
            }

            if (pending.isNotEmpty()) {
                PendingRequestsCard(
                    colors = colors,
                    pending = pending,
                    onApprove = { member ->
                        val idx = staff.indexOfFirst { it.id == member.id }
                        if (idx >= 0) {
                            staff[idx] = staff[idx].copy(status = "active")
                            val roleRes = roleTitleRes(member.role)
                            val roleLabel = if (roleRes != 0) resources.getString(roleRes) else member.role
                            notifications.success(
                                title = resources.getString(R.string.staff_toast_approved_title),
                                message = resources.getString(R.string.staff_toast_approved_msg, member.name, roleLabel),
                            )
                        }
                    },
                    onReject = { member -> staff.removeAll { it.id == member.id } },
                )
            }

            SettingTextField(
                colors = colors,
                value = search,
                onChange = { search = it },
                placeholder = stringResource(R.string.staff_search_ph),
                leadingIcon = Icons.Outlined.Search,
            )

            RoleFilters(
                colors = colors,
                roleCounts = Roles.associateWith { r -> nonPending.count { it.role == r } },
                allCount = nonPending.size,
                active = roleFilter,
                onChange = { roleFilter = it },
            )

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
                    Text(stringResource(R.string.staff_mgmt_no_match), color = colors.textMuted, fontSize = 13.sp)
                }
            }
        }

        PosNotificationHost(
            state = notifications,
            colors = colors,
            modifier = Modifier.align(Alignment.TopCenter),
        )
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

    if (registerOpen) {
        RegisterStaffDialog(
            colors = colors,
            onDismiss = { registerOpen = false },
            onRegister = { name, username, role ->
                val nextId = "n${System.currentTimeMillis()}"
                val defaults = RoleDefaults[role] ?: emptyMap()
                staff.add(
                    StaffMember(
                        id = nextId,
                        name = name,
                        username = username,
                        role = role,
                        status = "active",
                        joinDate = "May 2026",
                        permissionCount = defaults.values.count { it },
                        permissions = defaults,
                    ),
                )
                registerOpen = false
            },
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

    if (tempPinFor != null) {
        val pinId = tempPinFor
        LaunchedEffect(pinId) {
            kotlinx.coroutines.delay(5000)
            if (tempPinFor == pinId) tempPinFor = null
        }
    }
}
