package com.mh.restaurantchainpos.pos.ui.layout.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
                navigationRail()
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    header()
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        content()
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                header()
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    content()
                }
                bottomBar()
            }
        }
    }
}
