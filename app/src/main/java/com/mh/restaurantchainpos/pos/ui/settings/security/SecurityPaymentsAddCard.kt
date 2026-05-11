package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlinx.coroutines.delay

@Composable
internal fun AddCardDialog(
    colors: PosColors,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var mode by remember { mutableStateOf("type") }
    var cardNumber by remember { mutableStateOf("") }
    var qrScanning by remember { mutableStateOf(false) }
    var qrScanned by remember { mutableStateOf(false) }
    var qrProgress by remember { mutableIntStateOf(0) }

    fun resetQrState() {
        qrScanning = false
        qrScanned = false
        qrProgress = 0
    }

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

            Column(
                Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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
