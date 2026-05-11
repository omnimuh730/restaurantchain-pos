package com.mh.restaurantchainpos.pos.ui.layout.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.RoomService
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.mh.restaurantchainpos.pos.data.PosPage

internal fun posPageNavIcon(page: PosPage): ImageVector =
    when (page) {
        PosPage.FloorPlan -> Icons.Outlined.GridView
        PosPage.Orders -> Icons.AutoMirrored.Outlined.ReceiptLong
        PosPage.Kitchen -> Icons.Outlined.RoomService
        PosPage.Analytics -> Icons.Outlined.BarChart
        PosPage.Settings -> Icons.Outlined.Settings
    }
