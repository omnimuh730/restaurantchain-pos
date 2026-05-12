package com.mh.restaurantchainpos.pos.ui.analytics

/**
 * Mock data for the Analytics screens. Lifted as faithfully as possible from
 * the React demo (`SalesDashboardView.tsx`, `MenuAnalysisView.tsx`,
 * `CustomerAnalysisView.tsx`, `historyMockData.ts`).
 */

// -----------------------------------------------------------------------------
// Sales Dashboard
// -----------------------------------------------------------------------------

data class TrendPoint(
    /** Axis tick key: hour digits, MON…SUN, W1…W4, JAN…APR, etc. */
    val axisKey: String,
    val revenueUsd: Double,
    val revenueKrw: Long,
    val orders: Int,
)

data class SalesDashboardKpi(
    val totalRev: Double,
    val totalOrders: String,
    val avgTicket: Double,
    val cancels: String,
    val revChange: String,
    val ordChange: String,
    val ticketChange: String,
    val cancelChange: String,
)

data class PaymentSplit(val methodKey: String, val pct: Int, val accent: Long)

object SalesDashboardData {
    val hourly: List<TrendPoint> = listOf(
        TrendPoint("8", 40.0, 680_000, 8),
        TrendPoint("9", 120.0, 1_240_000, 14),
        TrendPoint("10", 85.0, 2_180_000, 28),
        TrendPoint("11", 310.0, 4_620_000, 52),
        TrendPoint("12", 620.0, 5_900_000, 85),
        TrendPoint("13", 540.0, 3_280_000, 72),
        TrendPoint("14", 180.0, 1_640_000, 35),
        TrendPoint("15", 90.0, 460_000, 18),
        TrendPoint("16", 75.0, 320_000, 12),
        TrendPoint("17", 220.0, 1_080_000, 32),
        TrendPoint("18", 1180.0, 2_940_000, 68),
        TrendPoint("19", 1840.0, 3_420_000, 92),
        TrendPoint("20", 1520.0, 4_860_000, 78),
        TrendPoint("21", 880.0, 5_720_000, 45),
        TrendPoint("22", 210.0, 2_380_000, 18),
    )
    val weekly: List<TrendPoint> = listOf(
        TrendPoint("MON", 480.0, 4_180_000, 42),
        TrendPoint("TUE", 1640.0, 1_920_000, 48),
        TrendPoint("WED", 820.0, 6_340_000, 55),
        TrendPoint("THU", 2180.0, 3_260_000, 50),
        TrendPoint("FRI", 1240.0, 5_820_000, 68),
        TrendPoint("SAT", 2940.0, 2_480_000, 82),
        TrendPoint("SUN", 620.0, 4_960_000, 61),
    )
    val monthly: List<TrendPoint> = listOf(
        TrendPoint("W1", 4820.0, 12_360_000, 268),
        TrendPoint("W2", 9240.0, 18_780_000, 312),
        TrendPoint("W3", 6120.0, 26_450_000, 365),
        TrendPoint("W4", 10940.0, 15_260_000, 338),
    )
    val quarterly: List<TrendPoint> = listOf(
        TrendPoint("JAN", 18560.0, 94_200_000, 1080),
        TrendPoint("FEB", 32840.0, 68_400_000, 996),
        TrendPoint("MAR", 24720.0, 112_880_000, 1232),
        TrendPoint("APR", 38900.0, 79_420_000, 1283),
    )

    fun forPeriod(p: Period): List<TrendPoint> = when (p) {
        Period.Today -> hourly
        Period.Month -> monthly
        Period.Quarter -> quarterly
        Period.Week, Period.Custom -> weekly
    }

