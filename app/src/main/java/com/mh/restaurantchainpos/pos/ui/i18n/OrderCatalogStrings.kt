package com.mh.restaurantchainpos.pos.ui.i18n

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R

fun Context.orderCatalogString(prefix: String, id: String, fallback: String = id): String {
    val resName = "${prefix}_${id.replace('-', '_')}".lowercase()
    val resId = resources.getIdentifier(resName, "string", packageName)
    return if (resId != 0) getString(resId) else fallback
}

@Composable
fun rememberOrderCatalogString(prefix: String, id: String, fallback: String = id): String {
    val ctx = LocalContext.current
    val localeKey = LocalConfiguration.current.locales[0]
    val resName = "${prefix}_${id.replace('-', '_')}".lowercase()
    val resId = remember(prefix, id, localeKey) {
        ctx.resources.getIdentifier(resName, "string", ctx.packageName)
    }
    return if (resId != 0) stringResource(resId) else fallback
}

@Composable
fun ordersPaymentMethodLabel(methodKey: String): String = when (methodKey.lowercase()) {
    "cash" -> stringResource(R.string.orders_payment_cash)
    "credit" -> stringResource(R.string.orders_payment_credit)
    "mix" -> stringResource(R.string.orders_payment_mix)
    else -> methodKey
}

fun Context.ordersPaymentMethodLabel(methodKey: String): String = when (methodKey.lowercase()) {
    "cash" -> getString(R.string.orders_payment_cash)
    "credit" -> getString(R.string.orders_payment_credit)
    "mix" -> getString(R.string.orders_payment_mix)
    else -> methodKey
}

@Composable
fun ordersMenuLineTitle(nameKey: String): String =
    rememberOrderCatalogString("orders_item", nameKey, nameKey)
