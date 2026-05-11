package com.mh.restaurantchainpos.pos.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mh.restaurantchainpos.pos.data.StaffMember
import com.mh.restaurantchainpos.pos.ui.theme.Blue600
import com.mh.restaurantchainpos.pos.ui.theme.Orange500
import com.mh.restaurantchainpos.pos.ui.theme.Red500

internal data class RoleConfig(val icon: ImageVector)

internal val Roles = listOf("Waiter", "Chef", "Cashier")
internal val RoleConfigs: Map<String, RoleConfig> = mapOf(
    "Waiter" to RoleConfig(Icons.Outlined.HowToReg),
    "Chef" to RoleConfig(Icons.Outlined.SoupKitchen),
    "Cashier" to RoleConfig(Icons.Outlined.Wallet),
)

internal data class PermItem(val id: String, val label: String, val desc: String, val icon: ImageVector)

internal val PermGroups: List<Pair<String, List<PermItem>>> = listOf(
    "PAGE ACCESS" to listOf(
        PermItem("floor-plan", "Floor Plan", "View floor plan & table layout", Icons.Outlined.Store),
        PermItem("orders", "Orders", "Access orders page", Icons.Outlined.RestaurantMenu),
        PermItem("kitchen", "Kitchen", "Access kitchen page", Icons.Outlined.SoupKitchen),
    ),
    "ACTIONS" to listOf(
        PermItem("reservations", "Reservations", "Handle table reservations", Icons.Outlined.CalendarToday),
        PermItem("take-orders", "Take Orders", "Create & process orders", Icons.Outlined.RestaurantMenu),
        PermItem("process-payment", "Process Payment", "Handle payments & refunds", Icons.Outlined.CreditCard),
        PermItem("menu-management", "Menu Management", "Manage menu items & categories", Icons.Outlined.RestaurantMenu),
    ),
)
internal val AllPerms: List<PermItem> = PermGroups.flatMap { it.second }
internal val PermIcons: List<PermItem> = listOf(
    AllPerms[0],
    AllPerms[1],
    AllPerms[2],
    AllPerms[3],
    PermItem("settings-password", "Settings Password", "", Icons.Outlined.Shield),
)
internal val RoleDefaults: Map<String, Map<String, Boolean>> = mapOf(
    "Waiter" to mapOf("orders" to true, "kitchen" to true, "take-orders" to true),
    "Chef" to mapOf("kitchen" to true),
    "Cashier" to mapOf(
        "floor-plan" to true,
        "reservations" to true,
        "orders" to true,
        "kitchen" to true,
        "take-orders" to true,
        "process-payment" to true,
        "menu-management" to true,
    ),
)
internal const val TotalPermCount = 7

internal enum class ConfirmKind(
    val title: String,
    val description: String,
    val btnLabel: String,
    val btnColor: Color,
) {
    Deactivate(
        "Deactivate Staff",
        "This staff member will no longer be able to log in.",
        "Deactivate",
        Orange500,
    ),
    Activate(
        "Activate Staff",
        "This staff member will regain access to the POS.",
        "Activate",
        Blue600,
    ),
    Remove(
        "Remove Staff",
        "This permanently removes the staff member and their permissions.",
        "Remove",
        Red500,
    ),
    ResetPin(
        "Reset PIN",
        "This will generate a temporary PIN for the staff member.",
        "Reset PIN",
        Orange500,
    ),
}

internal data class ConfirmRequest(val kind: ConfirmKind, val member: StaffMember)