    val foreignKpis: Map<Period, SalesDashboardKpi> = mapOf(
        Period.Today to SalesDashboardKpi(8450.0, "38", 22.24, "1", "+7.4%", "+2.1%", "+5.2%", "-50%"),
        Period.Week to SalesDashboardKpi(9920.0, "186", 53.34, "4", "-2.8%", "+11.0%", "-12.4%", "+33%"),
        Period.Month to SalesDashboardKpi(31120.0, "682", 45.63, "19", "+18.6%", "+6.4%", "+11.5%", "-14%"),
        Period.Quarter to SalesDashboardKpi(115020.0, "2,108", 54.56, "58", "+21.3%", "+8.2%", "+12.1%", "-6%"),
        Period.Custom to SalesDashboardKpi(9920.0, "186", 53.34, "4", "-2.8%", "+11.0%", "-12.4%", "+33%"),
    )
    val domesticKpis: Map<Period, SalesDashboardKpi> = mapOf(
        Period.Today to SalesDashboardKpi(3_840_000.0, "54", 71_100.0, "3", "-4.1%", "+18.6%", "-19.1%", "+200%"),
        Period.Week to SalesDashboardKpi(28_960_000.0, "312", 92_820.0, "11", "+24.6%", "+3.3%", "+20.6%", "-18%"),
        Period.Month to SalesDashboardKpi(82_840_000.0, "1,486", 55_740.0, "42", "+6.1%", "+14.2%", "-7.0%", "+9%"),
        Period.Quarter to SalesDashboardKpi(324_160_000.0, "4,820", 67_250.0, "172", "+9.8%", "+21.8%", "-9.8%", "-3%"),
        Period.Custom to SalesDashboardKpi(28_960_000.0, "312", 92_820.0, "11", "+24.6%", "+3.3%", "+20.6%", "-18%"),
    )

    val paymentForeign: List<PaymentSplit> = listOf(
        PaymentSplit("credit", 78, 0xFF3B82F6),
        PaymentSplit("cash", 22, 0xFF22C55E),
    )
    val paymentDomestic: List<PaymentSplit> = listOf(
        PaymentSplit("credit", 41, 0xFF3B82F6),
        PaymentSplit("cash", 59, 0xFF22C55E),
    )
}

// -----------------------------------------------------------------------------
// Menu Analysis
// -----------------------------------------------------------------------------

data class MenuItemRow(
    val nameKey: String,
    val categoryKey: String,
    val currency: AnalyticsCurrency,
    val baseQty: Int,
    val basePrice: Double,
    val weeklyBest: List<Int>? = null,
)

object MenuAnalysisData {
    val items: List<MenuItemRow> = listOf(
        MenuItemRow("ribeyeSteak", "grilledBbq", AnalyticsCurrency.Foreign, 102, 45.0, listOf(14, 17, 13, 15, 22, 28, 19)),
        MenuItemRow("grilledSalmon", "grilledBbq", AnalyticsCurrency.Foreign, 128, 20.0),
        MenuItemRow("lobsterTail", "entrees", AnalyticsCurrency.Foreign, 58, 58.0),
        MenuItemRow("caesarSalad", "salads", AnalyticsCurrency.Foreign, 158, 14.0),
        MenuItemRow("truffleFries", "sides", AnalyticsCurrency.Foreign, 182, 8.0),
        MenuItemRow("lycheeMartini", "cocktails", AnalyticsCurrency.Foreign, 94, 12.0),
        MenuItemRow("chickenWings", "appetizers", AnalyticsCurrency.Foreign, 146, 12.0),
        MenuItemRow("tiramisu", "desserts", AnalyticsCurrency.Foreign, 74, 9.0),
        MenuItemRow("fishAndChips", "entrees", AnalyticsCurrency.Foreign, 88, 22.0),
        MenuItemRow("maiTai", "cocktails", AnalyticsCurrency.Foreign, 63, 11.0),
        MenuItemRow("bibimbap", "riceDishes", AnalyticsCurrency.Domestic, 212, 14000.0, listOf(24, 28, 31, 26, 36, 42, 33)),
        MenuItemRow("bulgogi", "grilledBbq", AnalyticsCurrency.Domestic, 165, 18000.0),
        MenuItemRow("soju", "sakeSoju", AnalyticsCurrency.Domestic, 288, 7000.0),
        MenuItemRow("makgeolli", "sakeSoju", AnalyticsCurrency.Domestic, 142, 9000.0),
        MenuItemRow("kimchi", "coldDishes", AnalyticsCurrency.Domestic, 196, 5000.0),
        MenuItemRow("ramen", "hotSoups", AnalyticsCurrency.Domestic, 176, 12000.0),
        MenuItemRow("udonNoodles", "noodles", AnalyticsCurrency.Domestic, 134, 13000.0),
        MenuItemRow("gyoza", "hotAppetizers", AnalyticsCurrency.Domestic, 158, 8000.0),
        MenuItemRow("greenTea", "tea", AnalyticsCurrency.Domestic, 224, 3000.0),
        MenuItemRow("hotSake", "sakeSoju", AnalyticsCurrency.Domestic, 96, 8000.0),
    )

