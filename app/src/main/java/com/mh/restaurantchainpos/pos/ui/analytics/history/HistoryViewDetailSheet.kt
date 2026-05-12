package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

@Composable
internal fun HistoryDetailSheet(event: HistoryEvent, isDark: Boolean, onClose: () -> Unit) {
    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val card = if (isDark) Color(0xFF1F2937) else Color.White
    val border = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val tile = if (isDark) Color(0xFF111827) else Color(0xFFF8FAFC)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose,
                    ),
            )
        }
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight * 0.75f)
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(card),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) { Text("←", color = text1, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(8.dp))
                    Text("Event details", color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(border))
                val (icon, accent) = kindIconAccent(event.kind, isDark)
                val statusBg = statusBadgeBg(event.status, isDark)
                val statusFg = statusBadgeFg(event.status, isDark)
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) { Text(icon, color = accent, fontSize = 16.sp) }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(event.guest, color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${relDay(event.timestampMs)} · ${fmtTime(event.timestampMs)}",
                            color = text2,
                            fontSize = 12.sp,
                        )
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(event.status.label, color = statusFg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailTile("Table", event.tableLabel, "📍", tile, text1, text2, Modifier.weight(1f))
                        DetailTile("Party", "${event.partySize ?: "—"} guests", "👥", tile, text1, text2, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailTile("Paid at", event.paidAt ?: "—", "💳", tile, text1, text2, Modifier.weight(1f))
                        DetailTile("Method", event.payment ?: "—", "💵", tile, text1, text2, Modifier.weight(1f))
                    }
                    DetailTile("# ID", event.id, "#", tile, text1, text2, Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(12.dp))
                if (event.items.isNotEmpty()) {
                    Text("Receipt", color = text2, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(tile)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text("Item", color = text2, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text("Qty", color = text2, fontSize = 11.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
                        Text("Price", color = text2, fontSize = 11.sp, modifier = Modifier.width(64.dp), textAlign = TextAlign.End)
                        Text("Total", color = text2, fontSize = 11.sp, modifier = Modifier.width(72.dp), textAlign = TextAlign.End)
                    }
                    event.items.forEach { it ->
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            Text(it.name, color = text1, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text(it.qty.toString(), color = text1, fontSize = 13.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
                            Text(formatLine(it.price, it.currency), color = text1, fontSize = 13.sp, modifier = Modifier.width(64.dp), textAlign = TextAlign.End)
                            Text(formatLine(it.qty * it.price, it.currency), color = text1, fontSize = 13.sp, modifier = Modifier.width(72.dp), textAlign = TextAlign.End)
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(border))
                    val totalUsd = event.items.filter { it.currency == AnalyticsCurrency.Foreign }.sumOf { it.qty * it.price }
                    val totalKrw = event.items.filter { it.currency == AnalyticsCurrency.Domestic }.sumOf { (it.qty * it.price).toLong() }
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Total", color = text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            if (totalKrw > 0) {
                                Text(AnalyticsFormat.won(totalKrw), color = Blue600, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            if (totalUsd > 0.01) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (totalKrw > 0) Text("+ ", color = Color(0xFFEF4444), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(AnalyticsFormat.usd(totalUsd), color = Color(0xFFEF4444), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
                if (!event.notes.isNullOrEmpty()) {
                    Box(
                        Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tile)
                            .padding(12.dp),
                    ) {
                        Column {
                            Text("Notes", color = text2, fontSize = 11.sp)
                            Text(event.notes, color = text1, fontSize = 13.sp)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
internal fun DetailTile(
    label: String,
    value: String,
    icon: String,
    tile: Color,
    text1: Color,
    text2: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tile)
            .padding(10.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, color = text2, fontSize = 11.sp)
                Spacer(Modifier.width(4.dp))
                Text(label, color = text2, fontSize = 11.sp)
            }
            Spacer(Modifier.height(2.dp))
            Text(value, color = text1, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
