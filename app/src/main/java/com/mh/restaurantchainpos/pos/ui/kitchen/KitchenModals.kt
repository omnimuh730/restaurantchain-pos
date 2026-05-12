package com.mh.restaurantchainpos.pos.ui.kitchen

import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.KitchenItem
import com.mh.restaurantchainpos.pos.ui.i18n.ordersMenuLineTitle
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Green500
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
                    Text(ordersMenuLineTitle(item.titleKey), color = colors.textMuted, fontSize = 12.sp)
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
                    Text(stringResource(R.string.kitchen_partial_of_qty, item.qty), color = colors.textMuted, fontSize = 11.sp)
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
        "complete" -> "Complete Items?"
        "recall" -> "Recall to received"
        "accept" -> "Accept order"
        else -> "Confirm"
    }
    val subtitle = when (action) {
        "complete" -> "The following items will be marked as completed:"
        else -> "${items.size} item(s)"
    }
    ModalScaffold(onDismiss = onCancel) {
        Column(
            Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .widthIn(max = 340.dp),
        ) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        color = colors.text,
                        fontWeight = if (action == "complete") FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = if (action == "complete") 17.sp else 15.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(subtitle, color = colors.textMuted, fontSize = if (action == "complete") 13.sp else 12.sp)
                }
                Text("✕", color = colors.textMuted, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onCancel))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (action == "complete") 10.dp else 6.dp),
            ) {
                if (action == "complete") {
                    items.take(6).forEach { CompleteConfirmItemRow(colors, it) }
                } else {
                    items.take(6).forEach {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(Blue500))
                            Spacer(Modifier.width(8.dp))
                            Text(ordersMenuLineTitle(it.titleKey), color = colors.text, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text("${it.selectedQty ?: it.qty}", color = colors.textMuted, fontSize = 12.sp)
                        }
                    }
                }
                if (items.size > 6) Text(stringResource(R.string.kitchen_more_items, items.size - 6), color = colors.textMuted, fontSize = 11.sp)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (action == "complete") {
                    ModalButton("Cancel", colors.textMuted, colors.surface, colors.border, Modifier.weight(1f), onCancel)
                    ModalButton("Complete", Color.White, Blue500, Blue500, Modifier.weight(1f), onConfirm)
                } else {
                    ModalButton("Cancel", colors.text, colors.surfaceRaised, colors.border, Modifier.weight(1f), onCancel)
                    ModalButton("Confirm", Color.White, Blue500, Blue500, Modifier.weight(1f), onConfirm)
                }
            }
        }
    }
}

@Composable
private fun CompleteConfirmItemRow(colors: PosColors, item: KitchenItem) {
    val qty = item.selectedQty ?: item.qty
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Green500),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(ordersMenuLineTitle(item.titleKey), color = colors.text, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(qty.toString(), color = colors.text, fontSize = 14.sp)
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnClickOutside = false,
        ),
    ) {
        val view = LocalView.current
        val blurRadiusPx = with(LocalDensity.current) { 32.dp.roundToPx() }
        SideEffect {
            val window = (view.parent as? DialogWindowProvider)?.window ?: return@SideEffect
            window.setDimAmount(0f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
                window.setBackgroundBlurRadius(blurRadiusPx)
            }
        }
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
}
