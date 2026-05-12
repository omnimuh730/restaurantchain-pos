package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlin.math.roundToInt

@Composable
fun UpgradePlans(colors: PosColors) {
    var slidePay by remember { mutableStateOf<String?>(null) }
    val proFeatures = stringArrayResource(R.array.settings_up_pro_features).toList()
    val ultraFeatures = stringArrayResource(R.array.settings_up_ultra_features).toList()
    val proLabel = stringResource(R.string.settings_plan_pro)
    val ultraLabel = stringResource(R.string.settings_plan_ultra)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(
            colors = colors,
            title = stringResource(R.string.settings_upgrade_page_title),
            subtitle = stringResource(R.string.settings_upgrade_page_subtitle),
            badge = stringResource(R.string.settings_tier_free),
            badgeIcon = Icons.Outlined.AutoAwesome,
            headerIcon = Icons.Outlined.AutoAwesome,
        ) {
            PlanCard(
                colors = colors,
                planKey = "Pro",
                planLabel = proLabel,
                price = "$29",
                features = proFeatures,
                icon = Icons.Outlined.AutoAwesome,
            ) { slidePay = "Pro" }
            Spacer(Modifier.height(12.dp))
            PlanCard(
                colors = colors,
                planKey = "Ultra",
                planLabel = ultraLabel,
                price = "$79",
                features = ultraFeatures,
                icon = Icons.Outlined.WorkspacePremium,
            ) { slidePay = "Ultra" }
        }
    }

    slidePay?.let { planKey ->
        SlideToPayDialog(colors = colors, planKey = planKey, onClose = { slidePay = null })
    }
}

@Composable
private fun PlanCard(
    colors: PosColors,
    planKey: String,
    planLabel: String,
    price: String,
    features: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onUpgrade: () -> Unit,
) {
    val highlighted = planKey == "Ultra"
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (highlighted) Blue500.copy(alpha = 0.08f) else colors.surfaceRaised)
            .border(1.5.dp, if (highlighted) Blue600 else colors.border, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Blue500.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = Blue600, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(planLabel, color = colors.text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    if (planKey == "Pro") {
                        stringResource(R.string.settings_plan_pro_tagline)
                    } else {
                        stringResource(R.string.settings_plan_ultra_tagline)
                    },
                    color = colors.textMuted,
                    fontSize = 11.sp,
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, color = colors.text, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(stringResource(R.string.settings_upgrade_per_month), color = colors.textMuted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        Spacer(Modifier.height(10.dp))
        features.forEach { feature ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp),
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Blue600, modifier = Modifier.size(14.dp))
                Spacer(Modifier.size(8.dp))
                Text(feature, color = colors.text, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        PrimaryButton(
            stringResource(R.string.settings_upgrade_choose_plan, planLabel),
            onUpgrade,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = Icons.Outlined.Bolt,
        )
    }
}

@Composable
private fun SlideToPayDialog(colors: PosColors, planKey: String, onClose: () -> Unit) {
    var dragX by remember { mutableStateOf(0f) }
    val maxDragPx = with(androidx.compose.ui.platform.LocalDensity.current) { 240.dp.toPx() }
    var done by remember { mutableStateOf(false) }
    val planLabel = when (planKey) {
        "Pro" -> stringResource(R.string.settings_plan_pro)
        else -> stringResource(R.string.settings_plan_ultra)
    }
    ModalScrim(onDismiss = onClose) {
        Column(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .widthIn(max = 360.dp)
                .consumeModalTaps()
                .padding(20.dp),
        ) {
            Text(stringResource(R.string.settings_upgrade_confirm_title, planLabel), color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.settings_upgrade_confirm_hint), color = colors.textMuted, fontSize = 12.sp)
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
                        stringResource(R.string.settings_upgrade_subscribed),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Center),
                    )
                } else {
                    Text(
                        stringResource(R.string.settings_upgrade_slide_to_pay),
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
            OutlineButton(
                if (done) stringResource(R.string.common_close) else stringResource(R.string.common_cancel),
                colors.text,
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
