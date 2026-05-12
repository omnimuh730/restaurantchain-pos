package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.PaymentCard
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import kotlinx.coroutines.delay

@Composable
fun SecurityPaymentsSettings(colors: PosColors, passwordOnly: Boolean = false) {
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var passwordUpdated by remember { mutableStateOf(false) }
    var floorPlanAlert by remember { mutableStateOf(true) }
    var kitchenAlert by remember { mutableStateOf(true) }
    val cards = remember { mutableStateListOf(*PosMockData.paymentCards.toTypedArray()) }
    var selectedCardId by remember { mutableStateOf(cards.firstOrNull { it.isDefault }?.last4 ?: cards.firstOrNull()?.last4) }
    var addCardOpen by remember { mutableStateOf(false) }
    val newCardHolder = stringResource(R.string.settings_pay_new_card_holder)

    val mismatch = confirm.isNotEmpty() && newPass != confirm
    val canSubmit = newPass.isNotEmpty() && newPass == confirm

    if (passwordUpdated) {
        LaunchedEffect("password-updated") {
            delay(3000)
            passwordUpdated = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(
            colors = colors,
            title = stringResource(R.string.settings_sec_password_title),
            subtitle = stringResource(R.string.settings_sec_password_subtitle),
            headerIcon = Icons.Outlined.Shield,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Column {
                    Text(stringResource(R.string.settings_sec_new_password), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    SettingTextField(
                        colors = colors,
                        value = newPass,
                        onChange = { newPass = it; passwordUpdated = false },
                        placeholder = stringResource(R.string.settings_sec_ph_new_password),
                        leadingIcon = Icons.Outlined.Lock,
                        trailingIcon = if (showNew) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        keyboard = if (showNew) androidx.compose.ui.text.input.KeyboardType.Text else androidx.compose.ui.text.input.KeyboardType.Password,
                    )
                }
                Column {
                    Text(stringResource(R.string.settings_sec_confirm_password), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    SettingTextField(
                        colors = colors,
                        value = confirm,
                        onChange = { confirm = it; passwordUpdated = false },
                        placeholder = stringResource(R.string.settings_sec_ph_confirm_password),
                        leadingIcon = Icons.Outlined.LockOpen,
                        trailingIcon = if (showConfirm) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        keyboard = if (showConfirm) androidx.compose.ui.text.input.KeyboardType.Text else androidx.compose.ui.text.input.KeyboardType.Password,
                    )
                    if (mismatch) {
                        Spacer(Modifier.height(4.dp))
                        Text(stringResource(R.string.settings_sec_password_mismatch), color = Red500, fontSize = 11.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PrimaryButton(
                        stringResource(R.string.settings_sec_update_password),
                        onClick = {
                            if (canSubmit) {
                                passwordUpdated = true
                                newPass = ""
                                confirm = ""
                            }
                        },
                        enabled = canSubmit,
                    )
                    if (passwordUpdated) {
                        Spacer(Modifier.size(8.dp))
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Blue500.copy(alpha = 0.12f))
                                .border(1.dp, Blue500.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Outlined.Check, contentDescription = null, tint = Blue600, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.size(5.dp))
                            Text(stringResource(R.string.settings_sec_password_updated), color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        if (!passwordOnly) {
            SettingCard(
                colors = colors,
                title = stringResource(R.string.settings_sec_notifications_title),
                subtitle = stringResource(R.string.settings_sec_notifications_subtitle),
                headerIcon = Icons.Outlined.Notifications,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NotificationRow(
                        colors = colors,
                        icon = Icons.Outlined.GridView,
                        title = stringResource(R.string.settings_sec_notif_floor_title),
                        description = stringResource(R.string.settings_sec_notif_floor_desc),
                        checked = floorPlanAlert,
                        onChange = { floorPlanAlert = !floorPlanAlert },
                    )
                    NotificationRow(
                        colors = colors,
                        icon = Icons.Outlined.SoupKitchen,
                        title = stringResource(R.string.settings_sec_notif_kitchen_title),
                        description = stringResource(R.string.settings_sec_notif_kitchen_desc),
                        checked = kitchenAlert,
                        onChange = { kitchenAlert = !kitchenAlert },
                    )
                }
            }

            SettingCard(
                colors = colors,
                title = stringResource(R.string.settings_sec_saved_cards_title),
                subtitle = stringResource(R.string.settings_sec_saved_cards_subtitle),
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
                    AddNewCardButton(colors = colors) { addCardOpen = true }
                }
            }
        }
    }

    if (addCardOpen) {
        AddCardDialog(
            colors = colors,
            onDismiss = { addCardOpen = false },
            onConfirm = { digits ->
                val last4 = digits.takeLast(4)
                cards.add(PaymentCard("credit card", last4, "12/28", newCardHolder))
                addCardOpen = false
            },
        )
    }
}
