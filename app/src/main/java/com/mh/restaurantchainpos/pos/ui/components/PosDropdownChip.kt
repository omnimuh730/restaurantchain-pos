package com.mh.restaurantchainpos.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

/** Default rounded-corner radius for dropdown menus shown by [PosDropdownChip]. */
val PosDropdownMenuRadius: Dp = 12.dp

/** Default elevation/shadow for dropdown menus shown by [PosDropdownChip]. */
val PosDropdownMenuElevation: Dp = 8.dp

/**
 * Visual variants for [PosDropdownChip]. They share the same anatomy
 * (leading icon? + label + chevron + Material3 [DropdownMenu]) but use
 * different background palettes so the same trigger reads correctly in
 * different surfaces.
 *
 * - [Soft]: tinted-background pill — used for selectors that sit on the
 *   surface (Orders floor/table, header role).
 * - [Outlined]: transparent fill with a 1dp border — used for muted
 *   selectors that sit inside a header (Kitchen sort).
 */
enum class PosDropdownChipVariant { Soft, Outlined }

/**
 * Material3 [DropdownMenu] with the same surface treatment as [PosDropdownChip]
 * menus (rounded corners, POS surface color, unified shadow).
 */
@Composable
fun PosDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    colors: PosColors,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 4.dp),
    shape: RoundedCornerShape = RoundedCornerShape(PosDropdownMenuRadius),
    menuWidth: Dp? = null,
    content: @Composable () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = if (menuWidth != null) {
            modifier.width(menuWidth).padding(horizontal = 4.dp, vertical = 4.dp)
        } else {
            modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        },
        offset = offset,
        shape = shape,
        containerColor = colors.surface,
        tonalElevation = 0.dp,
        shadowElevation = PosDropdownMenuElevation,
    ) {
        content()
    }
}

/**
 * The unified dropdown trigger used across the app. Provides a consistent
 * look (rounded pill, semi-bold label, animated chevron) and a Material3
 * [DropdownMenu] surface configured to match.
 *
 * The actual menu rows are passed as [content] so each call site is free to
 * render its own row visuals (use [PosDropdownMenuRow] for the standard
 * blue-fill-when-selected style).
 */
@Composable
fun PosDropdownChip(
    text: String,
    expanded: Boolean,
    colors: PosColors,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    leadingIconTint: Color? = null,
    labelColor: Color? = null,
    chevronTint: Color? = null,
    variant: PosDropdownChipVariant = PosDropdownChipVariant.Soft,
    menuOffset: DpOffset = DpOffset(0.dp, 4.dp),
    menuWidth: Dp? = null,
    menuShape: RoundedCornerShape = RoundedCornerShape(PosDropdownMenuRadius),
    content: @Composable () -> Unit,
) {
    val resolvedLabelColor = labelColor ?: colors.text
    val resolvedChevronTint = chevronTint ?: resolvedLabelColor
    val triggerModifier = when (variant) {
        PosDropdownChipVariant.Soft -> Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.newItemsBg)
        PosDropdownChipVariant.Outlined -> Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(999.dp))
    }
    Box(modifier) {
        Row(
            triggerModifier
                .height(36.dp)
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 8.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = leadingIconTint ?: resolvedLabelColor,
                    modifier = Modifier.size(16.dp),
                )
            }
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    color = resolvedLabelColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = resolvedChevronTint,
            )
        }
        PosDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            colors = colors,
            modifier = if (menuWidth != null) Modifier.width(menuWidth) else Modifier,
            offset = menuOffset,
            shape = menuShape,
        ) {
            content()
        }
    }
}

/**
 * Standard dropdown row: full-width fill with [PosColors.accent] when selected.
 */
@Composable
fun PosDropdownMenuRow(
    index: Int,
    totalCount: Int,
    text: String,
    selected: Boolean,
    colors: PosColors,
    onClick: () -> Unit,
    secondaryText: String? = null,
    leadingIcon: ImageVector? = null,
    danger: Boolean = false,
) {
    val shape = posMenuRowShape(index, totalCount)
    val bg = when {
        danger -> colors.surface
        selected -> colors.accent
        else -> colors.surface
    }
    val fg = when {
        danger -> Color(0xFFEF4444)
        selected -> colors.onAccent
        else -> colors.text
    }
    val secondaryFg = when {
        danger -> Color(0xFFEF4444).copy(alpha = 0.85f)
        selected -> colors.onAccent.copy(alpha = 0.88f)
        else -> colors.textMuted
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(16.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = text,
                color = fg,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            )
            if (secondaryText != null) {
                Text(
                    text = secondaryText,
                    color = secondaryFg,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

/** Builds the per-row clipping shape so the highlight respects the menu's
 *  rounded outer corners. */
internal fun posMenuRowShape(index: Int, count: Int): RoundedCornerShape {
    val r = PosDropdownMenuRadius
    if (count <= 1) return RoundedCornerShape(r)
    val top = if (index == 0) r else 0.dp
    val bottom = if (index == count - 1) r else 0.dp
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

