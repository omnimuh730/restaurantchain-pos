package com.mh.restaurantchainpos.pos.ui.analytics

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.i18n.fmtTimeLocalized
import com.mh.restaurantchainpos.pos.ui.i18n.historyGuestLine
import com.mh.restaurantchainpos.pos.ui.i18n.historyNoteText
import com.mh.restaurantchainpos.pos.ui.i18n.paymentMethodLabel
import com.mh.restaurantchainpos.pos.ui.i18n.receiptLineTitle
import com.mh.restaurantchainpos.pos.ui.i18n.relDayLabel
import com.mh.restaurantchainpos.pos.ui.i18n.tableNumberLabel
import com.mh.restaurantchainpos.pos.ui.i18n.stringTitle
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

@Composable
internal fun HistoryDetailSheet(event: HistoryEvent, isDark: Boolean, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val em = stringResource(R.string.analytics_detail_em_dash)
    val text1 = if (isDark) Color(0xFFE5E7EB) else Color(0xFF1E293B)
    val text2 = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val card = if (isDark) Color(0xFF1F2937) else Color.White
    val border = if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0)
    val tile = if (isDark) Color(0xFF111827) else Color(0xFFF8FAFC)

    // Own window so the sheet works above vertically-scrolled History content
    // (unbounded height there breaks fillMaxSize overlays).
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
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
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
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
                    Text(stringResource(R.string.analytics_detail_title), color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
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
                        Text(historyGuestLine(event.guest, event.guestKey), color = text1, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${relDayLabel(ctx, event.timestampMs)} · ${fmtTimeLocalized(event.timestampMs)}",
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
                        Text(event.status.stringTitle(), color = statusFg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailTile(stringResource(R.string.analytics_detail_tile_table), tableNumberLabel(event.tableNum), "📍", tile, text1, text2, Modifier.weight(1f))
                        DetailTile(
                            stringResource(R.string.analytics_detail_tile_party),
                            event.partySize?.let { stringResource(R.string.analytics_detail_party_guests, it) } ?: em,
                            "👥",
                            tile,
                            text1,
                            text2,
                            Modifier.weight(1f),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailTile(stringResource(R.string.analytics_detail_tile_paid_at), event.paidAt ?: em, "💳", tile, text1, text2, Modifier.weight(1f))
                        DetailTile(
                            stringResource(R.string.analytics_detail_tile_method),
                            event.paymentKey?.let { paymentMethodLabel(it) } ?: em,
                            "💵",
                            tile,
                            text1,
                            text2,
                            Modifier.weight(1f),
                        )
                    }
                    DetailTile(stringResource(R.string.analytics_detail_tile_id), event.id, "#", tile, text1, text2, Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(12.dp))
                if (event.items.isNotEmpty()) {
                    Text(stringResource(R.string.analytics_detail_receipt), color = text2, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(tile)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(stringResource(R.string.analytics_col_item), color = text2, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text(stringResource(R.string.analytics_detail_col_qty), color = text2, fontSize = 11.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
                        Text(stringResource(R.string.analytics_detail_col_price), color = text2, fontSize = 11.sp, modifier = Modifier.width(64.dp), textAlign = TextAlign.End)
                        Text(stringResource(R.string.analytics_detail_col_total), color = text2, fontSize = 11.sp, modifier = Modifier.width(72.dp), textAlign = TextAlign.End)
                    }
                    event.items.forEach { it ->
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            Text(receiptLineTitle(it), color = text1, fontSize = 13.sp, modifier = Modifier.weight(1f))
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
                        Text(stringResource(R.string.analytics_detail_total), color = text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
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
                val noteText = historyNoteText(event.noteKey)
                if (noteText != null) {
                    Box(
                        Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tile)
                            .padding(12.dp),
                    ) {
                        Column {
                            Text(stringResource(R.string.analytics_detail_notes), color = text2, fontSize = 11.sp)
                            Text(noteText, color = text1, fontSize = 13.sp)
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
