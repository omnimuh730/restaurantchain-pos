package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

@Composable
internal fun HistKpi(
    label: String,
    value: String,
    sub: String?,
    card: Color,
    border: Color,
    text1: Color,
    text2: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(card)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Column {
            Text(label, color = text2, fontSize = 11.sp)
            Spacer(Modifier.height(2.dp))
            Text(value, color = text1, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            if (sub != null) {
                Text(sub, color = text2, fontSize = 10.sp)
            }
        }
    }
}

@Composable
internal fun TabPill(
    label: String,
    count: Int,
    active: Boolean,
    activeColor: Color,
    text2: Color,
    chip: Color,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) activeColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = if (active) Color.White else text2, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(6.dp))
        Box(
            Modifier
                .clip(CircleShape)
                .background(if (active) Color.White.copy(alpha = 0.18f) else chip)
                .padding(horizontal = 6.dp, vertical = 1.dp),
        ) {
            Text(count.toString(), color = if (active) Color.White else text2, fontSize = 10.sp)
        }
    }
}

@Composable
internal fun ReceiptCard(
    event: HistoryEvent,
    isDark: Boolean,
    onOpen: () -> Unit,
    card: Color,
    border: Color,
    text1: Color,
    text2: Color,
    muted: Color,
) {
    val (icon, accent) = kindIconAccent(event.kind, isDark)
    val statusBg = statusBadgeBg(event.status, isDark)
    val statusFg = statusBadgeFg(event.status, isDark)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(card)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable(onClick = onOpen),
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) { Text(icon, color = accent, fontSize = 14.sp) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(event.guest, color = text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) { Text(event.status.label, color = statusFg, fontSize = 10.sp, fontWeight = FontWeight.Medium) }
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("# ${event.id}", color = text2, fontSize = 11.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("⌖ ${event.tableLabel}", color = text2, fontSize = 11.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("⌚ ${relDay(event.timestampMs)} · ${fmtTime(event.timestampMs)}", color = text2, fontSize = 11.sp)
                }
                if (!event.payment.isNullOrEmpty()) {
                    Text("⌥ ${event.payment}", color = text2, fontSize = 11.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                if (event.amountKrw > 0) {
                    Text(AnalyticsFormat.won(event.amountKrw), color = Blue600, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                if (event.amountUsd > 0.01) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (event.amountKrw > 0) Text("+ ", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(AnalyticsFormat.usd(event.amountUsd), color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (event.items.isNotEmpty()) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(border))
            Column(Modifier.padding(12.dp)) {
                event.items.forEach { it ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(it.name, color = text1, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(it.qty.toString(), color = text1, fontSize = 12.sp, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
                        Text(formatLine(it.price, it.currency), color = text2, fontSize = 12.sp, modifier = Modifier.width(64.dp), textAlign = TextAlign.End)
                        Text(formatLine(it.qty * it.price, it.currency), color = text1, fontSize = 12.sp, modifier = Modifier.width(72.dp), textAlign = TextAlign.End)
                    }
                }
            }
        } else {
            Text(
                when (event.kind) {
                    HistoryKind.NoShow -> "Released after grace period."
                    HistoryKind.Reservation -> "No receipt — reservation only."
                    else -> event.notes ?: "Receipt summary unavailable."
                },
                color = muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}