    fun multiplier(p: Period): Double = when (p) {
        Period.Today -> 0.14
        Period.Month -> 4.2
        Period.Quarter -> 12.8
        Period.Week, Period.Custom -> 1.0
    }

    val categoryColors: List<Long> = listOf(
        0xFF3B82F6, 0xFF22C55E, 0xFFF59E0B, 0xFFA855F7,
        0xFFEF4444, 0xFF14B8A6, 0xFFEC4899,
    )

    val weekAxis = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
}

// -----------------------------------------------------------------------------
// Customer Analysis
// -----------------------------------------------------------------------------

data class CustomerKpi(
    val totalCust: String,
    val newCust: String,
    val returning: String,
    val satisfaction: String,
    val custChange: String,
    val newChange: String,
    val retChange: String,
    val satChange: String,
)

data class VisitFrequencyRow(val visitsKey: String, val customers: Int)
data class CustomerSegment(val nameKey: String, val value: Int, val accent: Long)
data class HourTraffic(val hour: String, val customers: Int)
data class PartySize(val sizeKey: String, val pct: Int)

object CustomerAnalysisData {
    val kpiByPeriod: Map<Period, CustomerKpi> = mapOf(
        Period.Today to CustomerKpi("82", "14", "58%", "4.7", "+4.1%", "+8.5%", "+1.2%", "+0.1"),
        Period.Week to CustomerKpi("1,284", "186", "62%", "4.6", "+8.3%", "+14.2%", "+3.1%", "-0.1"),
        Period.Month to CustomerKpi("5,120", "742", "65%", "4.6", "+10.1%", "+12.8%", "+4.5%", "+0.2"),
        Period.Quarter to CustomerKpi("14,860", "2,104", "63%", "4.5", "+12.6%", "+16.4%", "+2.8%", "-0.2"),
        Period.Custom to CustomerKpi("1,284", "186", "62%", "4.6", "+8.3%", "+14.2%", "+3.1%", "-0.1"),
    )

    val segmentByPeriod: Map<Period, List<CustomerSegment>> = mapOf(
        Period.Today to listOf(CustomerSegment("returning", 58, 0xFF3B82F6), CustomerSegment("new", 42, 0xFF22C55E)),
        Period.Week to listOf(CustomerSegment("returning", 62, 0xFF3B82F6), CustomerSegment("new", 38, 0xFF22C55E)),
        Period.Month to listOf(CustomerSegment("returning", 65, 0xFF3B82F6), CustomerSegment("new", 35, 0xFF22C55E)),
        Period.Quarter to listOf(CustomerSegment("returning", 63, 0xFF3B82F6), CustomerSegment("new", 37, 0xFF22C55E)),
        Period.Custom to listOf(CustomerSegment("returning", 62, 0xFF3B82F6), CustomerSegment("new", 38, 0xFF22C55E)),
    )

    val visitFreqByPeriod: Map<Period, List<VisitFrequencyRow>> = mapOf(
        Period.Today to listOf(
            VisitFrequencyRow("v1", 34),
            VisitFrequencyRow("v2_3", 26),
            VisitFrequencyRow("v4_6", 14),
            VisitFrequencyRow("v7_10", 6),
            VisitFrequencyRow("v10p", 2),
        ),
        Period.Week to listOf(
            VisitFrequencyRow("v1", 486),
            VisitFrequencyRow("v2_3", 384),
            VisitFrequencyRow("v4_6", 228),
            VisitFrequencyRow("v7_10", 124),
            VisitFrequencyRow("v10p", 62),
        ),
        Period.Month to listOf(
            VisitFrequencyRow("v1", 1792),
            VisitFrequencyRow("v2_3", 1536),
            VisitFrequencyRow("v4_6", 1024),
            VisitFrequencyRow("v7_10", 512),
            VisitFrequencyRow("v10p", 256),
        ),
        Period.Quarter to listOf(
            VisitFrequencyRow("v1", 5202),
            VisitFrequencyRow("v2_3", 4158),
            VisitFrequencyRow("v4_6", 2972),
            VisitFrequencyRow("v7_10", 1636),
            VisitFrequencyRow("v10p", 892),
        ),
        Period.Custom to listOf(
            VisitFrequencyRow("v1", 486),
            VisitFrequencyRow("v2_3", 384),
            VisitFrequencyRow("v4_6", 228),
            VisitFrequencyRow("v7_10", 124),
            VisitFrequencyRow("v10p", 62),
        ),
    )

