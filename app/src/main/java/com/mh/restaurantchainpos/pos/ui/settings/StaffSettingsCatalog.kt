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
import com.mh.restaurantchainpos.R
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

internal data class PermItem(val id: String, val titleRes: Int, val descRes: Int, val icon: ImageVector)

internal val PermGroups: List<Pair<Int, List<PermItem>>> = listOf(
    R.string.staff_perm_group_page_access to listOf(
        PermItem("floor-plan", R.string.staff_perm_floor_plan, R.string.staff_perm_floor_plan_desc, Icons.Outlined.Store),
        PermItem("orders", R.string.staff_perm_orders, R.string.staff_perm_orders_desc, Icons.Outlined.RestaurantMenu),
        PermItem("kitchen", R.string.staff_perm_kitchen, R.string.staff_perm_kitchen_desc, Icons.Outlined.SoupKitchen),
    ),
    R.string.staff_perm_group_actions to listOf(
        PermItem("reservations", R.string.staff_perm_reservations, R.string.staff_perm_reservations_desc, Icons.Outlined.CalendarToday),
        PermItem("take-orders", R.string.staff_perm_take_orders, R.string.staff_perm_take_orders_desc, Icons.Outlined.RestaurantMenu),
        PermItem("process-payment", R.string.staff_perm_process_payment, R.string.staff_perm_process_payment_desc, Icons.Outlined.CreditCard),
        PermItem("menu-management", R.string.staff_perm_menu_management, R.string.staff_perm_menu_management_desc, Icons.Outlined.RestaurantMenu),
    ),
)
internal val AllPerms: List<PermItem> = PermGroups.flatMap { it.second }
internal val PermIcons: List<PermItem> = listOf(
    AllPerms[0],
    AllPerms[1],
    AllPerms[2],
    AllPerms[3],
    PermItem("settings-password", R.string.staff_perm_settings_password, 0, Icons.Outlined.Shield),
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

internal fun roleTitleRes(role: String): Int =
    when (role) {
        "Waiter" -> R.string.staff_role_waiter
        "Chef" -> R.string.staff_role_chef
        "Cashier" -> R.string.staff_role_cashier
        else -> 0
    }

internal enum class ConfirmKind(val btnColor: Color) {
    Deactivate(Orange500),
    Activate(Blue600),
    Remove(Red500),
    ResetPin(Orange500),
}

internal data class ConfirmRequest(val kind: ConfirmKind, val member: StaffMember)
