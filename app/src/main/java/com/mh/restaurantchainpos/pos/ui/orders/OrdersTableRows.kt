package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun OrderTableHeader(colors: PosColors) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(28.dp)
            .border(1.dp, colors.border)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Item", color = Blue600, fontSize = 10.sp, modifier = Modifier.weight(1f))
        Text("Qty", color = Blue600, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.width(74.dp))
        Text("Each", color = Blue600, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(58.dp))
        Text("Line", color = Blue600, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(64.dp))
        Spacer(Modifier.width(22.dp))
    }
}

@Composable
internal fun OrderLineRow(
    colors: PosColors,
    line: OrderLine,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                line.name,
                color = colors.text,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (line.deleted) TextDecoration.LineThrough else TextDecoration.None,
            )
            if (line.modifiers.isNotEmpty()) {
                Text(
                    line.modifiers.joinToString(" / "),
                    color = colors.textMuted,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(Modifier.width(74.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            QtyButton("-", colors, onMinus)
            Text(line.qty.toString(), color = colors.text, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.width(24.dp))
            QtyButton("+", colors, onPlus)
        }
        Text(formatLineMoney(line.price, line.currency), color = colors.text, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(58.dp))
        Text(formatLineMoney(line.price * line.qty, line.currency), color = colors.text, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(64.dp))
        Text("x", color = colors.textMuted, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.width(22.dp).clickable(onClick = onRemove))
    }
}
