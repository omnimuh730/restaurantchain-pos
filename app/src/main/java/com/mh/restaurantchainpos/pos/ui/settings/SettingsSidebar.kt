package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

/**
 * Sidebar for the Settings screen. Mirrors the Analytics sidebar contract so
 * it can either dock on the left in wide layouts (`docked = true`) or slide in
 * from the left as an overlay drawer in narrow layouts (`docked = false`).
 */
@Composable
internal fun SettingsSidebar(
    colors: PosColors,
    sections: List<SettingsSection>,
    active: SettingsSection,
    onSelect: (SettingsSection) -> Unit,
    docked: Boolean,
    drawerOpen: Boolean,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (docked) {
        Box(modifier.width(264.dp).fillMaxHeight()) {
            SidebarContent(
                colors = colors,
                sections = sections,
                active = active,
                onSelect = onSelect,
                showClose = false,
                onClose = {},
            )
        }
    } else {
        Box(modifier.fillMaxSize()) {
            AnimatedVisibility(visible = drawerOpen, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCloseDrawer,
                        ),
                )
            }
            AnimatedVisibility(
                visible = drawerOpen,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it },
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(296.dp)
                        .background(colors.surface),
                ) {
                    SidebarContent(
                        colors = colors,
                        sections = sections,
                        active = active,
                        onSelect = {
                            onSelect(it)
                            onCloseDrawer()
                        },
                        showClose = true,
                        onClose = onCloseDrawer,
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarContent(
    colors: PosColors,
    sections: List<SettingsSection>,
    active: SettingsSection,
    onSelect: (SettingsSection) -> Unit,
    showClose: Boolean,
    onClose: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxHeight()
            .background(colors.surface)
            .border(1.dp, colors.border)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.settings_title),
                color = colors.text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            if (showClose) {
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.settings_close_cd),
                        tint = colors.textMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
        Spacer(Modifier.size(12.dp))
        sections.forEach { section ->
            SettingsSidebarItem(
                colors = colors,
                section = section,
                active = section == active,
                onClick = { onSelect(section) },
            )
            Spacer(Modifier.size(4.dp))
        }
        Spacer(Modifier.fillMaxWidth().size(8.dp))
    }
}
