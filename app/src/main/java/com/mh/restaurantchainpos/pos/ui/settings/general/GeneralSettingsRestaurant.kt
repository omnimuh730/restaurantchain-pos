package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainpos.pos.ui.layout.responsive.rememberIsMobile
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import kotlin.math.roundToInt

@Composable
internal fun RestaurantInfoCard(
    colors: PosColors,
    deposit: String,
    onDepositChange: (String) -> Unit,
    depositCurrency: CurrencyKind,
    onCurrencyChange: (CurrencyKind) -> Unit,
    grace: String,
    onGraceChange: (String) -> Unit,
) {
    SettingCard(
        colors = colors,
        title = stringResource(R.string.settings_gen_info_title),
        subtitle = stringResource(R.string.settings_gen_info_subtitle),
        badge = stringResource(R.string.settings_tier_free),
        badgeIcon = Icons.Outlined.Image,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                Text(stringResource(R.string.settings_gen_restaurant_name), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.settings_gen_demo_restaurant_name), color = colors.text, fontSize = 14.sp)
            }
            Column {
                Text(stringResource(R.string.settings_gen_description), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.settings_gen_demo_description),
                    color = colors.textMuted,
                    fontSize = 13.sp,
                )
            }
            Column {
                Text(stringResource(R.string.settings_gen_deposit_money), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    SettingTextField(
                        colors = colors,
                        value = deposit,
                        onChange = onDepositChange,
                        leadingText = if (depositCurrency == CurrencyKind.Foreign) "$" else "₩",
                        leadingTint = if (depositCurrency == CurrencyKind.Foreign) Red500 else Blue600,
                        keyboard = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                    CurrencySwitch(currency = depositCurrency, onChange = onCurrencyChange)
                }
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.settings_gen_deposit_hint), color = colors.textMuted, fontSize = 11.sp)
            }
            Column {
                Text(stringResource(R.string.settings_gen_grace_period), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                SettingTextField(
                    colors = colors,
                    value = grace,
                    onChange = onGraceChange,
                    leadingIcon = Icons.Outlined.Timer,
                    trailingText = stringResource(R.string.settings_gen_min_suffix),
                    keyboard = KeyboardType.Number,
                )
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.settings_gen_grace_hint), color = colors.textMuted, fontSize = 11.sp)
            }
            RestaurantImageGallery(colors)
        }
    }
}

