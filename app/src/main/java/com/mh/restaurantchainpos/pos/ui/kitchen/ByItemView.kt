package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.KitchenOrder
import com.mh.restaurantchainpos.pos.ui.theme.Amber400
import com.mh.restaurantchainpos.pos.ui.theme.Amber500
import com.mh.restaurantchainpos.pos.ui.theme.Green400
import com.mh.restaurantchainpos.pos.ui.theme.Green500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

/**
 * Aggregated view that lists every distinct kitchen item across the supplied
 * orders, with progress badges and the tables that requested each item.
 */
@Composable
fun ByItemView(colors: PosColors, sorted: List<KitchenOrder>) {
    val rows = remember(sorted) { aggregate(sorted) }
    if (rows.isEmpty()) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("⚠️", fontSize = 36.sp, color = colors.textMuted)
            Spacer(Modifier.height(8.dp))
            Text("No items in this view", color = colors.textMuted, fontSize = 13.sp)
        }
        return
    }

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
                .background(colors.surfaceRaised)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HeaderCell("ITEM", colors, weight = 5f, align = TextAlign.Start)
            HeaderCell("QTY", colors, weight = 2f, align = TextAlign.Center)
            HeaderCell("DONE", colors, weight = 2f, align = TextAlign.Center)
            HeaderCell("TABLES", colors, weight = 3f, align = TextAlign.Start)
        }
        LazyColumn(Modifier.fillMaxWidth().height(420.dp)) {
            items(rows) { row ->
                ByItemRowView(colors = colors, row = row)
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .background(colors.surfaceRaised)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${rows.size} items across ${sorted.size} orders",
                color = colors.textMuted,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                "${rows.sumOf { it.totalQty }} total",
                color = colors.textMuted,
                fontSize = 12.sp,
            )
        }
    }
}

private data class ByItemRow(
    val name: String,
    val modifier: String?,
    val totalQty: Int,
    val doneQty: Int,
    val tables: List<String>,
)

private fun aggregate(sorted: List<KitchenOrder>): List<ByItemRow> {
    val map = LinkedHashMap<String, ByItemRow>()
    sorted.forEach { order ->
        order.items.forEach { item ->
            if (item.previouslyCompleted) return@forEach
            val mod = item.modifier.takeIf { it.isNotBlank() }
            val key = item.name + (mod?.let { "|$it" } ?: "")
            val existing = map[key]
            if (existing == null) {
                map[key] = ByItemRow(
                    name = item.name,
                    modifier = mod,
                    totalQty = item.qty,
                    doneQty = if (item.done) item.qty else 0,
                    tables = listOf(order.table),
                )
            } else {
                map[key] = existing.copy(
                    totalQty = existing.totalQty + item.qty,
                    doneQty = existing.doneQty + if (item.done) item.qty else 0,
                    tables = if (existing.tables.contains(order.table)) existing.tables else existing.tables + order.table,
                )
            }
        }
    }
    return map.values.toList()
}

@Composable
private fun HeaderCell(text: String, colors: PosColors, weight: Float, align: TextAlign) {
    Box(
        Modifier.fillMaxWidth(weight / 12f),
        contentAlignment = if (align == TextAlign.Center) Alignment.Center else Alignment.CenterStart,
    ) {
        Text(
            text,
            color = colors.textMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = align,
        )
    }
}

@Composable
private fun ByItemRowView(colors: PosColors, row: ByItemRow) {
    val allDone = row.doneQty >= row.totalQty
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (allDone) Green500.copy(alpha = 0.05f) else Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.fillMaxWidth(5f / 12f)) {
            Text(
                row.name,
                color = if (allDone) colors.textMuted else colors.text,
                fontSize = 13.sp,
                textDecoration = if (allDone) TextDecoration.LineThrough else null,
            )
            row.modifier?.let {
                Text(it, color = colors.textMuted, fontSize = 10.sp)
            }
        }
        Box(Modifier.fillMaxWidth(2f / 7f), contentAlignment = Alignment.Center) {
            Text(
                row.totalQty.toString(),
                color = if (allDone) colors.textMuted else colors.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Box(Modifier.fillMaxWidth(2f / 5f), contentAlignment = Alignment.Center) {
            ProgressBadge(done = row.doneQty, total = row.totalQty, allDone = allDone)
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            row.tables.take(4).forEach { tid ->
                Box(
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.surfaceRaised)
                        .border(1.dp, colors.border, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .wrapContentSize(),
                ) {
                    Text(tid, color = colors.text, fontSize = 9.sp)
                }
            }
            if (row.tables.size > 4) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.surfaceRaised)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text("+${row.tables.size - 4}", color = colors.textMuted, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
private fun ProgressBadge(done: Int, total: Int, allDone: Boolean) {
    val (bg, fg) = when {
        allDone -> Green500.copy(alpha = 0.18f) to Green400
        done > 0 -> Amber500.copy(alpha = 0.18f) to Amber400
        else -> Color(0xFF1F2937) to Color(0xFF9CA3AF)
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text("$done/$total", color = fg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
