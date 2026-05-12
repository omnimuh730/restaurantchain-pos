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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue500
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors

@Composable
internal fun ModeTab(
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
internal fun QrScannerPanel(
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
                    Box(Modifier.fillMaxSize().padding(24.dp)) {
                        ScanCorner(Modifier.align(Alignment.TopStart), topLeft = true)
                        ScanCorner(Modifier.align(Alignment.TopEnd), topRight = true)
                        ScanCorner(Modifier.align(Alignment.BottomStart), bottomLeft = true)
                        ScanCorner(Modifier.align(Alignment.BottomEnd), bottomRight = true)
                    }
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
internal fun ScanCorner(
    modifier: Modifier,
    topLeft: Boolean = false,
    topRight: Boolean = false,
    bottomLeft: Boolean = false,
    bottomRight: Boolean = false,
) {
    val thickness = 2.dp
    val len = 18.dp
    Box(modifier.size(len)) {
        Box(
            Modifier
                .size(width = len, height = thickness)
                .background(Blue500)
                .align(if (topLeft || topRight) Alignment.TopStart else Alignment.BottomStart)
                .padding(start = 0.dp),
        )
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
