package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.data.formatMoney
import com.mh.restaurantchainpos.pos.ui.components.PillButton
import com.mh.restaurantchainpos.pos.ui.components.PosCard
import com.mh.restaurantchainpos.pos.ui.components.PosPrimaryButton
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.Green500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlinx.coroutines.delay

@Composable
fun PaymentSheet(colors: PosColors, totalUsd: Double, totalKrw: Double, checkNumber: String, tableLabel: String, onClose: () -> Unit) {
    var method by remember { mutableStateOf("cash") }
    var cardNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    var step by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    val steps = listOf("Validating card", "Checking password", "Checking balance", "Approving payment")

    LaunchedEffect(processing, step) {
        if (processing && step < steps.lastIndex) {
            delay(800)
            step += 1
        } else if (processing && step == steps.lastIndex) {
            delay(700)
            done = true
            processing = false
        }
    }

    Box(Modifier.fillMaxSize().background(colors.overlay).clickable(onClick = onClose), contentAlignment = Alignment.Center) {
        PosCard(colors, Modifier.fillMaxWidth(0.92f).clickable(enabled = false) {}) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Payment", color = colors.text, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                Spacer(Modifier.weight(1f))
                Text("Close", color = colors.textMuted, modifier = Modifier.clickable(onClick = onClose))
            }
            Text("$checkNumber · $tableLabel", color = colors.textMuted, fontSize = 12.sp)
            Row(Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillButton("Cash", method == "cash", colors) { method = "cash" }
                PillButton("Credit QR", method == "qr", colors) { method = "qr" }
                PillButton("Credit Card", method == "card", colors) { method = "card" }
                PillButton("Mix", method == "mix", colors) { method = "mix" }
            }
            PosCard(colors, Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    Text("KRW", color = colors.textMuted)
                    Spacer(Modifier.weight(1f))
                    Text(formatMoney(totalKrw, CurrencyKind.Domestic), color = Blue600, fontWeight = FontWeight.Bold)
                }
                Row(Modifier.fillMaxWidth()) {
                    Text("USD", color = colors.textMuted)
                    Spacer(Modifier.weight(1f))
                    Text(formatMoney(totalUsd, CurrencyKind.Foreign), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            }
            when (method) {
                "qr" -> QrPanel(colors)
                "card" -> {
                    OutlinedTextField(cardNumber, { cardNumber = it.take(16) }, Modifier.fillMaxWidth().padding(top = 12.dp), placeholder = { Text("1111111111111111") }, singleLine = true)
                    OutlinedTextField(password, { password = it.take(8) }, Modifier.fillMaxWidth().padding(top = 8.dp), placeholder = { Text("12345678") }, singleLine = true)
                }
                "mix" -> Text("Split tender mirrors the React flow: cash and card portions are calculated independently.", color = colors.textMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
                else -> Text("Cash received input and change due summary.", color = colors.textMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
            }
            if (processing) {
                Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    steps.forEachIndexed { index, label ->
                        Text(if (index <= step) "✓ $label" else "• $label", color = if (index <= step) Green500 else colors.textMuted, fontSize = 12.sp)
                    }
                }
            }
            if (done) Text("Payment complete", color = Green500, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 12.dp))
            Spacer(Modifier.height(12.dp))
            PosPrimaryButton(if (done) "Done" else "Process Payment", Modifier.fillMaxWidth()) {
                if (done) onClose() else {
                    step = 0
                    done = false
                    processing = true
                }
            }
        }
    }
}

@Composable
private fun QrPanel(colors: PosColors) {
    val transition = rememberInfiniteTransition(label = "qr-scan")
    val scan by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "scan",
    )
    Box(Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(172.dp).clip(RoundedCornerShape(18.dp)).background(Color.White).border(1.dp, colors.border, RoundedCornerShape(18.dp)).padding(14.dp)) {
            val cell = size.width / 9f
            for (x in 0..8) {
                for (y in 0..8) {
                    if ((x * y + x + y) % 3 == 0) drawRect(Color.Black, topLeft = androidx.compose.ui.geometry.Offset(x * cell, y * cell), size = androidx.compose.ui.geometry.Size(cell * 0.72f, cell * 0.72f))
                }
            }
            drawLine(Blue600, androidx.compose.ui.geometry.Offset(0f, size.height * scan), androidx.compose.ui.geometry.Offset(size.width, size.height * scan), strokeWidth = 4f)
        }
    }
}
