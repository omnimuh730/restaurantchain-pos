package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun PhoneNumbersCard(
    colors: PosColors,
    mainPhone: String,
    onMainPhoneChange: (String) -> Unit,
    altPhone: String,
    onAltPhoneChange: (String) -> Unit,
) {
    SettingCard(
        colors = colors,
        title = "Phone Numbers",
        headerIcon = Icons.Outlined.Phone,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                Text("Main Phone", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                SettingTextField(colors, mainPhone, onMainPhoneChange, leadingIcon = Icons.Outlined.Phone, keyboard = KeyboardType.Phone)
            }
            Column {
                Text("Alternate Phone", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                SettingTextField(colors, altPhone, onAltPhoneChange, leadingIcon = Icons.Outlined.Phone, keyboard = KeyboardType.Phone)
                Spacer(Modifier.height(4.dp))
                Text("Optional. Shown to guests when the main line is busy.", color = colors.textMuted, fontSize = 11.sp)
            }
        }
    }
}
