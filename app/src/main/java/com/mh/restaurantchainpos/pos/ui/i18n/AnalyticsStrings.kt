package com.mh.restaurantchainpos.pos.ui.i18n

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mh.restaurantchainpos.R
import com.mh.restaurantchainpos.pos.ui.analytics.HistoryData
import com.mh.restaurantchainpos.pos.ui.analytics.HistoryEvent
import com.mh.restaurantchainpos.pos.ui.analytics.HistoryReceiptItem
import com.mh.restaurantchainpos.pos.ui.analytics.TrendPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun axisTickLabel(axisKey: String): String =
    if (axisKey.all { it.isDigit() }) axisKey else stringResource(axisLabelRes(axisKey))

@Composable
fun TrendPoint.axisLabel(): String = axisTickLabel(axisKey)

fun TrendPoint.axisLabel(context: Context): String =
    if (axisKey.all { it.isDigit() }) axisKey else context.getString(axisLabelRes(axisKey))

private fun axisLabelRes(key: String): Int = when (key) {
    "MON" -> R.string.analytics_axis_mon
    "TUE" -> R.string.analytics_axis_tue
    "WED" -> R.string.analytics_axis_wed
    "THU" -> R.string.analytics_axis_thu
    "FRI" -> R.string.analytics_axis_fri
    "SAT" -> R.string.analytics_axis_sat
    "SUN" -> R.string.analytics_axis_sun
    "W1" -> R.string.analytics_axis_w1
    "W2" -> R.string.analytics_axis_w2
    "W3" -> R.string.analytics_axis_w3
    "W4" -> R.string.analytics_axis_w4
    "JAN" -> R.string.analytics_axis_jan
    "FEB" -> R.string.analytics_axis_feb
    "MAR" -> R.string.analytics_axis_mar
    "APR" -> R.string.analytics_axis_apr
    else -> R.string.analytics_axis_unknown
}

@Composable
fun paymentMethodLabel(methodKey: String): String = stringResource(paymentMethodRes(methodKey))

fun paymentMethodLabel(context: Context, methodKey: String): String =
    context.getString(paymentMethodRes(methodKey))

private fun paymentMethodRes(key: String): Int = when (key) {
    "credit" -> R.string.analytics_pay_credit
    "cash" -> R.string.analytics_pay_cash
    "credit_card" -> R.string.analytics_pay_credit_card
    else -> R.string.analytics_pay_unknown
}

@Composable
fun menuItemTitle(nameKey: String): String = stringResource(menuItemTitleRes(nameKey))

fun menuItemTitle(context: Context, nameKey: String): String =
    context.getString(menuItemTitleRes(nameKey))

private fun menuItemTitleRes(key: String): Int = when (key) {
    "ribeyeSteak" -> R.string.analytics_menu_ribeye_steak
    "grilledSalmon" -> R.string.analytics_menu_grilled_salmon
    "lobsterTail" -> R.string.analytics_menu_lobster_tail
    "caesarSalad" -> R.string.analytics_menu_caesar_salad
    "truffleFries" -> R.string.analytics_menu_truffle_fries
    "lycheeMartini" -> R.string.analytics_menu_lychee_martini
    "chickenWings" -> R.string.analytics_menu_chicken_wings
    "tiramisu" -> R.string.analytics_menu_tiramisu
    "fishAndChips" -> R.string.analytics_menu_fish_and_chips
    "maiTai" -> R.string.analytics_menu_mai_tai
    "bibimbap" -> R.string.analytics_menu_bibimbap
    "bulgogi" -> R.string.analytics_menu_bulgogi
    "soju" -> R.string.analytics_menu_soju
    "makgeolli" -> R.string.analytics_menu_makgeolli
    "kimchi" -> R.string.analytics_menu_kimchi
    "ramen" -> R.string.analytics_menu_ramen
    "udonNoodles" -> R.string.analytics_menu_udon
    "gyoza" -> R.string.analytics_menu_gyoza
    "greenTea" -> R.string.analytics_menu_green_tea
    "hotSake" -> R.string.analytics_menu_hot_sake
    "americano" -> R.string.analytics_line_americano
    "cafe_latte" -> R.string.analytics_line_cafe_latte
    "honey_cold_brew" -> R.string.analytics_line_honey_cold_brew
    "croissant" -> R.string.analytics_line_croissant
    "vanilla_cold_brew" -> R.string.analytics_line_vanilla_cold_brew
    "espresso_con_panna" -> R.string.analytics_line_espresso_con_panna
    else -> R.string.analytics_item_unknown
}

@Composable
fun menuCategoryTitle(categoryKey: String): String = stringResource(menuCategoryTitleRes(categoryKey))

fun menuCategoryTitle(context: Context, categoryKey: String): String =
    context.getString(menuCategoryTitleRes(categoryKey))

