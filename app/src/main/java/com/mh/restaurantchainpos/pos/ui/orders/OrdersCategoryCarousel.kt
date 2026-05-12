package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlin.math.max

private const val ItemsPerPage = 4

/**
 * Horizontal pager: 4 category/subcategory chips per page, swipe for next set.
 * Thin line under the pager shows the active page (not dot pagination).
 */
@Composable
internal fun OrdersCategoryCarousel(
    entries: List<Pair<String, String>>,
    selected: String?,
    colors: PosColors,
    activeStrong: Boolean,
    selectedBorder: Color,
    onClick: (String) -> Unit,
    resetKey: Any,
) {
    val pageCount = remember(entries.size) { max(1, (entries.size + ItemsPerPage - 1) / ItemsPerPage) }
    val initialPage = remember(entries, selected, resetKey, pageCount) {
        val idx = entries.indexOfFirst { it.first == selected }.let { if (it < 0) 0 else it }
        (idx / ItemsPerPage).coerceIn(0, pageCount - 1)
    }

    key(resetKey, entries.size) {
        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { pageCount },
        )

        LaunchedEffect(selected, entries) {
            val idx = entries.indexOfFirst { it.first == selected }.coerceAtLeast(0)
            val page = (idx / ItemsPerPage).coerceIn(0, pageCount - 1)
            if (pagerState.settledPage != page) {
                pagerState.scrollToPage(page)
            }
        }

        Column(Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
            ) { page ->
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for (slot in 0 until ItemsPerPage) {
                        val index = page * ItemsPerPage + slot
                        if (index < entries.size) {
                            val (id, label) = entries[index]
                            val active = selected == id
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when {
                                            active && activeStrong -> Blue600
                                            active -> Blue500
                                            else -> colors.chip
                                        },
                                    )
                                    .border(
                                        1.dp,
                                        if (active) selectedBorder else Color.Transparent,
                                        RoundedCornerShape(4.dp),
                                    )
                                    .clickable { onClick(id) }
                                    .padding(horizontal = 7.dp, vertical = 5.dp),
                                contentAlignment = Alignment.BottomStart,
                            ) {
                                Text(
                                    label,
                                    color = if (active) Color.White else colors.text,
                                    fontSize = 11.sp,
                                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                                    lineHeight = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        } else {
                            Spacer(Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
            }

            if (pageCount > 1) {
                BoxWithConstraints(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 2.dp, bottom = 4.dp)
                        .height(3.dp),
                ) {
                    val track = colors.border.copy(alpha = 0.35f)
                    val thumbW = maxWidth / pageCount
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(track),
                    )
                    Box(
                        Modifier
                            .width(thumbW)
                            .align(Alignment.CenterStart)
                            .offset(x = thumbW * pagerState.settledPage)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Blue600),
                    )
                }
            }
        }
    }
}
