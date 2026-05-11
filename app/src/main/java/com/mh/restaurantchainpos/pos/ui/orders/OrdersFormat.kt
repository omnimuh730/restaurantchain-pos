package com.mh.restaurantchainpos.pos.ui.orders

import com.mh.restaurantchainpos.pos.data.CurrencyKind

internal fun formatDomesticWon(value: Double): String = "\u20A9" + "%,.0f".format(value)
internal fun formatForeignUsd(value: Double): String = "$" + "%,.2f".format(value)

internal fun formatLineMoney(value: Double, currency: CurrencyKind): String =
    if (currency == CurrencyKind.Domestic) formatDomesticWon(value) else formatForeignUsd(value)

internal fun paySummary(krw: Double, usd: Double): String =
    listOfNotNull(
        if (krw > 0.0) formatDomesticWon(krw) else null,
        if (usd > 0.0 || krw == 0.0) formatForeignUsd(usd) else null,
    ).joinToString(" - ")

internal fun floorLabel(id: String): String = OrderFloors.firstOrNull { it.id == id }?.label ?: "All Floors"
internal fun tableLabel(id: String): String = OrderTables.firstOrNull { it.id == id }?.label ?: id
internal fun checkNumber(id: String): String = CheckNumbers[id] ?: "Ch. #--"
