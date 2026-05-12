package com.mh.restaurantchainpos.pos.ui.orders

import android.content.Context
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.data.CurrencyKind
import com.mh.restaurantchainpos.pos.data.FloorTable
import com.mh.restaurantchainpos.pos.ui.i18n.orderCatalogString

internal fun formatDomesticWon(value: Double): String = "\u20A9" + "%,.0f".format(value)
internal fun formatForeignUsd(value: Double): String = "$" + "%,.2f".format(value)

internal fun formatLineMoney(value: Double, currency: CurrencyKind): String =
    if (currency == CurrencyKind.Domestic) formatDomesticWon(value) else formatForeignUsd(value)

/** Totals for non-deleted lines only — must stay in sync with what the list shows. */
internal fun orderCurrencyTotals(lines: List<OrderLine>): Pair<Double, Double> {
    val visible = lines.filterNot { it.deleted }
    val krw = visible.filter { it.currency == CurrencyKind.Domestic }.sumOf { it.price * it.qty }
    val usd = visible.filter { it.currency == CurrencyKind.Foreign }.sumOf { it.price * it.qty }
    return krw to usd
}

internal fun paySummary(krw: Double, usd: Double): String =
    listOfNotNull(
        if (krw > 0.0) formatDomesticWon(krw) else null,
        if (usd > 0.0 || krw == 0.0) formatForeignUsd(usd) else null,
    ).joinToString(" · ")

internal fun Context.floorOrderLabel(id: String): String =
    if (OrderFloors.any { it.id == id }) {
        orderCatalogString("orders_floor", id, id)
    } else {
        getString(R.string.orders_all_floors)
    }

internal fun Context.tableOrderLabel(id: String): String =
    if (OrderTables.any { it.id == id }) {
        orderCatalogString("orders_table", id.lowercase(), id)
    } else {
        id
    }

/**
 * Floor canvas / cards: in edit mode always show [FloorTable.label]. In view mode, use the
 * localized orders catalog name for known table ids unless the floor row has a custom label
 * (not the auto-generated `Table N` pattern from mock data).
 */
internal fun Context.floorTableDisplayLine(table: FloorTable, editMode: Boolean): String {
    if (editMode) return table.label.ifBlank { table.id }
    if (!OrderTables.any { it.id == table.id }) return table.label.ifBlank { table.id }
    val catalog = tableOrderLabel(table.id)
    val looksLikeDefaultTableName = table.label.matches(Regex("^Table\\s+\\d+$", RegexOption.IGNORE_CASE))
    return if (table.label.isBlank() || table.label == table.id || looksLikeDefaultTableName) {
        catalog
    } else {
        table.label
    }
}

internal fun checkNumber(id: String): String = CheckNumbers[id] ?: "Ch. #--"

internal fun Context.historyTableCell(tableId: String): String {
    val table = OrderTables.firstOrNull { it.id == tableId } ?: return tableId
    return "${floorOrderLabel(table.floor)} · ${tableOrderLabel(table.id)}"
}

internal fun Context.historySummaryLine(billCount: Int, totalKrw: Double, totalUsd: Double): String =
    listOf(
        getString(R.string.orders_hist_bills_suffix, billCount),
        if (totalKrw > 0) formatDomesticWon(totalKrw) else null,
        if (totalUsd > 0) formatForeignUsd(totalUsd) else null,
    ).filterNotNull().joinToString(" · ")
