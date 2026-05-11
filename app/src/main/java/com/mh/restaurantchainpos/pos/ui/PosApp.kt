package com.mh.restaurantchainpos.pos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.AuthRoute
import com.mh.restaurantchainpos.pos.data.AuthSession
import com.mh.restaurantchainpos.pos.data.PosPage
import com.mh.restaurantchainpos.pos.data.roleNavAccess
import com.mh.restaurantchainpos.pos.ui.auth.LockScreen
import com.mh.restaurantchainpos.pos.ui.auth.SignInScreen
import com.mh.restaurantchainpos.pos.ui.auth.SignUpScreen
import com.mh.restaurantchainpos.pos.ui.screens.AnalyticsScreen
import com.mh.restaurantchainpos.pos.ui.screens.FloorPlanScreen
import com.mh.restaurantchainpos.pos.ui.screens.KitchenScreen
import com.mh.restaurantchainpos.pos.ui.screens.OrdersScreen
import com.mh.restaurantchainpos.pos.ui.screens.SettingsScreen
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.DarkPosColors
import com.mh.restaurantchainpos.pos.ui.theme.LightPosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosColors
import com.mh.restaurantchainpos.pos.ui.theme.PosDimens
import com.mh.restaurantchainpos.pos.ui.theme.posBackground

@Composable
fun PosApp() {
    var route by remember { mutableStateOf(AuthRoute.SignIn) }
    var session by remember { mutableStateOf(AuthSession()) }
    when (route) {
        AuthRoute.SignIn -> SignInScreen(
            onSignedIn = { s -> session = s; route = AuthRoute.Pos },
            onSignUp = { route = AuthRoute.SignUp },
        )
        AuthRoute.SignUp -> SignUpScreen(
            onSignedUp = { s -> session = s; route = AuthRoute.Pos },
            onSignIn = { route = AuthRoute.SignIn },
        )
        AuthRoute.Lock -> LockScreen(
            session = session,
            onUnlocked = { route = AuthRoute.Pos },
            onSwitchAccount = { route = AuthRoute.SignIn },
        )
        AuthRoute.Pos -> PosShell(
            onLock = { route = AuthRoute.Lock },
            onSignOut = { route = AuthRoute.SignIn },
        )
    }
}

@Composable
private fun PosShell(onLock: () -> Unit, onSignOut: () -> Unit) {
    var isDark by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf(ActiveRole.Admin) }
    var page by remember { mutableStateOf(PosPage.FloorPlan) }
    val badges = remember { mutableStateMapOf<String, Int>() }
    val colors = if (isDark) DarkPosColors else LightPosColors
    val allowed = roleNavAccess.getValue(role)

    LaunchedEffect(role) {
        if (page !in allowed) page = allowed.first()
    }

    Box(Modifier.fillMaxSize().background(posBackground(colors))) {
        Column(Modifier.fillMaxSize()) {
            PosHeader(
                colors = colors,
                isDark = isDark,
                role = role,
                onToggleDark = { isDark = !isDark },
                onRole = { role = it },
                onLock = onLock,
                onSignOut = onSignOut,
            )
            Box(Modifier.weight(1f).fillMaxWidth().padding(bottom = PosDimens.BottomNavHeight)) {
                when (page) {
                    PosPage.FloorPlan -> FloorPlanScreen(colors, role, isDark = isDark, onPendingReservations = { badges[""] = it })
                    PosPage.Orders -> OrdersScreen(colors, role)
                    PosPage.Kitchen -> KitchenScreen(colors, role, onReceivedCount = { badges["kitchen"] = it })
                    PosPage.Analytics -> AnalyticsScreen(colors)
                    PosPage.Settings -> SettingsScreen(colors, role)
                }
            }
        }
        BottomNav(colors = colors, pages = allowed, selected = page, badges = badges, onSelect = { page = it }, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun PosHeader(
    colors: PosColors,
    isDark: Boolean,
    role: ActiveRole,
    onToggleDark: () -> Unit,
    onRole: (ActiveRole) -> Unit,
    onLock: () -> Unit,
    onSignOut: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .height(PosDimens.HeaderHeight)
            .border(width = 1.dp, color = Color.Transparent)
            .border(1.dp, colors.headerBorder.copy(alpha = 0.8f))
            .padding(horizontal = PosDimens.SpaceLg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(Blue600), contentAlignment = Alignment.Center) {
            Text("POS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("Restaurant Chain", color = colors.text, fontSize = 15.sp, fontWeight = FontWeight.Medium, letterSpacing = (-0.2).sp)
            Text("POINT OF SALE", color = colors.textMuted.copy(alpha = 0.8f), fontSize = 10.sp, letterSpacing = 1.sp)
        }
        Spacer(Modifier.weight(1f))
        HeaderButton(if (isDark) "Light" else "Dark", colors, onToggleDark)
        Spacer(Modifier.width(8.dp))
        HeaderButton("Lock", colors, onLock)
        Spacer(Modifier.width(8.dp))
        Box {
            HeaderButton(role.label, colors) { expanded = true }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ActiveRole.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(option.label, fontWeight = if (option == role) FontWeight.Medium else FontWeight.Normal)
                                Text("${roleNavAccess.getValue(option).size} pages", fontSize = 10.sp, color = colors.textMuted)
                            }
                        },
                        onClick = {
                            onRole(option)
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Sign out", color = Color(0xFFEF4444)) },
                    onClick = { expanded = false; onSignOut() },
                )
            }
        }
    }
}

@Composable
private fun HeaderButton(text: String, colors: PosColors, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BottomNav(
    colors: PosColors,
    pages: List<PosPage>,
    selected: PosPage,
    badges: Map<String, Int>,
    onSelect: (PosPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(PosDimens.BottomNavHeight)
            .background(colors.navBackground)
            .border(1.dp, colors.headerBorder),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        pages.forEach { item ->
            val active = item == selected
            val badge = badges[item.badgeKey] ?: 0
            Column(
                Modifier
                    .weight(1f)
                    .clickable { onSelect(item) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Text(navIcon(item), color = if (active) colors.text else colors.navInactive, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    if (badge > 0) {
                        Box(Modifier.size(18.dp).clip(CircleShape).background(Blue600), contentAlignment = Alignment.Center) {
                            Text(if (badge > 99) "99+" else badge.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(item.label, color = if (active) colors.text else colors.navInactive, fontSize = 11.sp)
            }
        }
    }
}

private fun navIcon(page: PosPage): String =
    when (page) {
        PosPage.FloorPlan -> "F"
        PosPage.Orders -> "O"
        PosPage.Kitchen -> "K"
        PosPage.Analytics -> "A"
        PosPage.Settings -> "S"
    }
