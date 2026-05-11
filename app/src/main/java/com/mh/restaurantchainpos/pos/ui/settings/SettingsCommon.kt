package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

/** Card surface used by every settings section to present a titled block. */
@Composable
fun SettingCard(
    colors: PosColors,
    title: String,
    subtitle: String? = null,
    badge: String? = null,
    badgeIcon: ImageVector? = null,
    headerIcon: ImageVector? = null,
    headerIconTint: Color = Blue500,
    content: @Composable () -> Unit,
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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (headerIcon != null) {
                        Icon(headerIcon, contentDescription = null, tint = headerIconTint, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(8.dp))
                    }
                    Text(title, color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                if (subtitle != null) {
                    Spacer(Modifier.height(3.dp))
                    Text(subtitle, color = colors.textMuted, fontSize = 12.sp)
                }
            }
            if (badge != null) {
                CardBadge(colors = colors, text = badge, icon = badgeIcon)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
fun CardBadge(
    colors: PosColors,
    text: String,
    icon: ImageVector? = null,
    tint: Color = colors.textMuted,
    background: Color = colors.surfaceRaised,
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(11.dp))
            Spacer(Modifier.size(4.dp))
        }
        Text(text, color = tint, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FreeTierBadge(colors: PosColors) {
    CardBadge(colors, "Free Tier", Icons.Outlined.ShoppingBag)
}

@Composable
fun SettingLabel(colors: PosColors, label: String, modifier: Modifier = Modifier) {
    Text(label, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = modifier)
    Spacer(Modifier.height(6.dp))
}

/** Smaller, muted label used above sub-fields. */
@Composable
fun SubFieldLabel(colors: PosColors, label: String) {
    Text(label, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(6.dp))
}

@Composable
fun SettingTextField(
    colors: PosColors,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    keyboard: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
    leadingText: String? = null,
    leadingTint: Color = colors.textMuted,
    trailingIcon: ImageVector? = null,
    trailingText: String? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = leadingTint, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(10.dp))
            } else if (leadingText != null) {
                Text(leadingText, color = leadingTint, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.size(10.dp))
            }
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                enabled = enabled,
                keyboardOptions = KeyboardOptions(keyboardType = keyboard),
                textStyle = TextStyle(color = colors.text, fontSize = 14.sp),
                cursorBrush = SolidColor(Blue600),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, color = colors.textMuted, fontSize = 14.sp)
                    }
                    inner()
                },
            )
            if (trailingIcon != null) {
                Spacer(Modifier.size(8.dp))
                Icon(trailingIcon, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(16.dp))
            }
            if (trailingText != null) {
                Spacer(Modifier.size(8.dp))
                Text(trailingText, color = colors.textMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ToggleSwitch(checked: Boolean, color: Color = Blue600, size: Float = 1f, onChange: () -> Unit) {
    val w = (40 * size).dp
    val h = (22 * size).dp
    val thumb = (18 * size).dp
    Box(
        Modifier
            .size(width = w, height = h)
            .clip(RoundedCornerShape(50))
            .background(if (checked) color else Color(0xFFCBD5E1))
            .clickable(onClick = onChange),
    ) {
        Box(
            Modifier
                .padding(2.dp)
                .size(thumb)
                .clip(CircleShape)
                .background(Color.White)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart),
        )
    }
}

@Composable
fun SmallChip(label: String, color: Color, contentColor: Color = Color.White) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(label, color = contentColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) Blue600 else Blue600.copy(alpha = 0.4f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                Spacer(Modifier.size(6.dp))
            }
            Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun OutlineButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, color, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(Modifier.size(6.dp))
            }
            Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatBox(colors: PosColors, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(label, color = colors.textMuted, fontSize = 11.sp)
        Spacer(Modifier.height(3.dp))
        Text(value, color = colors.text, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun IconBox(
    icon: ImageVector,
    background: Color,
    tint: Color,
    size: Int = 36,
    iconSize: Int = 18,
    radius: Int = 10,
    onClick: (() -> Unit)? = null,
) {
    val mod = Modifier
        .size(size.dp)
        .clip(RoundedCornerShape(radius.dp))
        .background(background)
    Box(
        modifier = if (onClick != null) mod.clickable(onClick = onClick) else mod,
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(iconSize.dp))
    }
}

/**
 * Full-window modal scrim. Renders via [Dialog] so it overlays the entire app
 * (above the top header and bottom navigation). Tapping the dimmed background
 * dismisses; taps on the inner content do not bubble up.
 */
@Composable
fun ModalScrim(
    onDismiss: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val scrimSource = remember { MutableInteractionSource() }
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .clickable(
                    interactionSource = scrimSource,
                    indication = null,
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

/** Modifier used on the modal content container to swallow taps so the scrim doesn't dismiss. */
@Composable
fun Modifier.consumeModalTaps(): Modifier {
    val src = remember { MutableInteractionSource() }
    return this.clickable(interactionSource = src, indication = null, enabled = true) {}
}
