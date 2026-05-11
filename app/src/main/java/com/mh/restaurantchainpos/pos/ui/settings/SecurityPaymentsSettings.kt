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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
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
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

@Composable
fun SecurityPaymentsSettings(colors: PosColors, passwordOnly: Boolean = false) {
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var floorPlanAlert by remember { mutableStateOf(true) }
    var kitchenAlert by remember { mutableStateOf(true) }
    val cards = remember { mutableStateListOf(*PosMockData.paymentCards.toTypedArray()) }
    var selectedCardId by remember { mutableStateOf(cards.firstOrNull { it.isDefault }?.last4 ?: cards.firstOrNull()?.last4) }

    val mismatch = confirm.isNotEmpty() && newPass != confirm
    val canSubmit = newPass.length >= 8 && newPass == confirm

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(
            colors = colors,
            title = "Password Settings",
            subtitle = "Update your manager PIN or account password",
            headerIcon = Icons.Outlined.Shield,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Column {
                    Text("New Password", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    SettingTextField(
                        colors = colors,
                        value = newPass,
                        onChange = { newPass = it },
                        leadingIcon = Icons.Outlined.Lock,
                        trailingIcon = if (showNew) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        keyboard = if (showNew) KeyboardType.Text else KeyboardType.Password,
                    )
                }
                Column {
                    Text("Confirm Password", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    SettingTextField(
                        colors = colors,
                        value = confirm,
                        onChange = { confirm = it },
                        leadingIcon = Icons.Outlined.LockOpen,
                        trailingIcon = if (showConfirm) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        keyboard = if (showConfirm) KeyboardType.Text else KeyboardType.Password,
                    )
                    if (mismatch) {
                        Spacer(Modifier.height(4.dp))
                        Text("Passwords do not match", color = Red500, fontSize = 11.sp)
                    }
                }
                PrimaryButton(
                    "Update Password",
                    onClick = { newPass = ""; confirm = "" },
                    enabled = canSubmit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (!passwordOnly) {
            SettingCard(
                colors = colors,
                title = "Notifications",
                subtitle = "Toggle toast alerts for incoming activity",
                headerIcon = Icons.Outlined.Notifications,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NotificationRow(
                        colors = colors,
                        title = "Floor Plan",
                        description = "New reservation requests",
                        checked = floorPlanAlert,
                        onChange = { floorPlanAlert = !floorPlanAlert },
                    )
                    NotificationRow(
                        colors = colors,
                        title = "Kitchen",
                        description = "New chef tickets created",
                        checked = kitchenAlert,
                        onChange = { kitchenAlert = !kitchenAlert },
                    )
                }
            }

            SettingCard(
                colors = colors,
                title = "Saved Payment Methods",
                subtitle = "Manage your saved cards for billing",
                headerIcon = Icons.Outlined.CreditCard,
                badge = "${cards.size}",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    cards.forEach { card ->
                        PaymentCardRow(
                            colors = colors,
                            card = card,
                            selected = selectedCardId == card.last4,
                            onSelect = { selectedCardId = card.last4 },
                            onRemove = {
                                cards.remove(card)
                                if (selectedCardId == card.last4) selectedCardId = cards.firstOrNull()?.last4
                            },
                        )
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                            .clickable {
                                cards.add(PaymentCard("credit card", "9999", "01/30", "Demo Holder"))
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("+ Add New Card", color = colors.textMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    colors: PosColors,
    title: String,
    description: String,
    checked: Boolean,
    onChange: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = colors.textMuted, fontSize = 11.sp)
        }
        ToggleSwitch(checked = checked, onChange = onChange)
    }
}

@Composable
private fun PaymentCardRow(
    colors: PosColors,
    card: PaymentCard,
    selected: Boolean,
    onSelect: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Blue500.copy(alpha = 0.08f) else colors.surfaceRaised)
            .border(1.dp, if (selected) Blue600 else colors.border, RoundedCornerShape(10.dp))
            .clickable(onClick = onSelect)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 44.dp, height = 30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Blue600),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("•••• ${card.last4}", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                if (card.isDefault) SmallChip("DEFAULT", Blue600)
            }
            Text("${card.holderName} · exp ${card.expiry}", color = colors.textMuted, fontSize = 11.sp)
        }
        if (selected) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
        }
        Box(
            Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Close, contentDescription = null, tint = Red500, modifier = Modifier.size(16.dp))
        }
    }
}
