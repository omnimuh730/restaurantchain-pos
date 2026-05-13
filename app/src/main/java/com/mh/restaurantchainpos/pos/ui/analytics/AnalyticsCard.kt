package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.ui.components.PosElevatedSurface

private val AnalyticsCardShape = RoundedCornerShape(14.dp)

/** Shared elevated surface for analytics screens (shadow + clip; no stroke). */
@Composable
internal fun AnalyticsCard(card: Color, isDark: Boolean, content: @Composable () -> Unit) {
    PosElevatedSurface(
        isDark = isDark,
        modifier = Modifier.fillMaxWidth(),
        shape = AnalyticsCardShape,
        fillColor = card,
    ) {
        content()
    }
}
