package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.PaymentCard
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

@Composable
fun SecurityPaymentsSettings(colors: PosColors, passwordOnly: Boolean = false) {
    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val cards = remember { mutableStateListOf(*PosMockData.paymentCards.toTypedArray()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(
            colors = colors,
            title = "Change password",
            subtitle = "Use at least 8 characters and one number.",
            badge = "Required",
            badgeIcon = "🔒",
        ) {
            SettingLabel(colors, "Current password")
            SettingTextField(colors, current, { current = it }, leading = "🔑", keyboard = KeyboardType.Password)
            Spacer(Modifier.height(12.dp))
            SettingLabel(colors, "New password")
            SettingTextField(colors, newPass, { newPass = it }, leading = "🔐", keyboard = KeyboardType.Password)
            Spacer(Modifier.height(12.dp))
            SettingLabel(colors, "Confirm new password")
            SettingTextField(colors, confirm, { confirm = it }, leading = "🔐", keyboard = KeyboardType.Password)
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                "Update password",
                modifier = Modifier.fillMaxWidth(),
                enabled = current.isNotEmpty() && newPass.length >= 8 && newPass == confirm,
                onClick = { current = ""; newPass = ""; confirm = "" },
            )
        }

        if (passwordOnly) return@Column

        SettingCard(colors = colors, title = "Saved payment methods", subtitle = "Cards we charge for upgrades.", badge = "${cards.size}", badgeIcon = "💳") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cards.forEach { card -> PaymentCardRow(colors, card, onRemove = { cards.remove(card) }) }
                OutlineButton("+ Add card", Blue500, onClick = {
                    cards.add(PaymentCard("credit card", "9999", "01/30", "Demo Holder"))
                }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun PaymentCardRow(colors: PosColors, card: PaymentCard, onRemove: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 44.dp, height = 30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Blue500),
            contentAlignment = Alignment.Center,
        ) {
            Text("VISA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("•••• ${card.last4}", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                if (card.isDefault) SmallChip("DEFAULT", Blue500)
            }
            Text("${card.holderName} · exp ${card.expiry}", color = colors.textMuted, fontSize = 11.sp)
        }
        Box(
            Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Text("✕", color = Red500, fontSize = 13.sp)
        }
    }
}
