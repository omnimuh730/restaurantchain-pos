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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.components.posMenuRowShape
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun OrdersDropdownTableRow(
    index: Int,
    totalCount: Int,
    label: String,
    seats: Int,
    hasOrder: Boolean,
    selected: Boolean,
    colors: PosColors,
    onClick: () -> Unit,
) {
    val shape = posMenuRowShape(index, totalCount)
    val bg = if (selected) Blue500 else colors.surface
    val labelColor = if (selected) Color.White else colors.text
    val metaColor = if (selected) Color.White.copy(alpha = 0.88f) else colors.textMuted
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = labelColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (hasOrder) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (selected) Color.White else Blue500),
            )
        }
        Text(
            text = "$seats seats",
            color = metaColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
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
internal fun HistoryButton(colors: PosColors, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = colors.textMuted,
        )
        Text("History", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
        !enabled -> colors.surfaceRaised.copy(alpha = 0.65f)
        active -> Blue500
        else -> colors.surfaceRaised
    }
    val fg = when {
        !enabled -> colors.textMuted.copy(alpha = 0.65f)
        active -> Color.White
        else -> colors.text
    }
    Row(
        modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, if (!active && enabled) colors.border else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (badge != null) {
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Blue500),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badge.toString(),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 10.sp,
                        textAlign = TextAlign.Center,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                    ),
                )
            }
        }
    }
}

@Composable
internal fun QtyButton(text: String, colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when (text) {
            "-" -> Icon(
                imageVector = Icons.Outlined.Remove,
                contentDescription = "Decrease quantity",
                modifier = Modifier.size(16.dp),
                tint = colors.textMuted,
            )
            "+" -> Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Increase quantity",
                modifier = Modifier.size(16.dp),
                tint = colors.textMuted,
            )
            else -> Text(text, color = colors.textMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.inputOutline, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchGlyph(colors.textMuted)
            Spacer(Modifier.width(10.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.text,
                    fontSize = 12.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isBlank()) {
                            Text(
                                "Search",
                                color = colors.textMuted.copy(alpha = 0.72f),
                                fontSize = 11.sp,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            )
                        }
                        inner()
                    }
                },
            )
        }
    }
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
