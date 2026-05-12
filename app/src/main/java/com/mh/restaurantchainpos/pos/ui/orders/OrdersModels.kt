package com.mh.restaurantchainpos.pos.ui.orders

import com.mh.restaurantchainpos.pos.data.CurrencyKind

internal data class OrderFloor(val id: String)
internal data class OrderTable(val id: String, val seats: Int, val floor: String)
internal data class OrderMenuCategory(val id: String, val subCategories: List<OrderSubCategory>)
internal data class OrderSubCategory(val id: String, val items: List<OrderMenuItem>)
internal data class OrderMenuItem(
    val id: String,
    val price: Double,
    val currency: CurrencyKind = CurrencyKind.Foreign,
)

internal data class OrderLine(
    val id: String,
    val baseId: String,
    val price: Double,
    val qty: Int,
    val categoryId: String,
    val currency: CurrencyKind,
    val modifiers: List<String> = emptyList(),
    val ordered: Boolean = false,
    val deleted: Boolean = false,
    val origQty: Int? = null,
)

internal data class HistoryLineItem(
    val nameKey: String,
    val qty: Int,
    val each: Double,
    val line: Double,
    val currency: CurrencyKind,
)

internal data class HistoryBill(
    val id: String,
    val tableId: String,
    val time: String,
    val krw: Double,
    val usd: Double,
    /** Localized via [com.mh.restaurantchainpos.pos.ui.i18n.ordersPaymentMethodLabel]. */
    val methodKey: String,
    val lines: List<HistoryLineItem> = emptyList(),
)
