package com.mh.restaurantchainpos.pos.ui.floor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.Floor
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownMenu
import com.mh.restaurantchainpos.pos.ui.components.PosDropdownMenuRow
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.FloorPalette

@Composable
fun FloorTabsRow(
    colors: PosColors,
    palette: FloorPalette,
    role: ActiveRole,
    floors: List<Floor>,
    activeFloorId: String,
    onSelectFloor: (String) -> Unit,
    onRenameFloor: (String, String) -> Unit,
    onAddFloor: (String) -> Unit,
    onRemoveFloor: (String) -> Unit,
    onEditLayout: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    var addOpen by remember { mutableStateOf(false) }
    var removeOpen by remember { mutableStateOf(false) }
    var renamingId by remember { mutableStateOf<String?>(null) }
    var renameVal by remember { mutableStateOf("") }
    val scroll = rememberScrollState()
    val isAdmin = role == ActiveRole.Admin

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(palette.card)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(scroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                floors.forEach { f ->
                    val isActive = f.id == activeFloorId
                    if (renamingId == f.id) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(palette.raised)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            BasicTextField(
                                value = renameVal,
                                onValueChange = { renameVal = it },
                                singleLine = true,
                                textStyle = TextStyle(color = palette.text1, fontSize = 13.sp),
                                cursorBrush = SolidColor(Blue500),
                                modifier = Modifier.width(80.dp),
                            )
                        }
                    } else {
                        Column(
                            Modifier
                                .clickable { onSelectFloor(f.id) }
                                .padding(horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    f.name,
                                    color = if (isActive) Blue600 else palette.text2,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                )
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(
                                            if (isActive) Blue600.copy(alpha = 0.16f) else palette.raised,
                                        )
                                        .padding(horizontal = 7.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        f.tables.size.toString(),
                                        color = if (isActive) Blue600 else palette.text3,
                                        fontSize = 11.sp,
                                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier
                                    .height(4.dp)
                                    .fillMaxWidth()
                                    .background(if (isActive) Blue600 else Color.Transparent),
                            )
                        }
                    }
                }
            }

            if (isAdmin) {
                Box(
                    Modifier
                        .height(52.dp)
                        .width(40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { menuOpen = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = stringResource(R.string.floor_menu_cd_actions),
                            tint = if (menuOpen) Blue500 else palette.text2,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    PosDropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        colors = colors,
                        menuWidth = 200.dp,
                    ) {
                        PosDropdownMenuRow(
                            index = 0,
                            totalCount = 3,
                            text = stringResource(R.string.floor_menu_add_floor),
                            selected = false,
                            colors = colors,
                            leadingIcon = Icons.Outlined.Add,
                            onClick = {
                                menuOpen = false
                                addOpen = true
                            },
                        )
                        PosDropdownMenuRow(
                            index = 1,
                            totalCount = 3,
                            text = stringResource(R.string.floor_menu_edit_layout),
                            selected = false,
                            colors = colors,
                            leadingIcon = Icons.Outlined.Edit,
                            onClick = {
                                menuOpen = false
                                onEditLayout()
                            },
                        )
                        PosDropdownMenuRow(
                            index = 2,
                            totalCount = 3,
                            text = stringResource(R.string.floor_menu_remove_floor),
                            selected = false,
                            colors = colors,
                            leadingIcon = Icons.Outlined.Delete,
                            danger = true,
                            onClick = {
                                menuOpen = false
                                if (floors.size > 1) removeOpen = true
                            },
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
    }

    if (addOpen) {
        ModalShell(onClose = { addOpen = false }) {
            FloorRenameDialog(
                palette = palette,
                title = stringResource(R.string.floor_dialog_add_floor_title),
                hint = stringResource(R.string.floor_dialog_add_floor_hint),
                initial = "${floors.size + 1}F",
                primaryLabel = stringResource(R.string.floor_dialog_add_primary),
                onConfirm = { name ->
                    onAddFloor(name)
                    addOpen = false
                },
                onCancel = { addOpen = false },
            )
        }
    }
    if (removeOpen) {
        ModalShell(onClose = { removeOpen = false }) {
            FloorConfirmDialog(
                palette = palette,
                title = stringResource(R.string.floor_dialog_remove_floor_title),
                message = stringResource(
                    R.string.floor_dialog_remove_floor_message,
                    floors.first { it.id == activeFloorId }.name,
                ),
                primaryLabel = stringResource(R.string.floor_dialog_delete),
                primaryColor = Color(0xFFEF4444),
                onConfirm = {
                    onRemoveFloor(activeFloorId)
                    removeOpen = false
                },
                onCancel = { removeOpen = false },
            )
        }
    }
    if (renamingId != null) {
        // commit on outside click via dummy backdrop
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable {
                    val id = renamingId ?: return@clickable
                    onRenameFloor(id, renameVal)
                    renamingId = null
                },
        )
    }
}

@Composable
private fun ModalShell(onClose: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.clickable(enabled = false) {}) { content() }
    }
}

@Composable
private fun FloorRenameDialog(
    palette: FloorPalette,
    title: String,
    hint: String,
    initial: String,
    primaryLabel: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit,
) {
    var value by remember { mutableStateOf(initial) }
    Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.raised)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(20.dp),
    ) {
        Text(title, color = palette.text1, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Spacer(Modifier.height(4.dp))
        Text(hint, color = palette.text2, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(palette.card)
                .border(1.dp, palette.border, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .width(240.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = { value = it },
                singleLine = true,
                textStyle = TextStyle(color = palette.text1, fontSize = 14.sp),
                cursorBrush = SolidColor(Blue500),
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Spacer(Modifier.weight(1f))
            DialogButton(stringResource(R.string.common_cancel), palette.text1, palette.card, palette.border, onCancel)
            DialogButton(primaryLabel, Color.White, Blue500, Blue500, { onConfirm(value.ifBlank { initial }) })
        }
    }
}

@Composable
private fun FloorConfirmDialog(
    palette: FloorPalette,
    title: String,
    message: String,
    primaryLabel: String,
    primaryColor: Color,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.raised)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(20.dp),
    ) {
        Text(title, color = palette.text1, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Text(message, color = palette.text2, fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Spacer(Modifier.weight(1f))
            DialogButton(stringResource(R.string.common_cancel), palette.text1, palette.card, palette.border, onCancel)
            DialogButton(primaryLabel, Color.White, primaryColor, primaryColor, onConfirm)
        }
    }
}

@Composable
private fun DialogButton(label: String, contentColor: Color, bg: Color, border: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(label, color = contentColor, fontSize = 13.sp)
    }
}
