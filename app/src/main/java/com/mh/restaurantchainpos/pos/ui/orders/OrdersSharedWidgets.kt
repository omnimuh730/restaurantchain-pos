package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun DropdownChip(
    text: String,
    expanded: Boolean,
    colors: PosColors,
    onExpandedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    Box {
        Box(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surfaceRaised)
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 9.dp, vertical = 7.dp),
        ) {
            Text("$text ${if (expanded) "^" else "v"}", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            content()
        }
    }
}

@Composable
internal fun CompactButton(text: String, colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun ActionButton(
    text: String,
    active: Boolean,
    enabled: Boolean,
    colors: PosColors,
    modifier: Modifier = Modifier,
    badge: Int? = null,
    onClick: () -> Unit,
) {
    val bg = when {
        !enabled -> colors.surfaceRaised
        active -> Blue600
        else -> Color.Transparent
    }
    val fg = when {
        !enabled -> colors.textMuted.copy(alpha = 0.65f)
        active -> Color.White
        else -> Blue600
    }
    Row(
        modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, if (!active && enabled) Blue600 else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (badge != null) {
            Spacer(Modifier.width(6.dp))
            Box(Modifier.size(18.dp).clip(CircleShape).background(Blue600), contentAlignment = Alignment.Center) {
                Text(badge.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun QtyButton(text: String, colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .size(width = 18.dp, height = 18.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = colors.textMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun TotalRow(label: String, value: String, colors: PosColors) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text(value, color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun SplitHandle(colors: PosColors, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(colors.surface)
            .border(1.dp, colors.border),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(width = 40.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(colors.textMuted.copy(alpha = 0.35f)))
    }
}

@Composable
internal fun SearchBox(value: String, onValueChange: (String) -> Unit, colors: PosColors) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = colors.text, fontSize = 12.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                SearchGlyph(colors.textMuted)
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isBlank()) Text("Search", color = colors.textMuted.copy(alpha = 0.72f), fontSize = 11.sp)
                    inner()
                }
            }
        },
    )
}

@Composable
private fun SearchGlyph(color: Color) {
    Canvas(Modifier.size(14.dp)) {
        drawCircle(
            color = color,
            radius = size.minDimension * 0.32f,
            center = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.42f),
            style = Stroke(width = 1.7f),
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.66f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.9f),
            strokeWidth = 1.7f,
        )
    }
}
