package com.mh.restaurantchainpos.pos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.ui.theme.DarkPosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

/**
 * Drop shadow + rounded surface, matching [com.mh.restaurantchainpos.pos.ui.analytics.AnalyticsCard]
 * elevation. Optional stroke for cards that keep a hairline border (e.g. plan highlights).
 */
@Composable
fun PosElevatedSurface(
    colors: PosColors,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    fillColor: Color = colors.surface,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit,
) {
    PosElevatedSurface(
        isDark = colors === DarkPosColors,
        modifier = modifier,
        shape = shape,
        fillColor = fillColor,
        borderWidth = borderWidth,
        borderColor = borderColor,
        content = content,
    )
}

@Composable
fun PosElevatedSurface(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    fillColor: Color,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit,
) {
    val elevation = if (isDark) 16.dp else 6.dp
    val ambient = if (isDark) Color.Black.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.07f)
    val spot = if (isDark) Color.Black.copy(alpha = 0.55f) else Color.Black.copy(alpha = 0.12f)
    var m = modifier
        .shadow(elevation, shape, ambientColor = ambient, spotColor = spot)
        .clip(shape)
        .background(fillColor)
    if (borderWidth > 0.dp && borderColor != Color.Unspecified) {
        m = m.border(borderWidth, borderColor, shape)
    }
    Box(modifier = m, content = content)
}
