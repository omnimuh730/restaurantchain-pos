package com.mh.restaurantchainpos.pos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import kotlinx.coroutines.delay

enum class PosNotificationKind { Success, Error, Warning, Info, Default }

data class PosNotificationAction(
    val label: String,
    val onClick: () -> Unit,
)

internal data class PosNotificationItem(
    val id: String,
    val kind: PosNotificationKind,
    val title: String,
    val message: String?,
    val durationMillis: Long,
    val action: PosNotificationAction?,
)

class PosNotificationHostState internal constructor() {
    internal val notifications = mutableStateListOf<PosNotificationItem>()

    fun show(
        title: String,
        message: String? = null,
        kind: PosNotificationKind = PosNotificationKind.Default,
        durationMillis: Long = 4200L,
        action: PosNotificationAction? = null,
    ) {
        val id = "notification-${System.nanoTime()}"
        notifications.add(
            0,
            PosNotificationItem(
                id = id,
                kind = kind,
                title = title,
                message = message,
                durationMillis = durationMillis,
                action = action,
            ),
        )
        while (notifications.size > 3) notifications.removeAt(notifications.lastIndex)
    }

    fun success(title: String, message: String? = null, durationMillis: Long = 4200L) {
        show(title = title, message = message, kind = PosNotificationKind.Success, durationMillis = durationMillis)
    }

    fun info(title: String, message: String? = null, durationMillis: Long = 4200L) {
        show(title = title, message = message, kind = PosNotificationKind.Info, durationMillis = durationMillis)
    }

    fun dismiss(id: String) {
        notifications.removeAll { it.id == id }
    }
}

@Composable
fun rememberPosNotificationHostState(): PosNotificationHostState = remember { PosNotificationHostState() }

@Composable
fun PosNotificationHost(
    state: PosNotificationHostState,
    colors: PosColors,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopCenter,
) {
    Box(
        modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = alignment,
    ) {
        Column(
            Modifier.widthIn(max = 420.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            state.notifications.forEach { notification ->
                PosNotificationCard(
                    notification = notification,
                    colors = colors,
                    onDismiss = { state.dismiss(notification.id) },
                )
            }
        }
    }
}

@Composable
private fun PosNotificationCard(
    notification: PosNotificationItem,
    colors: PosColors,
    onDismiss: () -> Unit,
) {
    var visible by remember(notification.id) { mutableStateOf(false) }
    var dismissRequested by remember(notification.id) { mutableStateOf(false) }
    val spec = notification.kind.visuals(colors)

    LaunchedEffect(notification.id) {
        visible = true
        delay(notification.durationMillis)
        dismissRequested = true
        visible = false
    }

    LaunchedEffect(dismissRequested) {
        if (dismissRequested) {
            delay(220)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(160)) + slideInVertically(tween(260)) { -it / 2 },
        exit = fadeOut(tween(160)) + slideOutVertically(tween(220)) { -it / 2 },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .shadow(18.dp, RoundedCornerShape(22.dp), clip = false)
                .clip(RoundedCornerShape(22.dp))
                .background(colors.surface.copy(alpha = 0.96f))
                .border(1.dp, colors.border.copy(alpha = 0.72f), RoundedCornerShape(22.dp)),
        ) {
            Box(
                Modifier
                    .offset(x = (-34).dp, y = (-42).dp)
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(spec.accent.copy(alpha = 0.16f)),
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Brush.horizontalGradient(listOf(spec.accent, spec.endAccent))),
            )
            Row(
                Modifier.padding(start = 14.dp, top = 14.dp, end = 10.dp, bottom = 14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(spec.accent, spec.endAccent))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(spec.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f).padding(top = 1.dp)) {
                    Text(
                        notification.title,
                        color = colors.text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!notification.message.isNullOrBlank()) {
                        Spacer(Modifier.height(3.dp))
                        Text(
                            notification.message,
                            color = colors.textMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    notification.action?.let { action ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            action.label,
                            color = Blue600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                action.onClick()
                                dismissRequested = true
                                visible = false
                            },
                        )
                    }
                }
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceRaised)
                        .clickable {
                            dismissRequested = true
                            visible = false
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = "Dismiss notification", tint = colors.textMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private data class NotificationVisuals(
    val icon: ImageVector,
    val accent: Color,
    val endAccent: Color,
)

private fun PosNotificationKind.visuals(colors: PosColors): NotificationVisuals = when (this) {
    PosNotificationKind.Success -> NotificationVisuals(Icons.Outlined.CheckCircle, Blue600, Color(0xFF22C55E))
    PosNotificationKind.Error -> NotificationVisuals(Icons.Outlined.ErrorOutline, Color(0xFFEF4444), Color(0xFFF97316))
    PosNotificationKind.Warning -> NotificationVisuals(Icons.Outlined.WarningAmber, Color(0xFFF59E0B), Color(0xFFF97316))
    PosNotificationKind.Info -> NotificationVisuals(Icons.Outlined.Info, colors.text, Blue600)
    PosNotificationKind.Default -> NotificationVisuals(Icons.Outlined.AutoAwesome, colors.text, Blue600)
}
