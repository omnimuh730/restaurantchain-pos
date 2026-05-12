package com.mh.restaurantchainpos.pos.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

@Composable
internal fun ChoiceCard(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1D25))
            .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
            .clickableNoIndication(onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Blue600.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("→", color = Color(0xFF60A5FA), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = Color(0xFFE5E7EB), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = Color(0xFF6B7280), fontSize = 12.sp)
        }
        Text("›", color = Color(0xFF6B7280), fontSize = 18.sp)
    }
}

@Composable
internal fun DropdownButton(label: String, placeholder: Boolean, open: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A1D25))
            .border(1.dp, Color(0xFF374151), RoundedCornerShape(10.dp))
            .clickableNoIndication(onToggle)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = if (placeholder) Color(0xFF4B5563) else Color(0xFFE5E7EB), fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(if (open) "˄" else "˅", color = Color(0xFF6B7280), fontSize = 14.sp)
    }
}

@Composable
internal fun WaitingPanel(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    onSignIn: () -> Unit,
) {
    Box(
        Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color(0xFF78350F).copy(alpha = 0.3f))
            .border(1.dp, Color(0xFFB45309).copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("⏱", color = Color(0xFFFBBF24), fontSize = 22.sp)
    }
    Spacer(Modifier.height(20.dp))
    Text(title, color = Color(0xFFE5E7EB), fontSize = 18.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(8.dp))
    Text(subtitle, color = Color(0xFF6B7280), fontSize = 13.sp)
    Spacer(Modifier.height(24.dp))
    if (onBack != null) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFF374151), RoundedCornerShape(10.dp))
                .clickableNoIndication(onBack)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Go Back", color = Color(0xFFD1D5DB), fontSize = 13.sp)
        }
        Spacer(Modifier.height(8.dp))
    }
    Box(
        Modifier
            .fillMaxWidth()
            .clickableNoIndication(onSignIn)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("Back to Sign In", color = Color(0xFF60A5FA), fontSize = 13.sp)
    }
}

@Composable
internal fun BackTextLink(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clickableNoIndication(onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("‹ $text", color = Color(0xFF6B7280), fontSize = 13.sp)
    }
}
