package com.mh.restaurantchainpos.pos.ui.kitchen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.KitchenItem
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
fun ItemCountModal(
    colors: PosColors,
    item: KitchenItem,
    action: String,
    onConfirm: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    var count by remember { mutableStateOf(item.selectedQty ?: item.qty) }
    ModalScaffold(onDismiss = onCancel) {
        Column(
            Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .widthIn(max = 320.dp),
        ) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(if (action == "complete") "Complete how many?" else "Recall how many?", color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(item.name, color = colors.textMuted, fontSize = 12.sp)
                }
                Text("✕", color = colors.textMuted, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onCancel))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(
                Modifier.fillMaxWidth().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                CountStepper(colors, "−", enabled = count > 1) { if (count > 1) count -= 1 }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(count.toString(), color = colors.text, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("of ${item.qty}", color = colors.textMuted, fontSize = 11.sp)
                }
                CountStepper(colors, "+", enabled = count < item.qty) { if (count < item.qty) count += 1 }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModalButton("Cancel", colors.text, colors.surfaceRaised, colors.border, Modifier.weight(1f), onCancel)
                ModalButton("Select", Color.White, Blue500, Blue500, Modifier.weight(1f)) { onConfirm(count) }
            }
        }
    }
}

@Composable
fun ConfirmActionModal(
    colors: PosColors,
    action: String,
    items: List<KitchenItem>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val title = when (action) {
        "complete" -> "Mark as completed"
        "recall" -> "Recall to received"
        "accept" -> "Accept order"
        else -> "Confirm"
    }
    ModalScaffold(onDismiss = onCancel) {
        Column(
            Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .widthIn(max = 320.dp),
        ) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(title, color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(2.dp))
                    Text("${items.size} item(s)", color = colors.textMuted, fontSize = 12.sp)
                }
                Text("✕", color = colors.textMuted, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onCancel))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.take(6).forEach {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(Blue500))
                        Spacer(Modifier.width(8.dp))
                        Text(it.name, color = colors.text, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text("${it.selectedQty ?: it.qty}", color = colors.textMuted, fontSize = 12.sp)
                    }
                }
                if (items.size > 6) Text("+${items.size - 6} more", color = colors.textMuted, fontSize = 11.sp)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModalButton("Cancel", colors.text, colors.surfaceRaised, colors.border, Modifier.weight(1f), onCancel)
                ModalButton("Confirm", Color.White, Blue500, Blue500, Modifier.weight(1f), onConfirm)
            }
        }
    }
}

@Composable
private fun CountStepper(colors: PosColors, label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (enabled) colors.surfaceRaised else colors.surfaceRaised.copy(alpha = 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (enabled) colors.text else colors.textMuted, fontSize = 16.sp)
    }
}

@Composable
private fun ModalButton(
    label: String,
    contentColor: Color,
    bg: Color,
    border: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = contentColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun ModalScaffold(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.clickable(enabled = false) {}) { content() }
    }
}
