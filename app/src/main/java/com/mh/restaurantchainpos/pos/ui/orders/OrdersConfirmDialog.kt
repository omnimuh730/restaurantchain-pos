package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.i18n.rememberOrderCatalogString
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

/**
 * Confirmation dialog shown when the user taps "Order".
 * Lists the new (non-ordered) lines that will be sent to the kitchen.
 */
@Composable
internal fun ConfirmOrderDialog(
    colors: PosColors,
    tableLabel: String,
    newItems: List<OrderLine>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(colors.overlay)
            .clickable(onClick = onCancel),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 420.dp)
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .clickable(enabled = false) {}
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                stringResource(R.string.orders_confirm_title),
                color = colors.text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                if (newItems.size == 1) {
                    stringResource(R.string.orders_confirm_new_item_for_table, tableLabel)
                } else {
                    stringResource(R.string.orders_confirm_new_items_for_table, newItems.size, tableLabel)
                },
                color = colors.textMuted,
                fontSize = 13.sp,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                    .heightIn(max = 240.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                newItems.forEachIndexed { index, line ->
                    ConfirmDialogRow(line = line, colors = colors)
                    if (index < newItems.lastIndex) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .height(1.dp)
                                .background(colors.border),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Spacer(Modifier.weight(1f))
                DialogButton(
                    label = stringResource(R.string.common_cancel),
                    background = colors.surfaceRaised,
                    foreground = colors.text,
                    onClick = onCancel,
                )
                DialogButton(
                    label = stringResource(R.string.common_confirm),
                    background = Blue600,
                    foreground = Color.White,
                    onClick = onConfirm,
                )
            }
        }
    }
}

@Composable
private fun ConfirmDialogRow(line: OrderLine, colors: PosColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 32.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Blue500)
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                stringResource(R.string.orders_badge_new),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            rememberOrderCatalogString("orders_item", line.baseId, line.baseId),
            color = colors.text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            "\u00D7${line.qty} \u00B7 ${formatLineMoney(line.price * line.qty, line.currency)}",
            color = colors.textMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun DialogButton(
    label: String,
    background: Color,
    foreground: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

internal enum class OrderedLineModifyKind { Increase, Decrease, Remove }

/**
 * Confirmation dialog shown when the user adjusts (`-` / `+`) or removes an
 * already-ordered line, mirroring the kitchen `ConfirmActionModal` style.
 */
@Composable
internal fun ConfirmModifyOrderedDialog(
    colors: PosColors,
    line: OrderLine,
    kind: OrderedLineModifyKind,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val description = when (kind) {
        OrderedLineModifyKind.Increase -> stringResource(R.string.orders_modify_increase)
        OrderedLineModifyKind.Decrease -> stringResource(R.string.orders_modify_decrease)
        OrderedLineModifyKind.Remove -> stringResource(R.string.orders_modify_remove)
    }
    val nextQty = when (kind) {
        OrderedLineModifyKind.Increase -> line.qty + 1
        OrderedLineModifyKind.Decrease -> (line.qty - 1).coerceAtLeast(0)
        OrderedLineModifyKind.Remove -> 0
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(colors.overlay)
            .clickable(onClick = onCancel),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 360.dp)
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .clickable(enabled = false) {}
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                stringResource(R.string.orders_modify_confirm_title),
                color = colors.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(description, color = colors.textMuted, fontSize = 12.sp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceRaised)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    rememberOrderCatalogString("orders_item", line.baseId, line.baseId),
                    color = colors.text,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    stringResource(R.string.orders_modify_qty_from_to, line.qty, nextQty),
                    color = colors.textMuted,
                    fontSize = 12.sp,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Spacer(Modifier.weight(1f))
                DialogButton(
                    label = stringResource(R.string.common_cancel),
                    background = colors.surfaceRaised,
                    foreground = colors.text,
                    onClick = onCancel,
                )
                DialogButton(
                    label = stringResource(R.string.common_confirm),
                    background = Blue600,
                    foreground = Color.White,
                    onClick = onConfirm,
                )
            }
        }
    }
}
