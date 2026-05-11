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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.StaffMember
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun PermissionsModal(
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
internal fun PermissionRow(colors: PosColors, perm: PermItem, on: Boolean, onToggle: () -> Unit) {
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
internal fun QuickActionPill(colors: PosColors, label: String, onClick: () -> Unit) {
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
