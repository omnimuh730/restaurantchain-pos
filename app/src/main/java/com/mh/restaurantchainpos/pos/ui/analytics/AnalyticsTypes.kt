package com.mh.restaurantchainpos.pos.ui.analytics

/**
 * Shared types for the Analytics screens. Mirrors the React demo's `Period`,
 * `AnalyticsSection`, and `Currency` types so the rest of the module can stay
 * near-1:1 with the React sources under `pos/analytics/`.
 */
enum class AnalyticsSection(val id: String, val label: String, val icon: String) {
    Dashboard("dashboard", "Sales Dashboard", "*"),
    Menu("menu-analysis", "Menu Analysis", "#"),
    Customer("customer-analysis", "Customer Analysis", "@"),
    History("history", "History", "~"),
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
