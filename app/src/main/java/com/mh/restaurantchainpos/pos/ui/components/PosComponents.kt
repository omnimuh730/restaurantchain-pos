package com.mh.restaurantchainpos.pos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosDimens

@Composable
fun PosCard(
    colors: PosColors,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(PosDimens.RadiusLg))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(PosDimens.RadiusLg))
            .padding(PosDimens.SpaceLg),
        content = content,
    )
}

@Composable
fun PillButton(
    text: String,
    active: Boolean,
    colors: PosColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg = if (active) Blue600 else Color.Transparent
    val fg = if (active) Color.White else colors.textMuted
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(PosDimens.RadiusMd))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = fg, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
fun PosPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.height(44.dp),
        enabled = enabled,
        shape = RoundedCornerShape(PosDimens.RadiusLg),
        colors = ButtonDefaults.buttonColors(containerColor = Blue600, contentColor = Color.White),
        onClick = onClick,
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String = "", colors: PosColors) {
    Column {
        Text(title, color = colors.text, fontWeight = FontWeight.Medium, fontSize = 18.sp)
        if (subtitle.isNotBlank()) {
            Text(subtitle, color = colors.textMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, change: String, colors: PosColors, accent: Color = Blue600, modifier: Modifier = Modifier) {
    PosCard(colors, modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).clip(CircleShape).background(accent.copy(alpha = 0.13f)), contentAlignment = Alignment.Center) {
                Text(title.take(1), color = accent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, color = colors.textMuted, fontSize = 11.sp)
                Text(value, color = colors.text, fontWeight = FontWeight.Medium, fontSize = 20.sp)
            }
            Spacer(Modifier.weight(1f))
            Text(change, color = if (change.startsWith("-")) Color(0xFFEF4444) else Color(0xFF22C55E), fontSize = 12.sp)
        }
    }
}

@Composable
fun SimpleBars(values: List<Pair<String, Int>>, color: Color, colors: PosColors, modifier: Modifier = Modifier) {
    val max = values.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        values.forEach { (label, value) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = colors.textMuted, fontSize = 11.sp, modifier = Modifier.width(42.dp))
                Canvas(Modifier.weight(1f).height(9.dp)) {
                    drawLine(colors.border, start = androidx.compose.ui.geometry.Offset(0f, size.height / 2), end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2), strokeWidth = size.height, cap = StrokeCap.Round)
                    drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, size.height / 2), end = androidx.compose.ui.geometry.Offset(size.width * value / max, size.height / 2), strokeWidth = size.height, cap = StrokeCap.Round)
                }
                Text(value.toString(), color = colors.text, fontSize = 11.sp, modifier = Modifier.width(42.dp).padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(999.dp)).background(color.copy(alpha = 0.14f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ToastPill(show: Boolean, text: String, colors: PosColors) {
    val scale by animateFloatAsState(if (show) 1f else 0.94f, label = "toast-scale")
    AnimatedVisibility(show) {
        Box(Modifier.fillMaxWidth().padding(PosDimens.SpaceLg), contentAlignment = Alignment.TopEnd) {
            Box(
                Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFDbeafe).copy(alpha = 0.7f))
                    .border(1.dp, Color(0xFF93C5FD).copy(alpha = 0.55f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text(text, color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
