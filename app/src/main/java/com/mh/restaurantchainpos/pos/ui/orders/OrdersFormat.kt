package com.mh.restaurantchainpos.pos.ui.orders

import com.mh.restaurantchainpos.pos.data.CurrencyKind

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

internal fun floorLabel(id: String): String = OrderFloors.firstOrNull { it.id == id }?.label ?: "All Floors"
internal fun tableLabel(id: String): String = OrderTables.firstOrNull { it.id == id }?.label ?: id
internal fun checkNumber(id: String): String = CheckNumbers[id] ?: "Ch. #--"

/** e.g. "2nd Floor · Table 8" for the history table column. */
internal fun historyTableCell(tableId: String): String {
    val table = OrderTables.firstOrNull { it.id == tableId } ?: return tableId
    return "${floorLabel(table.floor)} · ${table.label}"
}

internal fun historySummaryLine(billCount: Int, totalKrw: Double, totalUsd: Double): String =
    listOf(
        "$billCount bills",
        if (totalKrw > 0) formatDomesticWon(totalKrw) else null,
        if (totalUsd > 0) formatForeignUsd(totalUsd) else null,
    ).filterNotNull().joinToString(" · ")
