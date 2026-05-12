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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
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
        title = stringResource(R.string.settings_gen_phone_numbers_title),
        headerIcon = Icons.Outlined.Phone,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                Text(stringResource(R.string.settings_gen_main_phone), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                SettingTextField(colors, mainPhone, onMainPhoneChange, leadingIcon = Icons.Outlined.Phone, keyboard = KeyboardType.Phone)
            }
            Column {
                Text(stringResource(R.string.settings_gen_alt_phone), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                SettingTextField(colors, altPhone, onAltPhoneChange, leadingIcon = Icons.Outlined.Phone, keyboard = KeyboardType.Phone)
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.settings_gen_alt_phone_hint), color = colors.textMuted, fontSize = 11.sp)
            }
        }
    }
}
