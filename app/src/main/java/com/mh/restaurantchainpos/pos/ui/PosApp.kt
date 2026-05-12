package com.mh.restaurantchainpos.pos.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.AuthRoute
import com.mh.restaurantchainpos.pos.data.AuthSession
import com.mh.restaurantchainpos.pos.data.PosPage
import com.mh.restaurantchainpos.pos.data.roleNavAccess
import com.mh.restaurantchainpos.pos.ui.analytics.AnalyticsScreen
import com.mh.restaurantchainpos.pos.ui.auth.LockScreen
import com.mh.restaurantchainpos.pos.ui.auth.SignInScreen
import com.mh.restaurantchainpos.pos.ui.auth.SignUpScreen
import com.mh.restaurantchainpos.pos.ui.floor.FloorPlanScreen
import com.mh.restaurantchainpos.pos.ui.kitchen.KitchenScreen
import com.mh.restaurantchainpos.pos.ui.layout.header.PosAppHeader
import com.mh.restaurantchainpos.pos.ui.layout.metrics.rememberPosLayoutMetrics
import com.mh.restaurantchainpos.pos.ui.layout.navigation.PosAppBottomBar
import com.mh.restaurantchainpos.pos.ui.layout.navigation.PosAppNavigationRail
import com.mh.restaurantchainpos.pos.ui.layout.shell.PosShellScaffold
import com.mh.restaurantchainpos.pos.ui.orders.OrdersScreen
import com.mh.restaurantchainpos.pos.ui.settings.SettingsScreen
import com.mh.restaurantchainpos.pos.ui.theme.DarkPosColors
import com.mh.restaurantchainpos.pos.ui.theme.LightPosColors
import com.mh.restaurantchainpos.ui.theme.RestaurantchainPOSTheme

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
    var ordersFloorPayTableId by remember { mutableStateOf<String?>(null) }
    var ordersFloorPayNonce by remember { mutableLongStateOf(0L) }
    val badges = remember { mutableStateMapOf<String, Int>() }
    val colors = if (isDark) DarkPosColors else LightPosColors
    val allowed = roleNavAccess.getValue(role)
    val layoutMetrics = rememberPosLayoutMetrics()

    LaunchedEffect(role) {
        if (page !in allowed) page = allowed.first()
    }

    RestaurantchainPOSTheme(darkTheme = isDark) {
        PosShellScaffold(
            colors = colors,
            metrics = layoutMetrics,
            header = {
                PosAppHeader(
                    colors = colors,
                    isDark = isDark,
                    role = role,
                    horizontalPadding = layoutMetrics.headerHorizontalPadding,
                    onToggleDark = { isDark = !isDark },
                    onRole = { role = it },
                    onLock = onLock,
                    onSignOut = onSignOut,
                )
            },
            bottomBar = {
                PosAppBottomBar(
                    colors = colors,
                    pages = allowed,
                    selected = page,
                    badges = badges,
                    onSelect = { page = it },
                )
            },
            navigationRail = {
                PosAppNavigationRail(
                    colors = colors,
                    pages = allowed,
                    selected = page,
                    badges = badges,
                    onSelect = { page = it },
                )
            },
        ) {
            when (page) {
                PosPage.FloorPlan -> FloorPlanScreen(
                    colors = colors,
                    role = role,
                    isDark = isDark,
                    onPendingReservations = { badges[""] = it },
                    onNavigateToOrderPayment = { tableId ->
                        ordersFloorPayTableId = tableId
                        ordersFloorPayNonce = System.nanoTime()
                        page = PosPage.Orders
                    },
                )
                PosPage.Orders -> OrdersScreen(
                    colors = colors,
                    role = role,
                    floorPaymentTableId = ordersFloorPayTableId,
                    floorPaymentNonce = ordersFloorPayNonce,
                    onConsumedFloorPayment = {
                        ordersFloorPayTableId = null
                        ordersFloorPayNonce = 0L
                    },
                )
                PosPage.Kitchen -> KitchenScreen(colors, role, onReceivedCount = { badges["kitchen"] = it })
                PosPage.Analytics -> AnalyticsScreen(colors)
                PosPage.Settings -> SettingsScreen(colors, role)
            }
        }
    }
}
