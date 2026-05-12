package com.mh.restaurantchainpos.pos.data

enum class PosPage(val id: String, val label: String, val badgeKey: String = id) {
    FloorPlan("floor-plan", "Floor", ""),
    Orders("orders", "Orders"),
    Kitchen("kitchen", "Kitchen"),
    Analytics("analytics", "Analytics"),
    Settings("settings", "Settings"),
}

enum class ActiveRole(val label: String) {
    Admin("Admin"),
    Cashier("Cashier"),
    Chef("Chef"),
    Waiter("Waiter"),
}

val roleNavAccess: Map<ActiveRole, List<PosPage>> = mapOf(
    ActiveRole.Admin to listOf(PosPage.Analytics, PosPage.FloorPlan, PosPage.Orders, PosPage.Kitchen, PosPage.Settings),
    ActiveRole.Cashier to listOf(PosPage.FloorPlan, PosPage.Orders, PosPage.Kitchen, PosPage.Settings),
    ActiveRole.Chef to listOf(PosPage.Kitchen, PosPage.Settings),
    ActiveRole.Waiter to listOf(PosPage.Orders, PosPage.Kitchen, PosPage.Settings),
)

enum class CurrencyKind { Foreign, Domestic }

data class MenuCategory(val id: String, val label: String, val subCategories: List<MenuSubCategory>)

data class MenuSubCategory(val id: String, val label: String, val items: List<MenuItem>)

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val currency: CurrencyKind = CurrencyKind.Foreign,
)

data class OrderItem(
    val id: String,
    val baseId: String,
    val name: String,
    val price: Double,
    val qty: Int,
    val category: String,
    val currency: CurrencyKind,
    val ordered: Boolean = false,
    val deleted: Boolean = false,
)

enum class TableStatus { Available, Occupied, Reserved }

enum class TableShape { Rect, Circle }

data class TableOrderItem(val nameKey: String, val qty: Int, val price: Int)

data class FloorTable(
    val id: String,
    val label: String,
    val seats: Int,
    val shape: TableShape,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val status: TableStatus,
    val revenue: Int = 0,
    val occupiedSeats: Int = 0,
    val guestName: String = "",
    val reservationTime: String = "",
    val orderItems: List<TableOrderItem> = emptyList(),
)

data class Floor(val id: String, val name: String, val tables: List<FloorTable>)

object FloorMetrics {
    const val BaseUnit = 72
    const val SnapGrid = BaseUnit / 3
    const val CanvasW = 2400
    const val CanvasH = 1800
}

enum class ReservationType { Confirmed, Request }

data class Reservation(
    val id: String,
    val tableId: String,
    val guestName: String,
    val partySize: Int,
    val startTime: String,
    val durationHours: Double,
    val dayOffset: Int,
    val type: ReservationType,
    val status: String = "",
)

enum class KitchenStatus { Received, InProgress, Completed }

data class KitchenItem(
    val id: String,
    val titleKey: String,
    val qty: Int,
    val done: Boolean = false,
    val previouslyCompleted: Boolean = false,
    val modifier: String = "",
    val selectedQty: Int? = null,
)

data class KitchenOrder(
    val id: String,
    val table: String,
    val status: KitchenStatus,
    val minutesAgo: Int,
    val completedMinutesAgo: Int? = null,
    val items: List<KitchenItem>,
)

data class KitchenFloor(val id: String, val label: String, val tables: List<String>)

data class StaffMember(
    val id: String,
    val name: String,
    val username: String,
    val role: String,
    val status: String,
    val joinDate: String,
    val permissionCount: Int,
    val permissions: Map<String, Boolean> = emptyMap(),
)

data class PaymentCard(val brand: String, val last4: String, val expiry: String, val holderName: String, val isDefault: Boolean = false)

data class AnalyticsPoint(val label: String, val foreign: Int, val domestic: Int, val orders: Int)

data class HistoryEvent(val id: String, val kind: String, val guest: String, val table: String, val amount: Int, val status: String, val note: String)