@Composable
internal fun CurrencySwitch(currency: CurrencyKind, onChange: (CurrencyKind) -> Unit) {
    val density = LocalDensity.current
    val trackWidth = 140.dp
    val trackHeight = 44.dp
    val pillSize = 36.dp
    val padding = 4.dp
    val maxOffsetPx = with(density) { (trackWidth - pillSize - padding * 2).toPx() }
    val thresholdPx = with(density) { 45.dp.toPx() }

    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val basePos = if (currency == CurrencyKind.Foreign) maxOffsetPx else 0f
    val pillX = (basePos + dragOffset).coerceIn(0f, maxOffsetPx)
    val animatedPillX by animateFloatAsState(
        targetValue = pillX,
        animationSpec = if (isDragging) {
            androidx.compose.animation.core.snap()
        } else {
            spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessMedium,
            )
        },
        label = "currency-pill",
    )

    Box(
        Modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(Blue600)
            .pointerInput(currency) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isDragging = true
                        dragOffset = 0f
                    },
                    onDragEnd = {
                        when {
                            currency == CurrencyKind.Foreign && dragOffset < -thresholdPx ->
                                onChange(CurrencyKind.Domestic)
                            currency == CurrencyKind.Domestic && dragOffset > thresholdPx ->
                                onChange(CurrencyKind.Foreign)
                        }
                        dragOffset = 0f
                        isDragging = false
                    },
                    onDragCancel = {
                        dragOffset = 0f
                        isDragging = false
                    },
                    onHorizontalDrag = { _, dx ->
                        val proposed = dragOffset + dx
                        dragOffset = when (currency) {
                            CurrencyKind.Foreign -> proposed.coerceIn(-maxOffsetPx, 0f)
                            CurrencyKind.Domestic -> proposed.coerceIn(0f, maxOffsetPx)
                        }
                    },
                )
            }
            .padding(padding),
    ) {
        val labelOnLeft = currency == CurrencyKind.Foreign
        Text(
            text = if (currency == CurrencyKind.Foreign) {
                stringResource(R.string.settings_gen_currency_foreign)
            } else {
                stringResource(R.string.settings_gen_currency_domestic)
            },
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(if (labelOnLeft) Alignment.CenterStart else Alignment.CenterEnd)
                .padding(horizontal = 12.dp),
        )
        Box(
            Modifier
                .offset { IntOffset(animatedPillX.roundToInt(), 0) }
                .size(pillSize)
                .clip(RoundedCornerShape(7.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            val sym = if (currency == CurrencyKind.Foreign) "₩" else "$"
            val symColor = if (currency == CurrencyKind.Foreign) Blue600 else Red500
            Text(sym, color = symColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun RestaurantImageGallery(colors: PosColors) {
    val images = remember {
        listOf(
            Color(0xFF334155),
            Color(0xFF1F2937),
            Color(0xFF374151),
            Color(0xFF475569),
        )
    }
    var index by remember { mutableIntStateOf(0) }
    var showAll by remember { mutableStateOf(false) }
    var dragOffsetPx by remember { mutableStateOf(0f) }
    val isMobile = rememberIsMobile()
    val scope = rememberCoroutineScope()

    if (showAll) {
        Dialog(
            onDismissRequest = { showAll = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
        ) {
            val safeIndex = index.coerceIn(0, images.lastIndex)
            val pagerState = rememberPagerState(
                initialPage = safeIndex,
                pageCount = { images.size },
            )
            LaunchedEffect(pagerState.settledPage) {
                if (index != pagerState.settledPage) {
                    index = pagerState.settledPage
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.settings_gen_images_nav, pagerState.currentPage + 1, images.size),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { showAll = false }) {
                        Text(stringResource(R.string.settings_gen_hide), color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) { page ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = if (isMobile) 0.dp else 40.dp,
                                vertical = if (isMobile) 0.dp else 12.dp,
                            )
                            .clip(RoundedCornerShape(if (isMobile) 0.dp else 12.dp))
                            .background(images[page]),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.Image,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(80.dp),
                        )
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    images.forEachIndexed { i, _ ->
                        Box(
                            Modifier
                                .padding(horizontal = 3.dp)
                                .height(8.dp)
                                .width(if (i == pagerState.currentPage) 18.dp else 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (i == pagerState.currentPage) Blue600 else Color.White.copy(alpha = 0.28f))
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(i)
                                    }
                                },
                        )
                    }
                }
            }
        }
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.settings_gen_images_nav, index + 1, images.size),
                color = colors.text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceRaised)
                    .clickable { showAll = !showAll }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    if (showAll) stringResource(R.string.settings_gen_hide) else stringResource(R.string.settings_gen_show_all),
                    color = colors.text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        if (!showAll) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .pointerInput(images.size) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val threshold = 80f
                                if (dragOffsetPx < -threshold) index = (index + 1) % images.size
                                else if (dragOffsetPx > threshold) index = (index - 1 + images.size) % images.size
                                dragOffsetPx = 0f
                            },
                            onDragCancel = { dragOffsetPx = 0f },
                            onHorizontalDrag = { _, dx -> dragOffsetPx += dx },
                        )
                    },
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(images[index]),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Image,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(56.dp),
                    )
                }
                Box(
                    Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { index = (index - 1 + images.size) % images.size }
                        .size(34.dp)
                        .align(Alignment.CenterStart),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Box(
                    Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { index = (index + 1) % images.size }
                        .size(34.dp)
                        .align(Alignment.CenterEnd),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                images.forEachIndexed { i, _ ->
                    Box(
                        Modifier
                            .padding(horizontal = 3.dp)
                            .height(8.dp)
                            .width(if (i == index) 18.dp else 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i == index) Blue600 else colors.border)
                            .clickable { index = i },
                    )
                }
            }
        }
    }
}
