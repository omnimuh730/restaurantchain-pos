package com.mh.restaurantchainpos.pos.ui.analytics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Shared types for the Analytics screens. Mirrors the React demo's `Period`,
 * `AnalyticsSection`, and `Currency` types so the rest of the module can stay
 * near-1:1 with the React sources under `pos/analytics/`.
 */
enum class AnalyticsSection(val id: String, val label: String, val icon: ImageVector) {
    SalesDashboard("sales-dashboard", "Sales Dashboard", Icons.Outlined.BarChart),
    Menu("menu-analysis", "Menu Analysis", Icons.Outlined.RestaurantMenu),
    Customer("customer-analysis", "Customer Analysis", Icons.Outlined.People),
    History("history", "History", Icons.Outlined.History),
}

enum class Period(val id: String, val label: String) {
    Today("today", "Today"),
    Week("week", "Week"),
    Month("month", "Month"),
    Quarter("3month", "3 Months"),
    Custom("custom", "Custom"),
}

enum class AnalyticsCurrency { Domestic, Foreign }

data class DateRange(val startMs: Long, val endMs: Long)
