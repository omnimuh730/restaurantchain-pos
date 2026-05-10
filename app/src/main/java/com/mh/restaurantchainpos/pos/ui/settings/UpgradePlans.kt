package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlin.math.max
import kotlin.math.roundToInt

private val ProFeatures = listOf(
    "Up to 20 staff members",
    "Advanced analytics & reports",
    "Multi-floor plan support",
    "Priority email support",
    "Custom receipt branding",
    "Table reservation system",
)

private val UltraFeatures = listOf(
    "Everything in Pro",
    "Unlimited staff members",
    "Multi-location support",
    "24/7 priority support",
    "API access & integrations",
    "Custom domain & branding",
    "Advanced inventory tracking",
    "Dedicated account manager",
)

@Composable
fun UpgradePlans(colors: PosColors) {
    var slidePay by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(colors = colors, title = "Plans", subtitle = "Pick the tier that fits your restaurant.", badge = "Free tier", badgeIcon = "✦") {
            PlanCard(colors, "Pro", "$29 / month", ProFeatures) { slidePay = "Pro" }
            Spacer(Modifier.height(12.dp))
            PlanCard(colors, "Ultra", "$79 / month", UltraFeatures) { slidePay = "Ultra" }
        }
    }

    if (slidePay != null) {
        SlideToPayDialog(colors = colors, plan = slidePay!!, onClose = { slidePay = null })
    }
}

@Composable
private fun PlanCard(colors: PosColors, plan: String, price: String, features: List<String>, onUpgrade: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (plan == "Ultra") Blue500.copy(alpha = 0.1f) else colors.surfaceRaised)
            .border(1.dp, if (plan == "Ultra") Blue500 else colors.border, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(plan, color = colors.text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.weight(1f))
            Text(price, color = Blue500, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))
        features.forEach { feature ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(Blue500))
                Spacer(Modifier.size(8.dp))
                Text(feature, color = colors.textMuted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(if (plan == "Ultra") "Slide to upgrade" else "Upgrade to $plan", onUpgrade, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun SlideToPayDialog(colors: PosColors, plan: String, onClose: () -> Unit) {
    var dragX by remember { mutableStateOf(0f) }
    val maxDragPx = with(androidx.compose.ui.platform.LocalDensity.current) { 240.dp.toPx() }
    var done by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .widthIn(max = 360.dp)
                .clickable(enabled = false) {}
                .padding(20.dp),
        ) {
            Text("Confirm $plan upgrade", color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text("Slide the button all the way to the right to authorize.", color = colors.textMuted, fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(if (done) Blue500 else colors.surfaceRaised)
                    .border(1.dp, colors.border, RoundedCornerShape(28.dp)),
            ) {
                if (done) {
                    Text(
                        "Subscribed!",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Center),
                    )
                } else {
                    Text(
                        "Slide to pay",
                        color = colors.textMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                Box(
                    Modifier
                        .offset { IntOffset(dragX.roundToInt(), 0) }
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Blue500)
                        .pointerInput(maxDragPx) {
                            detectDragGestures(
                                onDragEnd = {
                                    if (dragX >= maxDragPx * 0.85f) done = true
                                    else dragX = 0f
                                },
                                onDrag = { _, drag ->
                                    val next = (dragX + drag.x).coerceIn(0f, maxDragPx)
                                    dragX = next
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("→", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlineButton(if (done) "Close" else "Cancel", colors.text, onClick = onClose, modifier = Modifier.fillMaxWidth())
        }
    }
}
