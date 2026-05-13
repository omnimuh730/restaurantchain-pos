package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.components.PosElevatedSurface
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun StaffStatCard(
    colors: PosColors,
    label: String,
    value: Int,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    PosElevatedSurface(colors, modifier, RoundedCornerShape(12.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
                Text(label, color = colors.textMuted, fontSize = 11.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text("$value", color = colors.text, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
