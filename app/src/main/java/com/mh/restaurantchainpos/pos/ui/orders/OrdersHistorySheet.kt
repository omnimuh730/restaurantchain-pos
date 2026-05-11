package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun HistorySheet(colors: PosColors, onClose: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.48f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clickable(enabled = false) {},
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Today's Bill History", color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("${TodayBills.size} bills - ${formatDomesticWon(TodayBills.sumOf { it.krw })} - ${formatForeignUsd(TodayBills.sumOf { it.usd })}", color = colors.textMuted, fontSize = 12.sp)
                }
                Spacer(Modifier.weight(1f))
                Text("Close", color = colors.textMuted, fontSize = 13.sp, modifier = Modifier.clickable(onClick = onClose))
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(TodayBills, key = { it.id }) { bill ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("${bill.id} - ${tableLabel(bill.tableId)}", color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("${bill.time} - ${bill.method}", color = colors.textMuted, fontSize = 11.sp)
                        }
                        Text(paySummary(bill.krw, bill.usd), color = colors.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
