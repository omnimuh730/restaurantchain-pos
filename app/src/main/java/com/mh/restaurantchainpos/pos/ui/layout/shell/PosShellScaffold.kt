package com.mh.restaurantchainpos.pos.ui.layout.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mh.restaurantchainpos.pos.ui.layout.metrics.PosLayoutMetrics
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.posBackground

/**
 * Root POS shell: background, header, primary navigation (bottom bar or start rail), and page content.
 * Navigation is laid out in the composition hierarchy (not overlaid), so content always receives the correct bounds.
 */
@Composable
fun PosShellScaffold(
    colors: PosColors,
    metrics: PosLayoutMetrics,
    header: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    navigationRail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxSize().background(posBackground(colors))) {
        if (metrics.useStartNavigationRail) {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.statusBarsPadding().navigationBarsPadding()) {
                    navigationRail()
                }
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    Box(Modifier.fillMaxWidth().background(colors.surface).statusBarsPadding()) {
                        header()
                    }
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        content()
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .background(colors.navBackground)
                            .windowInsetsBottomHeight(WindowInsets.navigationBars),
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxWidth().background(colors.surface).statusBarsPadding()) {
                    header()
                }
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    content()
                }
                // Extend navBackground through the system gesture / nav inset so it
                // does not show the shell gradient (gray strip) under the bar.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(colors.navBackground),
                ) {
                    bottomBar()
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    }
}
