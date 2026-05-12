package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val AnalyticsCardShape = RoundedCornerShape(14.dp)

/** Shared elevated surface for analytics screens (shadow + clip; no stroke). */
@Composable
internal fun AnalyticsCard(card: Color, isDark: Boolean, content: @Composable () -> Unit) {
    val elevation = if (isDark) 16.dp else 6.dp
    val ambient = if (isDark) Color.Black.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.07f)
    val spot = if (isDark) Color.Black.copy(alpha = 0.55f) else Color.Black.copy(alpha = 0.12f)
    Box(
        Modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = AnalyticsCardShape,
                ambientColor = ambient,
                spotColor = spot,
            )
            .clip(AnalyticsCardShape)
            .background(card),
    ) { content() }
}
