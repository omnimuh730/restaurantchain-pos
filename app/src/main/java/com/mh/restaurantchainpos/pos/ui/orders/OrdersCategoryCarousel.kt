package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

private const val ItemsPerViewport = 4

/** Narrow centered track; thumb width is a fixed fraction of this track for both carousels. */
private val CarouselIndicatorTrackWidth = 104.dp
private const val CarouselIndicatorThumbFractionOfTrack = 0.34f

/**
 * Continuously-scrollable category/sub-category strip.
 *
 * Unlike the previous paged variant, this one shows ~4 chips per viewport and
 * lets the user swipe smoothly between them. When the gesture ends, the row
 * snaps so the leading chip lines up with the viewport's start edge — that way
 * no chip is ever clipped at rest. If the user is already at the last "page"
 * (i.e. fewer than `ItemsPerViewport` items remain to the right), LazyList's
 * own clamping handles the tail naturally and we do not need to back-fill with
 * earlier items.
 *
 * The progress indicator uses a short centered track (not full row width). The
 * blue thumb keeps the same pixel width on main and sub strips; only its travel
 * distance (driven by scroll fraction) differs when category counts differ.
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
    val horizontalPadding = 12.dp
    val itemSpacing = 4.dp
    val rowHeight = 46.dp

    // The `key(resetKey)` wrapper is intentional: switching main category should
    // reset the sub-category carousel scroll to the start, exactly like the
    // previous paged implementation did via `rememberPagerState` recreation.
    key(resetKey, entries.size) {
        val listState = rememberLazyListState()
        val flingBehavior = rememberSnapFlingBehavior(
            lazyListState = listState,
            snapPosition = SnapPosition.Start,
        )

        // External selection (e.g. tapping a chip elsewhere, or initial mount)
        // should bring the selected chip into view, but only if it isn't already
        // fully visible — tapping an on-screen chip must not yank the row.
        LaunchedEffect(selected, entries) {
            val targetIndex = entries.indexOfFirst { it.first == selected }
            if (targetIndex < 0) return@LaunchedEffect
            val info = listState.layoutInfo
            val visible = info.visibleItemsInfo
            val firstVisible = visible.firstOrNull()?.index
            val lastFullyVisible = visible.lastOrNull { item ->
                item.offset + item.size <= info.viewportEndOffset
            }?.index
            val alreadyVisible = firstVisible != null &&
                lastFullyVisible != null &&
                targetIndex in firstVisible..lastFullyVisible
            if (!alreadyVisible) {
                listState.animateScrollToItem(targetIndex)
            }
        }

        Column(Modifier.fillMaxWidth()) {
            BoxWithConstraints(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .height(rowHeight),
            ) {
                // Compute fixed chip width so exactly `ItemsPerViewport` chips
                // (plus the gaps between them) fill the visible row.
                val itemWidth =
                    (maxWidth - itemSpacing * (ItemsPerViewport - 1)) / ItemsPerViewport
                LazyRow(
                    state = listState,
                    flingBehavior = flingBehavior,
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp),
                ) {
                    items(entries, key = { it.first }) { (id, label) ->
                        CategoryChip(
                            label = label,
                            active = selected == id,
                            activeStrong = activeStrong,
                            selectedBorder = selectedBorder,
                            colors = colors,
                            width = itemWidth,
                            onClick = { onClick(id) },
                        )
                    }
                }
            }

            if (entries.size > ItemsPerViewport) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                        .padding(top = 2.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CarouselScrollIndicator(
                        listState = listState,
                        totalItems = entries.size,
                        modifier = Modifier.width(CarouselIndicatorTrackWidth),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    active: Boolean,
    activeStrong: Boolean,
    selectedBorder: Color,
    colors: PosColors,
    width: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .width(width)
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
            .clickable(onClick = onClick)
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
}

/**
 * Smooth single-track progress indicator driven by the LazyRow's scroll state.
 * The track is short and centered; the blue thumb has a fixed width relative
 * to that track so both main- and sub-category rows look identical — only how
 * far the thumb moves per list length changes.
 */
@Composable
private fun CarouselScrollIndicator(
    listState: LazyListState,
    totalItems: Int,
    modifier: Modifier = Modifier,
) {
    val positionFraction by remember(listState, totalItems) {
        derivedStateOf {
            if (totalItems <= ItemsPerViewport) return@derivedStateOf 0f
            val info = listState.layoutInfo
            val viewport = info.viewportSize.width.toFloat()
            val visible = info.visibleItemsInfo
            if (visible.isEmpty() || viewport <= 0f) return@derivedStateOf 0f
            val itemSize = visible.first().size.toFloat()
            val spacing = if (visible.size >= 2) {
                (visible[1].offset - visible[0].offset - visible[0].size).toFloat()
            } else {
                0f
            }
            val stride = itemSize + spacing
            if (stride <= 0f) return@derivedStateOf 0f
            val contentSize = stride * totalItems - spacing
            val maxScroll = (contentSize - viewport).coerceAtLeast(1f)
            val scrolled = listState.firstVisibleItemIndex * stride +
                listState.firstVisibleItemScrollOffset
            (scrolled / maxScroll).coerceIn(0f, 1f)
        }
    }

    // Solid mid-slate track, clearly darker than the orders panel background.
    val trackColor = Color(0xFF64748B)

    Canvas(
        modifier.height(3.dp),
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas
        val radius = CornerRadius(h / 2f, h / 2f)
        drawRoundRect(
            color = trackColor,
            size = Size(w, h),
            cornerRadius = radius,
        )
        val thumbWidth = (w * CarouselIndicatorThumbFractionOfTrack).coerceAtLeast(h)
        val thumbX = (w - thumbWidth) * positionFraction
        drawRoundRect(
            color = Blue600,
            topLeft = Offset(thumbX, 0f),
            size = Size(thumbWidth, h),
            cornerRadius = radius,
        )
    }
}
