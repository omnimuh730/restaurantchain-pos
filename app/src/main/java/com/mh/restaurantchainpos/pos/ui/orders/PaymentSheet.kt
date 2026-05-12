package com.mh.restaurantchainpos.pos.ui.orders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CallSplit
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.i18n.ordersPaymentMethodLabel
import com.mh.restaurantchainpos.pos.ui.theme.Blue50
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlinx.coroutines.launch

private val UsdRed = Color(0xFFDC2626)

/** Light POS shell uses white `surface`; dark uses charcoal — drives payment method tile chrome. */
private val PaymentMethodTilesLightSurface = Color(0xFFFFFFFF)

private fun PosColors.paymentMethodTilesLightChrome(): Boolean = surface == PaymentMethodTilesLightSurface

private enum class PayMethod(val id: String, val icon: ImageVector) {
    Cash("cash", Icons.Outlined.AccountBalanceWallet),
    Credit("credit", Icons.Outlined.CreditCard),
    Mix("mix", Icons.Outlined.CallSplit),
}

@Composable
fun PaymentSheet(
    colors: PosColors,
    totalUsd: Double,
    totalKrw: Double,
    checkNumber: String,
    tableLabel: String,
    onClose: () -> Unit,
) {
    var method by remember { mutableStateOf(PayMethod.Cash) }
    var done by remember { mutableStateOf(false) }
    var cardNumber by remember { mutableStateOf("") }
    var cardPassword by remember { mutableStateOf("") }
    var cashKrw by remember { mutableStateOf("") }
    var cashUsd by remember { mutableStateOf("") }
    var creditKrw by remember { mutableStateOf(if (totalKrw > 0) totalKrw.toLong().toString() else "") }
    var creditUsd by remember { mutableStateOf(if (totalUsd > 0) "%.2f".format(totalUsd) else "") }

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        PaymentHeader(
            colors = colors,
            tableLabel = tableLabel,
            checkNumber = checkNumber,
            onBack = onClose,
        )

        TotalLine(
            totalKrw = totalKrw,
            totalUsd = totalUsd,
            colors = colors,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 16.dp),
        )

        if (done) {
            DoneState(
                colors = colors,
                method = method,
                totalKrw = totalKrw,
                totalUsd = totalUsd,
                onDone = onClose,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        } else {
            MethodTiles(
                colors = colors,
                selected = method,
                onSelect = { method = it },
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                when (method) {
                    PayMethod.Cash -> CashContent()
                    PayMethod.Credit -> CreditContent(
                        colors = colors,
                        cardNumber = cardNumber,
                        onCardNumber = { cardNumber = it.take(19) },
                        cardPassword = cardPassword,
                        onCardPassword = { cardPassword = it.take(8) },
                        onProceed = { done = true },
                    )
                    PayMethod.Mix -> MixContent(
                        colors = colors,
                        cashKrw = cashKrw,
                        onCashKrw = { cashKrw = it.filter(Char::isDigit) },
                        cashUsd = cashUsd,
                        onCashUsd = { cashUsd = it.filterAmount() },
                        creditKrw = creditKrw,
                        onCreditKrw = { creditKrw = it.filter(Char::isDigit) },
                        creditUsd = creditUsd,
                        onCreditUsd = { creditUsd = it.filterAmount() },
                    )
                }
            }

            ConfirmPaymentButton(
                onClick = { done = true },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Header                                                         */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun PaymentHeader(
    colors: PosColors,
    tableLabel: String,
    checkNumber: String,
    onBack: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.orders_cd_back),
                tint = colors.text,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(R.string.orders_pay_title), color = colors.text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "$tableLabel · $checkNumber",
                color = colors.textMuted,
                fontSize = 12.sp,
            )
        }
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Total line: ₩18,000 + $65.00                                  */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun TotalLine(
    totalKrw: Double,
    totalUsd: Double,
    colors: PosColors,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (totalKrw > 0) {
            Text(
                text = "\u20A9" + "%,.0f".format(totalKrw),
                color = Blue600,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        if (totalKrw > 0 && totalUsd > 0) {
            Text("+", color = colors.textMuted, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        if (totalUsd > 0) {
            Text(
                text = "$" + "%,.2f".format(totalUsd),
                color = UsdRed,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Method tiles: Cash / Credit / Mix                              */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun MethodTiles(
    colors: PosColors,
    selected: PayMethod,
    onSelect: (PayMethod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PayMethod.entries.forEach { entry ->
            MethodTile(
                colors = colors,
                method = entry,
                selected = entry == selected,
                onClick = { onSelect(entry) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MethodTile(
    colors: PosColors,
    method: PayMethod,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lightChrome = colors.paymentMethodTilesLightChrome()
    val borderColor = if (selected) Blue500 else colors.border
    val bg = when {
        selected && lightChrome -> Blue50
        else -> colors.surface
    }
    val fg = when {
        selected -> if (lightChrome) Blue600 else Blue500
        else -> if (lightChrome) colors.text else colors.textMuted
    }
    Column(
        modifier = modifier
            .height(86.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(if (selected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val label = ordersPaymentMethodLabel(method.id)
        Icon(
            imageVector = method.icon,
            contentDescription = label,
            tint = fg,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = label,
            color = fg,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Cash content (intentionally empty body, like reference)        */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun CashContent() {
    // The reference shows a decorative illustration here. Keep the area
    // intentionally empty so the focus stays on the totals and "Confirm".
    Box(Modifier.fillMaxSize())
}

/* ────────────────────────────────────────────────────────────── */
/* Credit content: swipeable QR ↔ Card form                       */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun CreditContent(
    colors: PosColors,
    cardNumber: String,
    onCardNumber: (String) -> Unit,
    cardPassword: String,
    onCardPassword: (String) -> Unit,
    onProceed: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            pageSpacing = 16.dp,
        ) { page ->
            when (page) {
                0 -> CreditQrPage(colors = colors)
                else -> CreditCardFormPage(
                    colors = colors,
                    cardNumber = cardNumber,
                    onCardNumber = onCardNumber,
                    cardPassword = cardPassword,
                    onCardPassword = onCardPassword,
                    onProceed = onProceed,
                )
            }
        }

        PageIndicator(
            current = pagerState.currentPage,
            total = 2,
            colors = colors,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        val onPagerSwipe: (Int) -> Unit = { target ->
            scope.launch { pagerState.animateScrollToPage(target) }
        }
        if (pagerState.currentPage == 0) {
            Text(
                text = "Swipe to card payment  \u00BB",
                color = Blue500,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onPagerSwipe(1) }
                    .padding(8.dp),
            )
        } else {
            Text(
                text = "\u00AB  Swipe back to QR",
                color = Blue500,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onPagerSwipe(0) }
                    .padding(8.dp),
            )
        }
    }
}

@Composable
private fun CreditQrPage(colors: PosColors) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BoxWithConstraints {
            val side = (minOf(maxWidth, maxHeight)).coerceAtMost(260.dp)
            Box(
                Modifier
                    .size(side)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                    .padding(14.dp),
            ) {
                QrCanvas(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun CreditCardFormPage(
    colors: PosColors,
    cardNumber: String,
    onCardNumber: (String) -> Unit,
    cardPassword: String,
    onCardPassword: (String) -> Unit,
    onProceed: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.orders_pay_card_number), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            BorderedTextField(
                value = cardNumber,
                onValueChange = onCardNumber,
                placeholder = "****-****-****-****",
                colors = colors,
                trailing = {
                    Icon(
                        imageVector = Icons.Outlined.QrCode2,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(18.dp),
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Text(stringResource(R.string.orders_pay_card_password), color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            BorderedTextField(
                value = cardPassword,
                onValueChange = onCardPassword,
                placeholder = "********",
                colors = colors,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            )
            val proceedEnabled = cardNumber.isNotBlank() && cardPassword.isNotBlank()
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (proceedEnabled) Blue600 else Blue500.copy(alpha = 0.45f))
                    .clickable(enabled = proceedEnabled, onClick = onProceed),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Proceed",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Mix content: Cash / Credit split + QR                          */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun MixContent(
    colors: PosColors,
    cashKrw: String,
    onCashKrw: (String) -> Unit,
    cashUsd: String,
    onCashUsd: (String) -> Unit,
    creditKrw: String,
    onCreditKrw: (String) -> Unit,
    creditUsd: String,
    onCreditUsd: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, colors.border, RoundedCornerShape(14.dp)),
        ) {
            MixPaymentColumn(
                colors = colors,
                title = "Cash",
                icon = Icons.Outlined.AccountBalanceWallet,
                krw = cashKrw,
                onKrw = onCashKrw,
                usd = cashUsd,
                onUsd = onCashUsd,
                krwPlaceholder = "0",
                usdPlaceholder = "0.00",
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
            )
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(colors.border),
            )
            MixPaymentColumn(
                colors = colors,
                title = "Credit",
                icon = Icons.Outlined.CreditCard,
                krw = creditKrw,
                onKrw = onCreditKrw,
                usd = creditUsd,
                onUsd = onCreditUsd,
                krwPlaceholder = "0",
                usdPlaceholder = "0.00",
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            BoxWithConstraints {
                val side = (minOf(maxWidth, maxHeight)).coerceAtMost(220.dp)
                Box(
                    Modifier
                        .size(side)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                ) {
                    QrCanvas(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun MixPaymentColumn(
    colors: PosColors,
    title: String,
    icon: ImageVector,
    krw: String,
    onKrw: (String) -> Unit,
    usd: String,
    onUsd: (String) -> Unit,
    krwPlaceholder: String,
    usdPlaceholder: String,
    modifier: Modifier = Modifier,
) {
    val placeholderTint = colors.textMuted.copy(alpha = 0.55f)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.text,
                modifier = Modifier.size(16.dp),
            )
            Text(title, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        BorderedTextField(
            value = krw,
            onValueChange = onKrw,
            placeholder = krwPlaceholder,
            colors = colors,
            leading = { Text("\u20A9", color = Blue600, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
            valueColor = Blue600,
            placeholderColor = placeholderTint,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        BorderedTextField(
            value = usd,
            onValueChange = onUsd,
            placeholder = usdPlaceholder,
            colors = colors,
            leading = { Text("$", color = UsdRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
            valueColor = UsdRed,
            placeholderColor = placeholderTint,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Done state                                                     */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun DoneState(
    colors: PosColors,
    method: PayMethod,
    totalKrw: Double,
    totalUsd: Double,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(1.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Blue600),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                stringResource(R.string.orders_pay_complete),
                color = colors.text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(ordersPaymentMethodLabel(method.id), color = colors.textMuted, fontSize = 13.sp)
                if (totalKrw > 0) {
                    Text(
                        "\u20A9" + "%,.0f".format(totalKrw),
                        color = Blue600,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (totalKrw > 0 && totalUsd > 0) {
                    Text("+", color = colors.textMuted, fontSize = 13.sp)
                }
                if (totalUsd > 0) {
                    Text(
                        "$" + "%,.2f".format(totalUsd),
                        color = UsdRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(bottom = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Blue600)
                .clickable(onClick = onDone),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.orders_pay_done), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Confirm Payment button                                         */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun ConfirmPaymentButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Blue600)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "Confirm Payment",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Page indicator (small dots)                                    */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun PageIndicator(current: Int, total: Int, colors: PosColors, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { index ->
            val active = index == current
            Box(
                Modifier
                    .size(if (active) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (active) Blue500 else colors.border),
            )
        }
    }
}

/* ────────────────────────────────────────────────────────────── */
/* Bordered text field used across credit / mix forms             */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun BorderedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    colors: PosColors,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    valueColor: Color = colors.text,
    placeholderColor: Color? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
) {
    val resolvedPlaceholderColor = placeholderColor ?: colors.textMuted.copy(alpha = 0.7f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (leading != null) leading()
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(Blue500),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                textStyle = TextStyle(
                    color = valueColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isBlank()) {
                            Text(
                                placeholder,
                                color = resolvedPlaceholderColor,
                                fontSize = 13.sp,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            )
                        }
                        inner()
                    }
                },
            )
            if (trailing != null) trailing()
        }
    }
}

/* ────────────────────────────────────────────────────────────── */
/* QR canvas (shared by Credit + Mix views)                       */
/* ────────────────────────────────────────────────────────────── */

@Composable
private fun QrCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val cells = 21
        val cell = size.minDimension / cells
        // Quiet zone clear background
        drawRect(Color.White, Offset.Zero, Size(size.width, size.height))
        // Pseudo-random module pattern that "looks" like a QR code.
        for (x in 0 until cells) {
            for (y in 0 until cells) {
                val v = ((x * 31 + y * 17 + x * y) % 7)
                if (v < 3) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(x * cell, y * cell),
                        size = Size(cell, cell),
                    )
                }
            }
        }
        // Three positioning markers (corners).
        finderSquare(cells, cell, 0, 0)
        finderSquare(cells, cell, cells - 7, 0)
        finderSquare(cells, cell, 0, cells - 7)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.finderSquare(
    cells: Int,
    cell: Float,
    cx: Int,
    cy: Int,
) {
    val outer = Size(cell * 7, cell * 7)
    drawRect(Color.White, Offset(cx * cell, cy * cell), outer)
    drawRect(Color.Black, Offset(cx * cell, cy * cell), outer)
    drawRect(Color.White, Offset((cx + 1) * cell, (cy + 1) * cell), Size(cell * 5, cell * 5))
    drawRect(Color.Black, Offset((cx + 2) * cell, (cy + 2) * cell), Size(cell * 3, cell * 3))
}

/* ────────────────────────────────────────────────────────────── */
/* Helpers                                                        */
/* ────────────────────────────────────────────────────────────── */

private fun String.filterAmount(): String {
    // Allow digits and a single decimal point, max 2 fractional digits.
    val cleaned = StringBuilder()
    var sawDot = false
    var fractional = 0
    for (ch in this) {
        when {
            ch.isDigit() -> {
                if (sawDot) {
                    if (fractional < 2) {
                        cleaned.append(ch); fractional++
                    }
                } else {
                    cleaned.append(ch)
                }
            }
            ch == '.' && !sawDot -> { cleaned.append(ch); sawDot = true }
        }
    }
    return cleaned.toString()
}