private fun menuCategoryTitleRes(key: String): Int = when (key) {
    "grilledBbq" -> R.string.analytics_cat_grilled_bbq
    "entrees" -> R.string.analytics_cat_entrees
    "salads" -> R.string.analytics_cat_salads
    "sides" -> R.string.analytics_cat_sides
    "cocktails" -> R.string.analytics_cat_cocktails
    "appetizers" -> R.string.analytics_cat_appetizers
    "desserts" -> R.string.analytics_cat_desserts
    "riceDishes" -> R.string.analytics_cat_rice_dishes
    "sakeSoju" -> R.string.analytics_cat_sake_soju
    "coldDishes" -> R.string.analytics_cat_cold_dishes
    "hotSoups" -> R.string.analytics_cat_hot_soups
    "noodles" -> R.string.analytics_cat_noodles
    "hotAppetizers" -> R.string.analytics_cat_hot_appetizers
    "tea" -> R.string.analytics_cat_tea
    else -> R.string.analytics_cat_unknown
}

@Composable
fun customerSegmentTitle(nameKey: String): String = stringResource(
    when (nameKey) {
        "returning" -> R.string.analytics_segment_returning
        "new" -> R.string.analytics_segment_new
        else -> R.string.analytics_segment_unknown
    },
)

@Composable
fun visitFrequencyLabel(visitsKey: String): String = stringResource(
    when (visitsKey) {
        "v1" -> R.string.analytics_visit_v1
        "v2_3" -> R.string.analytics_visit_v2_3
        "v4_6" -> R.string.analytics_visit_v4_6
        "v7_10" -> R.string.analytics_visit_v7_10
        "v10p" -> R.string.analytics_visit_v10p
        else -> R.string.analytics_visit_unknown
    },
)

@Composable
fun partySizeRowLabel(sizeKey: String): String = stringResource(
    when (sizeKey) {
        "p1" -> R.string.analytics_party_p1
        "p2" -> R.string.analytics_party_p2
        "p3_4" -> R.string.analytics_party_p3_4
        "p5_6" -> R.string.analytics_party_p5_6
        "p7p" -> R.string.analytics_party_p7p
        else -> R.string.analytics_party_unknown
    },
)

@Composable
fun historyGuestLine(guest: String, guestKey: String?): String =
    if (guestKey == "walk_in") stringResource(R.string.analytics_guest_walk_in) else guest

fun historyGuestLine(context: Context, guest: String, guestKey: String?): String =
    if (guestKey == "walk_in") context.getString(R.string.analytics_guest_walk_in) else guest

@Composable
fun historyNoteText(noteKey: String?): String? =
    noteKey?.let { stringResource(historyNoteRes(it)) }

fun historyNoteText(context: Context, noteKey: String?): String? =
    noteKey?.let { context.getString(historyNoteRes(it)) }

private fun historyNoteRes(key: String): Int = when (key) {
    "anniversary_dinner" -> R.string.analytics_note_anniversary
    "grace_20m" -> R.string.analytics_note_grace_20m
    "cold_soup_refund" -> R.string.analytics_note_cold_soup_refund
    "out_of_stock_refund" -> R.string.analytics_note_out_of_stock_refund
    "no_confirmation" -> R.string.analytics_note_no_confirmation
    else -> R.string.analytics_note_unknown
}

fun tableNumberLabel(context: Context, tableNum: Int): String =
    context.getString(R.string.analytics_table_n, tableNum)

@Composable
fun tableNumberLabel(tableNum: Int): String = stringResource(R.string.analytics_table_n, tableNum)

@Composable
fun receiptLineTitle(item: HistoryReceiptItem): String = stringResource(menuItemTitleRes(item.itemKey))

fun receiptLineTitle(context: Context, item: HistoryReceiptItem): String =
    context.getString(menuItemTitleRes(item.itemKey))

fun historySearchHaystack(context: Context, e: HistoryEvent): String {
    val sb = StringBuilder()
    sb.append(e.id).append(' ')
    sb.append(historyGuestLine(context, e.guest, e.guestKey)).append(' ')
    sb.append(tableNumberLabel(context, e.tableNum)).append(' ')
    sb.append(e.tableNum).append(' ')
    e.paymentKey?.let { sb.append(paymentMethodLabel(context, it)).append(' ') }
    historyNoteText(context, e.noteKey)?.let { sb.append(it).append(' ') }
    e.items.forEach { sb.append(receiptLineTitle(context, it)).append(' ') }
    return sb.toString()
}

fun relDayLabel(context: Context, ms: Long): String {
    val now = System.currentTimeMillis()
    val days = ((now - ms) / HistoryData.DAY).toInt()
    return when {
        days <= 0 -> context.getString(R.string.analytics_rel_today)
        days == 1 -> context.getString(R.string.analytics_rel_yesterday)
        else -> context.getString(R.string.analytics_rel_days_ago, days)
    }
}

fun fmtTimeLocalized(ms: Long): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(ms))

@Composable
fun List<TrendPoint>.toAxisLabels(): List<String> {
    val out = ArrayList<String>(size)
    forEach { out.add(it.axisLabel()) }
    return out
}
