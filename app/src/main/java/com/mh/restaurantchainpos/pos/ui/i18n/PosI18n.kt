package com.mh.restaurantchainpos.pos.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.ActiveRole
import com.mh.restaurantchainpos.pos.data.PosPage
import com.mh.restaurantchainpos.pos.ui.analytics.AnalyticsSection
import com.mh.restaurantchainpos.pos.ui.analytics.HistoryKind
import com.mh.restaurantchainpos.pos.ui.analytics.HistoryStatus
import com.mh.restaurantchainpos.pos.ui.analytics.Period
import com.mh.restaurantchainpos.pos.ui.floor.FloorViewMode
import com.mh.restaurantchainpos.pos.ui.kitchen.KitchenSortMode
import com.mh.restaurantchainpos.pos.ui.kitchen.KitchenViewTab
import com.mh.restaurantchainpos.pos.ui.settings.SettingsSection

@Composable
fun PosPage.stringTitle(): String = when (this) {
    PosPage.Analytics -> stringResource(R.string.nav_analytics)
    PosPage.FloorPlan -> stringResource(R.string.nav_floor)
    PosPage.Orders -> stringResource(R.string.nav_orders)
    PosPage.Kitchen -> stringResource(R.string.nav_kitchen)
    PosPage.Settings -> stringResource(R.string.nav_settings)
}

@Composable
fun ActiveRole.stringTitle(): String = when (this) {
    ActiveRole.Admin -> stringResource(R.string.role_admin)
    ActiveRole.Cashier -> stringResource(R.string.role_cashier)
    ActiveRole.Chef -> stringResource(R.string.role_chef)
    ActiveRole.Waiter -> stringResource(R.string.role_waiter)
}

@Composable
fun ActiveRole.pagesSummary(pageCount: Int): String =
    stringResource(R.string.role_pages_suffix, pageCount)

@Composable
fun FloorViewMode.stringTitle(): String = when (this) {
    FloorViewMode.Floor -> stringResource(R.string.floor_mode_floor)
    FloorViewMode.Table -> stringResource(R.string.floor_mode_table)
    FloorViewMode.Calendar -> stringResource(R.string.floor_mode_calendar)
}

@Composable
fun KitchenViewTab.stringTitle(): String = when (this) {
    KitchenViewTab.Received -> stringResource(R.string.kitchen_tab_received)
    KitchenViewTab.InProgress -> stringResource(R.string.kitchen_tab_in_progress)
    KitchenViewTab.Completed -> stringResource(R.string.kitchen_tab_completed)
}

@Composable
fun KitchenSortMode.stringLabel(): String = when (this) {
    KitchenSortMode.Oldest -> stringResource(R.string.kitchen_sort_oldest_first)
    KitchenSortMode.Newest -> stringResource(R.string.kitchen_sort_newest_first)
}

@Composable
fun KitchenSortMode.stringTrigger(): String = when (this) {
    KitchenSortMode.Oldest -> stringResource(R.string.kitchen_sort_oldest)
    KitchenSortMode.Newest -> stringResource(R.string.kitchen_sort_newest)
}

@Composable
fun AnalyticsSection.stringTitle(): String = when (this) {
    AnalyticsSection.SalesDashboard -> stringResource(R.string.analytics_sales_dashboard)
    AnalyticsSection.Menu -> stringResource(R.string.analytics_menu_analysis)
    AnalyticsSection.Customer -> stringResource(R.string.analytics_customer_analysis)
    AnalyticsSection.History -> stringResource(R.string.analytics_history)
}

@Composable
fun HistoryKind.stringTitle(): String = when (this) {
    HistoryKind.Order -> stringResource(R.string.analytics_hist_kind_order)
    HistoryKind.Reservation -> stringResource(R.string.analytics_hist_kind_reservation)
    HistoryKind.Payment -> stringResource(R.string.analytics_hist_kind_payment)
    HistoryKind.NoShow -> stringResource(R.string.analytics_hist_kind_no_show)
    HistoryKind.WalkIn -> stringResource(R.string.analytics_hist_kind_walk_in)
}

@Composable
fun HistoryStatus.stringTitle(): String = when (this) {
    HistoryStatus.Completed -> stringResource(R.string.analytics_hist_status_completed)
    HistoryStatus.Paid -> stringResource(R.string.analytics_hist_status_paid)
    HistoryStatus.NoShow -> stringResource(R.string.analytics_hist_status_no_show)
    HistoryStatus.Refunded -> stringResource(R.string.analytics_hist_status_refunded)
}

@Composable
fun Period.stringTitle(): String = when (this) {
    Period.Today -> stringResource(R.string.period_today)
    Period.Week -> stringResource(R.string.period_week)
    Period.Month -> stringResource(R.string.period_month)
    Period.Quarter -> stringResource(R.string.period_quarter)
    Period.Custom -> stringResource(R.string.period_custom)
}

@Composable
fun SettingsSection.stringTitle(): String = when (this) {
    SettingsSection.General -> stringResource(R.string.settings_section_general)
    SettingsSection.Menu -> stringResource(R.string.settings_section_menu)
    SettingsSection.Amenities -> stringResource(R.string.settings_section_amenities)
    SettingsSection.Security -> stringResource(R.string.settings_section_security)
    SettingsSection.Staff -> stringResource(R.string.settings_section_staff)
    SettingsSection.Upgrade -> stringResource(R.string.settings_section_upgrade)
}

@Composable
fun SettingsSection.stringDescription(): String = when (this) {
    SettingsSection.General -> stringResource(R.string.settings_section_general_desc)
    SettingsSection.Menu -> stringResource(R.string.settings_section_menu_desc)
    SettingsSection.Amenities -> stringResource(R.string.settings_section_amenities_desc)
    SettingsSection.Security -> stringResource(R.string.settings_section_security_desc)
    SettingsSection.Staff -> stringResource(R.string.settings_section_staff_desc)
    SettingsSection.Upgrade -> stringResource(R.string.settings_section_upgrade_desc)
}
