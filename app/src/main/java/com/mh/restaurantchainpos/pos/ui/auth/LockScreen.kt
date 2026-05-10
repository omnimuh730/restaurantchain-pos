package com.mh.restaurantchainpos.pos.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.AuthSession
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import kotlinx.coroutines.delay

@Composable
fun LockScreen(
    session: AuthSession,
    onUnlocked: () -> Unit,
    onSwitchAccount: () -> Unit,
) {
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val initials = session.name
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .joinToString("")
        .uppercase()
        .take(2)

    LaunchedEffect(loading) {
        if (!loading) return@LaunchedEffect
        delay(600)
        loading = false
        onUnlocked()
    }

    AuthBackdrop {
        Box(
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Blue600.copy(alpha = 0.15f))
                .border(1.dp, Blue600.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("\uD83D\uDD12", fontSize = 22.sp)
        }
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Blue600),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials.ifBlank { "AD" }, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(12.dp))
        Text(session.name, color = Color(0xFFE5E7EB), fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(2.dp))
        Text("@${session.username}", color = Color(0xFF6B7280), fontSize = 13.sp)
        Spacer(Modifier.height(28.dp))
        AuthField(
            label = "Password",
            value = password,
            onChange = { password = it; error = "" },
            placeholder = "Enter your password",
            password = true,
        )
        if (error.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            AuthErrorBox(error)
        }
        Spacer(Modifier.height(20.dp))
        AuthPrimaryButton(
            text = if (loading) "Unlocking" else "Unlock",
            enabled = !loading,
            loading = loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            error = ""
            if (password.length < 6) {
                error = "Password must be at least 6 characters."
                return@AuthPrimaryButton
            }
            loading = true
        }
        Spacer(Modifier.height(24.dp))
        Box(Modifier.clickableNoIndication(onSwitchAccount)) {
            Text("Switch account", color = Color(0xFF6B7280), fontSize = 13.sp)
        }
    }
}