    val partySize: List<PartySize> = listOf(
        PartySize("p1", 8),
        PartySize("p2", 32),
        PartySize("p3_4", 38),
        PartySize("p5_6", 15),
        PartySize("p7p", 7),
    )

    val hourlyTraffic: List<HourTraffic> = listOf(
        HourTraffic("8", 12),
        HourTraffic("9", 28),
        HourTraffic("10", 45),
        HourTraffic("11", 82),
        HourTraffic("12", 120),
        HourTraffic("13", 105),
        HourTraffic("14", 52),
        HourTraffic("15", 28),
        HourTraffic("16", 18),
        HourTraffic("17", 48),
        HourTraffic("18", 98),
        HourTraffic("19", 135),
        HourTraffic("20", 110),
        HourTraffic("21", 65),
        HourTraffic("22", 22),
    )
}

// -----------------------------------------------------------------------------
// History
// -----------------------------------------------------------------------------

enum class HistoryKind(val id: String) {
    Order("order"),
    Reservation("reservation"),
    Payment("payment"),
    NoShow("no-show"),
    WalkIn("walk-in"),
}

enum class HistoryStatus(val id: String) {
    Completed("completed"),
    Paid("paid"),
    NoShow("no-show"),
    Refunded("refunded"),
}

data class HistoryReceiptItem(
    val itemKey: String,
    val qty: Int,
    val price: Double,
    val currency: AnalyticsCurrency = AnalyticsCurrency.Foreign,
)

data class HistoryEvent(
    val id: String,
    val kind: HistoryKind,
    val guest: String,
    val guestKey: String? = null,
    val tableNum: Int,
    val amountUsd: Double = 0.0,
    val amountKrw: Long = 0L,
    val status: HistoryStatus,
    val timestampMs: Long,
    val partySize: Int? = null,
    val reservedAt: String? = null,
    val arrivedAt: String? = null,
    val paidAt: String? = null,
    val paymentKey: String? = null,
    val noteKey: String? = null,
    val items: List<HistoryReceiptItem> = emptyList(),
    val linkedToId: String? = null,
)

object HistoryData {
    private const val MIN: Long = 60_000L
    private const val HOUR: Long = 60L * MIN
    const val DAY: Long = 24L * HOUR

    private fun ago(days: Int, hours: Int = 0, minutes: Int = 0): Long =
        System.currentTimeMillis() - days * DAY - hours * HOUR - minutes * MIN

