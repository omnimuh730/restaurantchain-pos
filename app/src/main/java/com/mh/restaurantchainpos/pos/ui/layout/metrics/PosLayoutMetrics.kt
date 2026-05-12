package com.mh.restaurantchainpos.pos.ui.layout.metrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainpos.pos.ui.theme.PosDimens

/**
 * Window-derived layout decisions for the POS chrome (header + primary navigation).
 *
 * @param railBreakpointDp When [android.content.res.Configuration.screenWidthDp] is at least this value,
 * primary navigation moves to a start [androidx.compose.material3.NavigationRail]-style column.
 */
data class PosLayoutMetrics(
    val widthDp: Int,
    val heightDp: Int,
    val useBottomNavigation: Boolean,
    val useStartNavigationRail: Boolean,
    /** Horizontal padding for the app header; tighter on very narrow phones. */
    val headerHorizontalPadding: Dp,
)

@Composable
@ReadOnlyComposable
fun rememberPosLayoutMetrics(
    // Bumped from 900 → 1280 so the bottom navigation persists across phones
    // and medium tablets (Pixel Tablet, iPad-class portrait/landscape, etc.)
    // and only flips to a start rail on true desktop-class widths.
    railBreakpointDp: Int = 1280,
    compactHeaderWidthDp: Int = 400,
): PosLayoutMetrics {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp
    val useRail = widthDp >= railBreakpointDp
    return PosLayoutMetrics(
        widthDp = widthDp,
        heightDp = heightDp,
        useBottomNavigation = !useRail,
        useStartNavigationRail = useRail,
        headerHorizontalPadding = if (widthDp < compactHeaderWidthDp) PosDimens.SpaceMd else PosDimens.SpaceLg,
    )
}
