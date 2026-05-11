package com.mh.restaurantchainpos.pos.ui.layout.responsive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
 * Mirrors the React app's `useIsMobile` hook (Tailwind's `md` breakpoint at 768px).
 * Returns true when the current screen width is below [breakpoint] dp.
 */
@Composable
@ReadOnlyComposable
fun rememberIsMobile(breakpoint: Int = 768): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp < breakpoint
}

object PosBreakpoints {
    val Sm = 640.dp
    val Md = 768.dp
    val Lg = 1024.dp
}
