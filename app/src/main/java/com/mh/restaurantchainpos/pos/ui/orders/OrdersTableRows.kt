package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.i18n.rememberOrderCatalogString
import com.mh.restaurantchainpos.pos.ui.theme.Blue400
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
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
        Text(stringResource(R.string.orders_col_item), color = Blue400, fontSize = 10.sp, modifier = Modifier.weight(1f))
        Text(stringResource(R.string.orders_col_qty), color = Blue400, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.width(74.dp))
        Text(stringResource(R.string.orders_col_each), color = Blue400, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(58.dp))
        Text(stringResource(R.string.orders_col_line), color = Blue400, fontSize = 10.sp, textAlign = TextAlign.End, modifier = Modifier.width(64.dp))
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
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    val highlightAlpha by animateFloatAsState(
        targetValue = if (highlighted) 1f else 0f,
        animationSpec = tween(durationMillis = 420),
        label = "orderLineHighlight",
    )
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Blue500.copy(alpha = highlightAlpha * 0.18f))
            .defaultMinSize(minHeight = 38.dp)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                rememberOrderCatalogString("orders_item", line.baseId, line.baseId),
                color = colors.text,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (line.deleted) TextDecoration.LineThrough else TextDecoration.None,
            )
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
