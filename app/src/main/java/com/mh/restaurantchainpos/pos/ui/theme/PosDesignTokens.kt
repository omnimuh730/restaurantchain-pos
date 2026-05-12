package com.mh.restaurantchainpos.pos.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Non-color design constants (opacity steps, fixed measurements) shared by
 * POS surfaces. Prefer these over magic numbers in composables.
 */
object PosOpacity {
    /** Disabled controls, ghost buttons. */
    const val Disabled = 0.38f

    /** Very subtle tint on a parent (e.g. hover-like states). */
    const val SubtleTint = 0.06f

    /** Light-mode nav tab selection wash (paired with [PosColors.navSelectedBackground]). */
    const val NavSelectedLight = 0.12f

    /** Dark-mode nav tab selection wash — higher than light for contrast on near-black bars. */
    const val NavSelectedDark = 0.34f
}

object PosElevation {
    val MenuDropdown: Dp = 8.dp
    val CardResting: Dp = 2.dp
    val BarFloating: Dp = 6.dp
}

object PosSizes {
    val NavIndicatorHeight: Dp = 3.dp
    val NavIndicatorWidth: Dp = 30.dp
    val HeaderLogo: Dp = 36.dp
}