    val events: List<HistoryEvent> = listOf(
        HistoryEvent(
            id = "h-t1",
            kind = HistoryKind.Payment,
            guest = "Park K.",
            tableNum = 2,
            amountUsd = 156.50,
            status = HistoryStatus.Completed,
            timestampMs = ago(0, 6, 25),
            partySize = 4,
            paidAt = "12:35",
            paymentKey = "credit_card",
            items = listOf(
                HistoryReceiptItem("americano", 2, 3.50),
                HistoryReceiptItem("cafe_latte", 1, 4.00),
                HistoryReceiptItem("honey_cold_brew", 1, 5.50),
                HistoryReceiptItem("croissant", 2, 4.00),
                HistoryReceiptItem("tiramisu", 1, 6.50),
            ),
        ),
        HistoryEvent(
            id = "h-t2",
            kind = HistoryKind.Payment,
            guest = "Lee S.",
            tableNum = 3,
            amountUsd = 41.00,
            amountKrw = 88_000,
            status = HistoryStatus.Paid,
            timestampMs = ago(0, 5, 47),
            partySize = 3,
            paidAt = "01:12 PM",
            paymentKey = "cash",
        ),
        HistoryEvent(
            id = "h-t3",
            kind = HistoryKind.Order,
            guest = "Choi M.",
            tableNum = 10,
            amountUsd = 17.50,
            status = HistoryStatus.Completed,
            timestampMs = ago(0, 4, 12),
            partySize = 5,
            paymentKey = "credit_card",
            items = listOf(
                HistoryReceiptItem("vanilla_cold_brew", 2, 6.50),
                HistoryReceiptItem("espresso_con_panna", 1, 4.50),
            ),
        ),
        HistoryEvent(
            id = "h-r1",
            kind = HistoryKind.Reservation,
            guest = "Kim M.",
            tableNum = 2,
            status = HistoryStatus.Completed,
            timestampMs = ago(1, 2, 0),
            partySize = 4,
            reservedAt = "18:00",
            arrivedAt = "18:05",
            paidAt = "20:11",
            noteKey = "anniversary_dinner",
        ),
        HistoryEvent(
            id = "h-n1",
            kind = HistoryKind.NoShow,
            guest = "Lim S.",
            tableNum = 3,
            status = HistoryStatus.NoShow,
            timestampMs = ago(1, 1, 30),
            partySize = 4,
            reservedAt = "19:00",
            noteKey = "grace_20m",
        ),
        HistoryEvent(
            id = "h-w1",
            kind = HistoryKind.WalkIn,
            guest = "",
            guestKey = "walk_in",
            tableNum = 3,
            amountUsd = 41.00,
            amountKrw = 88_000,
            status = HistoryStatus.Completed,
            timestampMs = ago(1, 1, 5),
            partySize = 3,
            arrivedAt = "19:20",
            paidAt = "20:55",
            paymentKey = "cash",
            linkedToId = "h-n1",
            items = listOf(
                HistoryReceiptItem("bibimbap", 2, 14_000.0, AnalyticsCurrency.Domestic),
                HistoryReceiptItem("bulgogi", 1, 18_000.0, AnalyticsCurrency.Domestic),
                HistoryReceiptItem("greenTea", 3, 3_000.0, AnalyticsCurrency.Domestic),
            ),
        ),
        HistoryEvent(
            id = "h-r2",
            kind = HistoryKind.Reservation,
            guest = "Jung H.",
            tableNum = 8,
            status = HistoryStatus.Completed,
            timestampMs = ago(2, 0, 30),
            partySize = 3,
            reservedAt = "21:00",
            arrivedAt = "21:08",
            paidAt = "23:00",
        ),
        HistoryEvent(
            id = "h-rf1",
            kind = HistoryKind.Payment,
            guest = "Yoo N.",
            tableNum = 5,
            amountUsd = 22.00,
            status = HistoryStatus.Refunded,
            timestampMs = ago(3),
            partySize = 2,
            paidAt = "13:40",
            paymentKey = "credit_card",
            noteKey = "cold_soup_refund",
        ),
        HistoryEvent(
            id = "h-o1",
            kind = HistoryKind.Order,
            guest = "Oh S.",
            tableNum = 2,
            amountKrw = 64_000,
            status = HistoryStatus.Completed,
            timestampMs = ago(4, 5, 0),
            partySize = 4,
            items = listOf(
                HistoryReceiptItem("bibimbap", 2, 14_000.0, AnalyticsCurrency.Domestic),
                HistoryReceiptItem("soju", 2, 7_000.0, AnalyticsCurrency.Domestic),
                HistoryReceiptItem("kimchi", 2, 5_000.0, AnalyticsCurrency.Domestic),
                HistoryReceiptItem("greenTea", 2, 3_000.0, AnalyticsCurrency.Domestic),
            ),
        ),
        HistoryEvent(
            id = "h-r3",
            kind = HistoryKind.Reservation,
            guest = "Han B.",
            tableNum = 9,
            status = HistoryStatus.Completed,
            timestampMs = ago(5),
            partySize = 6,
            reservedAt = "20:00",
            arrivedAt = "20:02",
            paidAt = "22:35",
            paymentKey = "credit_card",
        ),
        HistoryEvent(
            id = "h-o2",
            kind = HistoryKind.Order,
            guest = "Bae J.",
            tableNum = 7,
            amountUsd = 78.50,
            status = HistoryStatus.Completed,
            timestampMs = ago(5, 2, 0),
            partySize = 2,
            items = listOf(
                HistoryReceiptItem("ribeyeSteak", 1, 45.0),
                HistoryReceiptItem("caesarSalad", 1, 14.0),
                HistoryReceiptItem("lycheeMartini", 1, 12.0),
            ),
        ),
        HistoryEvent(
            id = "h-w2",
            kind = HistoryKind.WalkIn,
            guest = "Ji N.",
            tableNum = 1,
            amountKrw = 32_000,
            status = HistoryStatus.Completed,
            timestampMs = ago(6),
            partySize = 2,
            arrivedAt = "12:15",
            paidAt = "13:02",
        ),
        HistoryEvent(
            id = "h-n2",
            kind = HistoryKind.NoShow,
            guest = "Cho R.",
            tableNum = 6,
            status = HistoryStatus.NoShow,
            timestampMs = ago(7, 3, 0),
            partySize = 2,
            reservedAt = "20:00",
            noteKey = "no_confirmation",
        ),
        HistoryEvent(
            id = "h-pay4",
            kind = HistoryKind.Payment,
            guest = "Seo J.",
            tableNum = 4,
            amountUsd = 96.20,
            status = HistoryStatus.Paid,
            timestampMs = ago(8),
            partySize = 3,
            paidAt = "21:18",
            paymentKey = "credit_card",
        ),
        HistoryEvent(
            id = "h-r4",
            kind = HistoryKind.Reservation,
            guest = "Moon S.",
            tableNum = 11,
            status = HistoryStatus.Completed,
            timestampMs = ago(10),
            partySize = 4,
            reservedAt = "19:30",
            arrivedAt = "19:33",
            paidAt = "21:45",
            paymentKey = "cash",
        ),
        HistoryEvent(
            id = "h-o3",
            kind = HistoryKind.Order,
            guest = "Ryu T.",
            tableNum = 12,
            amountKrw = 122_000,
            status = HistoryStatus.Completed,
            timestampMs = ago(12),
            partySize = 5,
            items = listOf(
                HistoryReceiptItem("bulgogi", 2, 18_000.0, AnalyticsCurrency.Domestic),
                HistoryReceiptItem("bibimbap", 1, 14_000.0, AnalyticsCurrency.Domestic),
                HistoryReceiptItem("makgeolli", 2, 9_000.0, AnalyticsCurrency.Domestic),
                HistoryReceiptItem("gyoza", 4, 8_000.0, AnalyticsCurrency.Domestic),
            ),
        ),
        HistoryEvent(
            id = "h-w3",
            kind = HistoryKind.WalkIn,
            guest = "Kang M.",
            tableNum = 8,
            amountUsd = 34.80,
            status = HistoryStatus.Completed,
            timestampMs = ago(15),
            partySize = 2,
            arrivedAt = "11:42",
            paidAt = "12:35",
        ),
        HistoryEvent(
            id = "h-r5",
            kind = HistoryKind.Reservation,
            guest = "Yoon E.",
            tableNum = 2,
            status = HistoryStatus.Completed,
            timestampMs = ago(20),
            partySize = 6,
            reservedAt = "18:30",
            arrivedAt = "18:35",
            paidAt = "20:55",
        ),
        HistoryEvent(
            id = "h-pay5",
            kind = HistoryKind.Payment,
            guest = "Hong M.",
            tableNum = 9,
            amountUsd = 188.30,
            status = HistoryStatus.Completed,
            timestampMs = ago(25),
            partySize = 6,
            paidAt = "22:11",
            paymentKey = "credit_card",
        ),
        HistoryEvent(
            id = "h-rf2",
            kind = HistoryKind.Payment,
            guest = "Park J.",
            tableNum = 10,
            amountKrw = 24_000,
            status = HistoryStatus.Refunded,
            timestampMs = ago(28),
            partySize = 1,
            paymentKey = "cash",
            noteKey = "out_of_stock_refund",
        ),
    )
}
