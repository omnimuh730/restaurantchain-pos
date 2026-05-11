package com.mh.restaurantchainpos.pos.ui.layout.navigation

import com.mh.restaurantchainpos.pos.data.PosPage

internal fun posPageNavGlyph(page: PosPage): String =
    when (page) {
        PosPage.FloorPlan -> "F"
        PosPage.Orders -> "O"
        PosPage.Kitchen -> "K"
        PosPage.Analytics -> "A"
        PosPage.Settings -> "S"
    }
