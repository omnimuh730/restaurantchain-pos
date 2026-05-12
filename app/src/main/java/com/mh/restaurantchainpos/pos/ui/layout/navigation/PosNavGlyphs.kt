package com.mh.restaurantchainpos.pos.ui.layout.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.PosPage

/** Custom navbar glyphs sourced from res/drawable (designer-supplied SVGs). */
@Composable
internal fun posPageNavPainter(page: PosPage): Painter =
    painterResource(
        id = when (page) {
            PosPage.FloorPlan -> R.drawable.ic_nav_floor
            PosPage.Orders -> R.drawable.ic_nav_order
            PosPage.Kitchen -> R.drawable.ic_nav_kitchen
            PosPage.Analytics -> R.drawable.ic_nav_analytics
            PosPage.Settings -> R.drawable.ic_nav_settings
        },
    )

/** Material-icon fallback retained for any consumer that still needs an ImageVector. */
internal fun posPageNavIcon(page: PosPage): ImageVector =
    when (page) {
        PosPage.FloorPlan -> Icons.Outlined.GridView
        PosPage.Orders -> Icons.AutoMirrored.Outlined.Assignment
        PosPage.Kitchen -> Icons.Outlined.Restaurant
        PosPage.Analytics -> Icons.Outlined.BarChart
        PosPage.Settings -> Icons.Outlined.Settings
    }
