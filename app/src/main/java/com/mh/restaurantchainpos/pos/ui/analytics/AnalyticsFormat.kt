package com.mh.restaurantchainpos.pos.ui.analytics

import java.text.NumberFormat
import java.util.Locale

/**
 * Currency / number helpers. Mirrors `formatDomesticWon` and `formatForeignUsd`
 * plus the `useAnalyticsCurrency` `pick`/`fmt` helpers in the React demo.
 */
object AnalyticsFormat {
    private val intFmt: NumberFormat = NumberFormat.getIntegerInstance(Locale.US)
    private val usdFmt: NumberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    fun won(value: Long): String = "₩" + intFmt.format(value)
    fun won(value: Int): String = won(value.toLong())

    fun usd(value: Double): String = "$" + usdFmt.format(value)
    fun usd(value: Int): String = "$" + usdFmt.format(value.toDouble())

    fun money(currency: AnalyticsCurrency, value: Double): String =
        when (currency) {
            AnalyticsCurrency.Domestic -> won(value.toLong())
            AnalyticsCurrency.Foreign -> usd(value)
        }

    fun int(value: Int): String = intFmt.format(value.toLong())
    fun int(value: Long): String = intFmt.format(value)
}
