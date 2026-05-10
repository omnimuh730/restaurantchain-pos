package com.mh.restaurantchainpos.pos.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

/** Shared dark background + centered card used by all auth screens. */
@Composable
fun AuthBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F14))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.widthIn(max = 360.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) { content() }
    }
}

@Composable
fun AuthBrand(title: String, subtitle: String, badge: String = "POS") {
    Box(
        Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Blue600),
        contentAlignment = Alignment.Center,
    ) {
        Text(badge, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
    Spacer(Modifier.height(16.dp))
    Text(title, color = Color(0xFFE5E7EB), fontSize = 20.sp, fontWeight = FontWeight.Medium, letterSpacing = (-0.3).sp)
    Spacer(Modifier.height(4.dp))
    Text(subtitle, color = Color(0xFF6B7280), fontSize = 13.sp)
    Spacer(Modifier.height(28.dp))
}

@Composable
fun AuthField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    password: Boolean = false,
    keyboard: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        Text(label, color = Color(0xFF9CA3AF), fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A1D25))
                .border(1.dp, Color(0xFF374151), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                enabled = enabled,
                visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboard),
                textStyle = TextStyle(color = Color(0xFFE5E7EB), fontSize = 14.sp),
                cursorBrush = SolidColor(Blue600),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, color = Color(0xFF4B5563), fontSize = 14.sp)
                    }
                    inner()
                },
            )
        }
    }
}

@Composable
fun AuthErrorBox(text: String, modifier: Modifier = Modifier) {
    if (text.isBlank()) return
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF7F1D1D).copy(alpha = 0.2f))
            .border(1.dp, Color(0xFFB91C1C).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(text, color = Color(0xFFF87171), fontSize = 12.sp)
    }
}

@Composable
fun AuthPrimaryButton(
    text: String,
    enabled: Boolean,
    loading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (loading || !enabled) Blue600.copy(alpha = 0.7f) else Blue600)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(if (loading) "$text…" else text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AuthSecondaryLink(text: String, action: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = Color(0xFF6B7280), fontSize = 13.sp)
        Spacer(Modifier.size(4.dp))
        Box(Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)) {
            Text(action, color = Color(0xFF60A5FA), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
internal fun Modifier.clickableNoIndication(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
}
