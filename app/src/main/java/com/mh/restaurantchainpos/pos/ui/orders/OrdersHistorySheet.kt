package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

private const val SheetHeightMin = 0.28f
private const val SheetHeightMax = 0.92f
private const val SheetHeightDefault = 0.62f

@Composable
internal fun HistorySheet(colors: PosColors, onClose: () -> Unit) {
    var sheetFraction by remember { mutableFloatStateOf(SheetHeightDefault) }
    var expandedBillId by remember { mutableStateOf<String?>(null) }
    val bills = TodayBills
    val totalKrw = bills.sumOf { it.krw }
    val totalUsd = bills.sumOf { it.usd }
    val sheetInteraction = remember { MutableInteractionSource() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.48f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.BottomCenter,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val screenHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)
            val sheetHeight = maxHeight * sheetFraction

            Box(Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(sheetHeight)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .clickable(
                            interactionSource = sheetInteraction,
                            indication = null,
                        ) { },
                ) {
                    Column(Modifier.fillMaxSize()) {
                        HistoryDragHandle(
                            colors = colors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .draggable(
                                    orientation = Orientation.Vertical,
                                    state = rememberDraggableState { delta ->
                                        sheetFraction = (sheetFraction - delta / screenHeightPx)
                                            .coerceIn(SheetHeightMin, SheetHeightMax)
                                    },
                                ),
                        )

                        HistorySheetHeader(
                            colors = colors,
                            billCount = bills.size,
                            totalKrw = totalKrw,
                            totalUsd = totalUsd,
                            onClose = onClose,
                        )

                        HorizontalDivider(color = colors.border)

                        HistoryTableHeader(colors = colors)

                        HorizontalDivider(color = colors.border)

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        ) {
                            itemsIndexed(bills, key = { _, bill -> bill.id }) { index, bill ->
                                val displayNo = bills.size - index
                                HistoryBillRow(
                                    colors = colors,
                                    displayNo = displayNo,
                                    bill = bill,
                                    expanded = expandedBillId == bill.id,
                                    onToggle = {
                                        if (bill.lines.isNotEmpty()) {
                                            expandedBillId =
                                                if (expandedBillId == bill.id) null else bill.id
                                        }
                                    },
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                    color = colors.border,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryDragHandle(colors: PosColors, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.textMuted.copy(alpha = 0.35f)),
        )
    }
}

@Composable
private fun HistorySheetHeader(
    colors: PosColors,
    billCount: Int,
    totalKrw: Double,
    totalUsd: Double,
    onClose: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = null,
            tint = colors.text,
            modifier = Modifier
                .padding(top = 2.dp, end = 10.dp)
                .size(22.dp),
        )
        Column(Modifier.weight(1f)) {
            Text(
                "Today's Bill History",
                color = colors.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                historySummaryLine(billCount, totalKrw, totalUsd),
                color = colors.textMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close",
                tint = colors.textMuted,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun HistoryTableHeader(colors: PosColors) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.surfaceRaised)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "No.",
            color = colors.textMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(28.dp),
        )
        Text(
            "Table",
            color = colors.textMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.1f),
        )
        Text(
            "Time",
            color = colors.textMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            "Method",
            color = colors.textMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(52.dp),
            textAlign = TextAlign.Center,
        )
        Column(
            Modifier.width(88.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                "Amount",
                color = colors.textMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HistoryBillRow(
    colors: PosColors,
    displayNo: Int,
    bill: HistoryBill,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(enabled = bill.lines.isNotEmpty(), onClick = onToggle),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = displayNo.toString(),
                color = colors.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(28.dp),
            )
            Text(
                text = historyTableCell(bill.tableId),
                color = colors.text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1.1f),
            )
            Text(
                text = bill.time,
                color = colors.text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(44.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = bill.method,
                color = colors.text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(52.dp),
                textAlign = TextAlign.Center,
            )
            BillAmountColumn(
                krw = bill.krw,
                usd = bill.usd,
                modifier = Modifier.width(88.dp),
            )
        }

        AnimatedVisibility(
            visible = expanded && bill.lines.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            HistoryLineItemsTable(colors = colors, lines = bill.lines)
        }
    }
}

@Composable
private fun BillAmountColumn(krw: Double, usd: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (krw > 0.0) {
            Text(
                formatDomesticWon(krw),
                color = Blue600,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
            )
        }
        if (usd > 0.0) {
            Text(
                formatForeignUsd(usd),
                color = Red500,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun HistoryLineItemsTable(colors: PosColors, lines: List<HistoryLineItem>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Item",
                color = colors.textMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                "Qty",
                color = colors.textMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                "Each",
                color = colors.textMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(56.dp),
                textAlign = TextAlign.End,
            )
            Text(
                "Line",
                color = colors.textMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(56.dp),
                textAlign = TextAlign.End,
            )
        }
        lines.forEach { line ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    line.name,
                    color = colors.text,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "x${line.qty}",
                    color = colors.text,
                    fontSize = 10.sp,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center,
                )
                MoneyText(
                    value = formatLineMoney(line.each, line.currency),
                    currency = line.currency,
                    modifier = Modifier.width(56.dp),
                )
                MoneyText(
                    value = formatLineMoney(line.line, line.currency),
                    currency = line.currency,
                    modifier = Modifier.width(56.dp),
                )
            }
        }
    }
}

@Composable
private fun MoneyText(value: String, currency: CurrencyKind, modifier: Modifier = Modifier) {
    val color = when (currency) {
        CurrencyKind.Domestic -> Blue600
        CurrencyKind.Foreign -> Red500
    }
    Text(
        text = value,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.End,
        modifier = modifier,
    )
}
