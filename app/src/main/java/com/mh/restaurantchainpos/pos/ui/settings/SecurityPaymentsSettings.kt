package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.PaymentCard
import com.mh.restaurantchainpos.pos.data.PosMockData
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.Red500
import kotlinx.coroutines.delay

@Composable
fun SecurityPaymentsSettings(colors: PosColors, passwordOnly: Boolean = false) {
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var passwordUpdated by remember { mutableStateOf(false) }
    var floorPlanAlert by remember { mutableStateOf(true) }
    var kitchenAlert by remember { mutableStateOf(true) }
    val cards = remember { mutableStateListOf(*PosMockData.paymentCards.toTypedArray()) }
    var selectedCardId by remember { mutableStateOf(cards.firstOrNull { it.isDefault }?.last4 ?: cards.firstOrNull()?.last4) }
    var addCardOpen by remember { mutableStateOf(false) }

    val mismatch = confirm.isNotEmpty() && newPass != confirm
    val canSubmit = newPass.isNotEmpty() && newPass == confirm

    if (passwordUpdated) {
        LaunchedEffect("password-updated") {
            delay(3000)
            passwordUpdated = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingCard(
            colors = colors,
            title = "Password Settings",
            subtitle = "Update your manager PIN or account password",
            headerIcon = Icons.Outlined.Shield,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Column {
                    Text("New Password", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    SettingTextField(
                        colors = colors,
                        value = newPass,
                        onChange = { newPass = it; passwordUpdated = false },
                        placeholder = "Enter new password",
                        leadingIcon = Icons.Outlined.Lock,
                        trailingIcon = if (showNew) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        keyboard = if (showNew) androidx.compose.ui.text.input.KeyboardType.Text else androidx.compose.ui.text.input.KeyboardType.Password,
                    )
                }
                Column {
                    Text("Confirm Password", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    SettingTextField(
                        colors = colors,
                        value = confirm,
                        onChange = { confirm = it; passwordUpdated = false },
                        placeholder = "Confirm new password",
                        leadingIcon = Icons.Outlined.LockOpen,
                        trailingIcon = if (showConfirm) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        keyboard = if (showConfirm) androidx.compose.ui.text.input.KeyboardType.Text else androidx.compose.ui.text.input.KeyboardType.Password,
                    )
                    if (mismatch) {
                        Spacer(Modifier.height(4.dp))
                        Text("Passwords do not match", color = Red500, fontSize = 11.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PrimaryButton(
                        "Update Password",
                        onClick = {
                            if (canSubmit) {
                                passwordUpdated = true
                                newPass = ""
                                confirm = ""
                            }
                        },
                        enabled = canSubmit,
                    )
                    if (passwordUpdated) {
                        Spacer(Modifier.size(8.dp))
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Blue500.copy(alpha = 0.12f))
                                .border(1.dp, Blue500.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Outlined.Check, contentDescription = null, tint = Blue600, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.size(5.dp))
                            Text("Password updated", color = Blue600, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        if (!passwordOnly) {
            SettingCard(
                colors = colors,
                title = "Notifications",
                subtitle = "Toggle toast alerts for incoming activity",
                headerIcon = Icons.Outlined.Notifications,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NotificationRow(
                        colors = colors,
                        icon = Icons.Outlined.GridView,
                        title = "Floor Plan",
                        description = "New reservation requests",
                        checked = floorPlanAlert,
                        onChange = { floorPlanAlert = !floorPlanAlert },
                    )
                    NotificationRow(
                        colors = colors,
                        icon = Icons.Outlined.SoupKitchen,
                        title = "Kitchen",
                        description = "New chef tickets created",
                        checked = kitchenAlert,
                        onChange = { kitchenAlert = !kitchenAlert },
                    )
                }
            }

            SettingCard(
                colors = colors,
                title = "Saved Payment Methods",
                subtitle = "Manage your saved cards for billing",
                headerIcon = Icons.Outlined.CreditCard,
                badge = "${cards.size}",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    cards.forEach { card ->
                        PaymentCardRow(
                            colors = colors,
                            card = card,
                            selected = selectedCardId == card.last4,
                            onSelect = { selectedCardId = card.last4 },
                            onRemove = {
                                cards.remove(card)
                                if (selectedCardId == card.last4) selectedCardId = cards.firstOrNull()?.last4
                            },
                        )
                    }
                    AddNewCardButton(colors = colors) { addCardOpen = true }
                }
            }
        }
    }

    if (addCardOpen) {
        AddCardDialog(
            colors = colors,
            onDismiss = { addCardOpen = false },
            onConfirm = { digits ->
                val last4 = digits.takeLast(4)
                cards.add(PaymentCard("credit card", last4, "12/28", "New Card Holder"))
                addCardOpen = false
            },
        )
    }
}

@Composable
private fun NotificationRow(
    colors: PosColors,
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onChange: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceRaised)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Blue500.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = colors.textMuted, fontSize = 11.sp)
        }
        ToggleSwitch(checked = checked, onChange = onChange)
    }
}

@Composable
private fun PaymentCardRow(
    colors: PosColors,
    card: PaymentCard,
    selected: Boolean,
    onSelect: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Blue500.copy(alpha = 0.08f) else colors.surfaceRaised)
            .border(1.dp, if (selected) Blue600 else colors.border, RoundedCornerShape(10.dp))
            .clickable(onClick = onSelect)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(width = 44.dp, height = 30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Blue600),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("•••• ${card.last4}", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                if (card.isDefault) SmallChip("DEFAULT", Blue600)
            }
            Text("${card.holderName} · exp ${card.expiry}", color = colors.textMuted, fontSize = 11.sp)
        }
        if (selected) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(6.dp))
        }
        Box(
            Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Close, contentDescription = null, tint = Red500, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun AddNewCardButton(colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 2.dp,
                color = colors.border,
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("+ Add New Card", color = colors.textMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

// ─── Add Card Dialog ─────────────────────────────────────
@Composable
private fun AddCardDialog(
    colors: PosColors,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var mode by remember { mutableStateOf("type") } // "type" or "qr"
    var cardNumber by remember { mutableStateOf("") }
    var qrScanning by remember { mutableStateOf(false) }
    var qrScanned by remember { mutableStateOf(false) }
    var qrProgress by remember { mutableIntStateOf(0) }

    fun resetQrState() {
        qrScanning = false
        qrScanned = false
        qrProgress = 0
    }

    // Simulate scanning progress when active.
    if (qrScanning) {
        LaunchedEffect("qr-scan") {
            while (qrProgress < 100 && qrScanning) {
                delay(60L)
                qrProgress += 2
            }
            if (qrProgress >= 100) {
                qrScanning = false
                qrScanned = true
                val random = buildString {
                    repeat(16) { append(('0'..'9').random()) }
                }
                cardNumber = formatCardNumber(random)
            }
        }
    }

    val digits = cardNumber.filter { it.isDigit() }
    val canAdd = digits.length >= 13

    ModalScrim(onDismiss = onDismiss) {
        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 420.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .consumeModalTaps(),
        ) {
            // Header
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = Blue500, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(10.dp))
                Text("Add New Card", color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))

            // Body
            Column(
                Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Mode tabs
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, colors.border, RoundedCornerShape(10.dp)),
                ) {
                    ModeTab(
                        colors = colors,
                        label = "Type Card Number",
                        icon = Icons.Outlined.CreditCard,
                        selected = mode == "type",
                        modifier = Modifier.weight(1f),
                    ) {
                        mode = "type"
                        cardNumber = ""
                        resetQrState()
                    }
                    Box(Modifier.size(width = 1.dp, height = 38.dp).background(colors.border))
                    ModeTab(
                        colors = colors,
                        label = "Scan QR Code",
                        icon = Icons.Outlined.QrCode,
                        selected = mode == "qr",
                        modifier = Modifier.weight(1f),
                    ) {
                        mode = "qr"
                        cardNumber = ""
                        resetQrState()
                    }
                }

                if (mode == "type") {
                    Column {
                        Text("Card Number", color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        SettingTextField(
                            colors = colors,
                            value = cardNumber,
                            onChange = { cardNumber = formatCardNumber(it) },
                            placeholder = "0000 0000 0000 0000",
                            keyboard = androidx.compose.ui.text.input.KeyboardType.Number,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Enter your 16-digit card number", color = colors.textMuted, fontSize = 11.sp)
                    }
                } else {
                    QrScannerPanel(
                        colors = colors,
                        scanning = qrScanning,
                        scanned = qrScanned,
                        progress = qrProgress,
                        cardNumber = cardNumber,
                        onStart = {
                            resetQrState()
                            qrScanning = true
                        },
                        onCancel = { resetQrState(); cardNumber = "" },
                        onScanAgain = {
                            resetQrState()
                            cardNumber = ""
                        },
                    )
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlineButton("Cancel", colors.text, onClick = onDismiss)
                Spacer(Modifier.size(8.dp))
                PrimaryButton(
                    label = "Add Card",
                    onClick = { if (canAdd) onConfirm(digits) },
                    enabled = canAdd,
                )
            }
        }
    }
}

@Composable
private fun ModeTab(
    colors: PosColors,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg = if (selected) Blue600 else Color.Transparent
    val fg = if (selected) Color.White else colors.textMuted
    Row(
        modifier
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(14.dp))
        Spacer(Modifier.size(6.dp))
        Text(label, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QrScannerPanel(
    colors: PosColors,
    scanning: Boolean,
    scanned: Boolean,
    progress: Int,
    cardNumber: String,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onScanAgain: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Viewport
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF111827))
                .border(
                    2.dp,
                    when {
                        scanning -> Blue500
                        scanned -> Blue500
                        else -> colors.border
                    },
                    RoundedCornerShape(12.dp),
                ),
        ) {
            when {
                scanning -> {
                    // Scan brackets
                    Box(Modifier.fillMaxSize().padding(24.dp)) {
                        // Corner brackets
                        ScanCorner(Modifier.align(Alignment.TopStart), topLeft = true)
                        ScanCorner(Modifier.align(Alignment.TopEnd), topRight = true)
                        ScanCorner(Modifier.align(Alignment.BottomStart), bottomLeft = true)
                        ScanCorner(Modifier.align(Alignment.BottomEnd), bottomRight = true)
                    }
                    // Scan line — position proportional to progress
                    val frac = (progress.coerceIn(0, 100)) / 100f
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = (24 + frac * 160).dp)
                            .height(2.dp)
                            .background(Blue500),
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xCC000000))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            "Scanning... $progress%",
                            color = Blue500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                scanned -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Blue600),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Card scanned successfully", color = Blue500, fontSize = 12.sp)
                    }
                }
                else -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Blue500.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = Blue500, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Position QR code within the scanner frame",
                            color = Color(0xFF9CA3AF),
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }

        if (scanned && cardNumber.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Blue500.copy(alpha = 0.10f))
                    .border(1.dp, Blue500.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = Blue600, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(8.dp))
                Text(
                    cardNumber,
                    color = Blue600,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        when {
            scanning -> OutlineButton(
                label = "Cancel Scan",
                color = colors.text,
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
            )
            scanned -> OutlineButton(
                label = "Scan Again",
                color = colors.text,
                onClick = onScanAgain,
                modifier = Modifier.fillMaxWidth(),
            )
            else -> PrimaryButton(
                label = "Start Scanning",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Outlined.QrCodeScanner,
            )
        }
    }
}

@Composable
private fun ScanCorner(
    modifier: Modifier,
    topLeft: Boolean = false,
    topRight: Boolean = false,
    bottomLeft: Boolean = false,
    bottomRight: Boolean = false,
) {
    val thickness = 2.dp
    val len = 18.dp
    Box(modifier.size(len)) {
        // Horizontal bar
        Box(
            Modifier
                .size(width = len, height = thickness)
                .background(Blue500)
                .align(if (topLeft || topRight) Alignment.TopStart else Alignment.BottomStart)
                .padding(start = 0.dp),
        )
        // Vertical bar
        Box(
            Modifier
                .size(width = thickness, height = len)
                .background(Blue500)
                .align(
                    when {
                        topLeft -> Alignment.TopStart
                        topRight -> Alignment.TopEnd
                        bottomLeft -> Alignment.BottomStart
                        bottomRight -> Alignment.BottomEnd
                        else -> Alignment.TopStart
                    },
                ),
        )
    }
}

private fun formatCardNumber(input: String): String {
    val digits = input.filter { it.isDigit() }.take(16)
    return digits.chunked(4).joinToString(" ")
}
