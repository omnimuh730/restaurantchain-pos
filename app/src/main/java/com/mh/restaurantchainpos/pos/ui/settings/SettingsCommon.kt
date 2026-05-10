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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

/** Card surface used by every settings section to present a titled block. */
@Composable
fun SettingCard(
    colors: PosColors,
    title: String,
    subtitle: String? = null,
    badge: String? = null,
    badgeIcon: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp)),
    ) {
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(title, color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(subtitle, color = colors.textMuted, fontSize = 12.sp)
                }
            }
            if (badge != null) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceRaised)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text("${badgeIcon ?: ""} $badge".trim(), color = colors.text, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        Column(Modifier.padding(20.dp)) { content() }
    }
}

@Composable
fun SettingLabel(colors: PosColors, label: String) {
    Text(label, color = colors.textMuted, fontSize = 12.sp)
    Spacer(Modifier.height(6.dp))
}

@Composable
fun SettingTextField(
    colors: PosColors,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    keyboard: KeyboardType = KeyboardType.Text,
    leading: String? = null,
    trailing: String? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leading != null) {
                Text(leading, color = colors.textMuted, fontSize = 13.sp)
                Spacer(Modifier.size(8.dp))
            }
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboard),
                textStyle = TextStyle(color = colors.text, fontSize = 13.sp),
                cursorBrush = SolidColor(Blue500),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, color = colors.textMuted, fontSize = 13.sp)
                    }
                    inner()
                },
            )
            if (trailing != null) {
                Spacer(Modifier.size(8.dp))
                Text(trailing, color = colors.textMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ToggleSwitch(checked: Boolean, color: Color = Blue500, onChange: () -> Unit) {
    Box(
        Modifier
            .size(width = 36.dp, height = 20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (checked) color else Color(0xFFCBD5E1))
            .clickable(onClick = onChange),
    ) {
        Box(
            Modifier
                .padding(2.dp)
                .size(16.dp)
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
fun PrimaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) Blue500 else Blue500.copy(alpha = 0.5f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun OutlineButton(label: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, color, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
