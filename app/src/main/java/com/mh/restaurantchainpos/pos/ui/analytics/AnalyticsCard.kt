package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Shared bordered surface used by analytics screens (avoids duplicate top-level `Card` clashes). */
@Composable
internal fun AnalyticsCard(card: Color, border: Color, content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(card)
            .border(1.dp, border, RoundedCornerShape(14.dp)),
    ) { content() }
}
