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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600

/**
 * Mobile-friendly PIN input with `length` boxes that mirror digits as the user types.
 *
 * Implementation note: instead of one focusable cell per digit (which is awkward on
 * Compose for Android), we use a single hidden BasicTextField and render each digit
 * inside a styled cell. This matches the React component visually and behaviorally
 * (paste fills all cells, backspace clears the last).
 */
@Composable
fun PinInput(
    value: String,
    onChange: (String) -> Unit,
    label: String? = null,
    length: Int = 6,
    modifier: Modifier = Modifier,
) {
    val focus = remember { FocusRequester() }
    val sanitized = value.filter { it.isDigit() }.take(length)
    Column(modifier.fillMaxWidth()) {
        if (label != null) {
            Text(label, color = Color(0xFF9CA3AF), fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
        }
        Box {
            BasicTextField(
                value = sanitized,
                onValueChange = { v -> onChange(v.filter { it.isDigit() }.take(length)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
                cursorBrush = SolidColor(Color.Transparent),
                modifier = Modifier
                    .matchParentSize()
                    .focusRequester(focus),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth()) {
                repeat(length) { i ->
                    val ch = sanitized.getOrNull(i)
                    val filled = ch != null
                    Box(
                        Modifier
                            .size(width = 40.dp, height = 48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A1D25))
                            .border(1.dp, if (filled) Blue600 else Color(0xFF374151), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(if (filled) "•" else "", color = Color(0xFFE5E7EB), fontSize = 20.sp, textAlign = TextAlign.Center)
                    }
                    if (i < length - 1) Spacer(Modifier.width(0.dp))
                }
            }
        }
    }
}
