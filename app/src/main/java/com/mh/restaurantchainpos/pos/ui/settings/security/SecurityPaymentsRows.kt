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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.PaymentCard
import com.mh.restaurantchainpos.pos.ui.components.PosElevatedSurface
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500

@Composable
internal fun NotificationRow(
    colors: PosColors,
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onChange: () -> Unit,
) {
    PosElevatedSurface(colors, Modifier.fillMaxWidth(), RoundedCornerShape(10.dp), fillColor = colors.surfaceRaised) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Blue500.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = colors.textMuted, fontSize = 11.sp)
        }
        ToggleSwitch(checked = checked, onChange = onChange)
        }
    }
}

@Composable
internal fun PaymentCardRow(
    colors: PosColors,
    card: PaymentCard,
    selected: Boolean,
    onSelect: () -> Unit,
    onRemove: () -> Unit,
) {
    PosElevatedSurface(
        colors,
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        RoundedCornerShape(10.dp),
        fillColor = if (selected) Blue500.copy(alpha = 0.08f) else colors.surfaceRaised,
        borderWidth = 1.dp,
        borderColor = if (selected) Blue600 else colors.border,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
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
                if (card.isDefault) SmallChip(stringResource(R.string.staff_payment_default_chip), Blue600)
            }
            Text(stringResource(R.string.staff_payment_card_exp, card.holderName, card.expiry), color = colors.textMuted, fontSize = 11.sp)
        }
        if (selected) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(6.dp))
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
}

@Composable
internal fun AddNewCardButton(colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 2.dp,
                color = colors.border,
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.settings_pay_add_new_row), color = colors.textMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
