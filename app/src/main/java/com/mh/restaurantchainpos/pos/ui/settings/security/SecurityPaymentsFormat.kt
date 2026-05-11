package com.mh.restaurantchainpos.pos.ui.settings

internal fun formatCardNumber(input: String): String {
    val digits = input.filter { it.isDigit() }.take(16)
    return digits.chunked(4).joinToString(" ")
}
