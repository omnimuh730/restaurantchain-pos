package com.mh.restaurantchainpos.pos.data

object PosMockData {
    val menuCategories = listOf(
        MenuCategory(
            id = "hot-foods",
            label = "Hot Foods",
            subCategories = listOf(
                MenuSubCategory("dumplings", "Dumplings", listOf(
                    MenuItem("pork-dumplings", "Pork Dumplings", 8.0),
                    MenuItem("shrimp-dumplings", "Shrimp Dumplings", 9.0),
                    MenuItem("vegetable-dumplings", "Vegetable Dumplings", 7.0),
                    MenuItem("soup-dumplings", "Soup Dumplings", 10.0),
                )),
                MenuSubCategory("spring-rolls", "Spring Rolls", listOf(
                    MenuItem("veggie-spring-roll", "Vegetable Spring Rolls", 6.0),
                    MenuItem("pork-spring-roll", "Pork Spring Rolls", 7.0),
                    MenuItem("shrimp-spring-roll", "Shrimp Spring Rolls", 8.0),
                )),
                MenuSubCategory("bao-buns", "Bao Buns", listOf(
                    MenuItem("pork-belly-bao", "Pork Belly Bao", 9000.0, CurrencyKind.Domestic),
                    MenuItem("chicken-bao", "Chicken Bao", 8000.0, CurrencyKind.Domestic),
                    MenuItem("veggie-bao", "Vegetable Bao", 7000.0, CurrencyKind.Domestic),
                )),
                MenuSubCategory("hot-soups", "Hot Soups", listOf(
                    MenuItem("miso-soup", "Miso Soup", 5000.0, CurrencyKind.Domestic),
                    MenuItem("hot-sour-soup", "Hot & Sour Soup", 6.0),
                    MenuItem("ramen", "Ramen", 12000.0, CurrencyKind.Domestic),
                    MenuItem("pho", "Pho", 13.0),
                )),
            ),
        ),
        MenuCategory(
            id = "cold-foods",
            label = "Cold Foods",
            subCategories = listOf(
                MenuSubCategory("sushi-sashimi", "Sushi & Sashimi", listOf(
                    MenuItem("salmon-sushi", "Salmon Sushi", 8.0),
                    MenuItem("tuna-sushi", "Tuna Sushi", 9.0),
                    MenuItem("california-roll", "California Roll", 10.0),
                    MenuItem("sashimi-platter", "Sashimi Platter", 20.0),
                )),
                MenuSubCategory("cold-appetizers", "Cold Appetizers", listOf(
                    MenuItem("seaweed-salad", "Seaweed Salad", 6.0),
                    MenuItem("kimchi", "Kimchi", 5000.0, CurrencyKind.Domestic),
                    MenuItem("pickled-vegetables", "Pickled Vegetables", 5000.0, CurrencyKind.Domestic),
                )),
                MenuSubCategory("salads", "Salads", listOf(
                    MenuItem("asian-chicken-salad", "Asian Chicken Salad", 10.0),
                    MenuItem("cucumber-salad", "Cucumber Salad", 6.0),
                    MenuItem("papaya-salad", "Papaya Salad", 8.0),
                )),
            ),
        ),
        MenuCategory(
            id = "main-meal",
            label = "Main Meal",
            subCategories = listOf(
                MenuSubCategory("rice-dishes", "Rice Dishes", listOf(
                    MenuItem("fried-rice", "Fried Rice", 12.0),
                    MenuItem("bibimbap", "Bibimbap", 14000.0, CurrencyKind.Domestic),
                    MenuItem("curry-rice", "Curry Rice", 13.0),
                    MenuItem("donburi", "Chicken Donburi", 14000.0, CurrencyKind.Domestic),
                )),
                MenuSubCategory("noodle-dishes", "Noodle Dishes", listOf(
                    MenuItem("pad-thai", "Pad Thai", 13.0),
                    MenuItem("chow-mein", "Chow Mein", 12.0),
                    MenuItem("lo-mein", "Lo Mein", 12.0),
                    MenuItem("udon", "Udon Noodles", 13000.0, CurrencyKind.Domestic),
                    MenuItem("dan-dan-noodles", "Dan Dan Noodles", 11.0),
                )),
                MenuSubCategory("grilled-bbq", "Grilled & BBQ", listOf(
                    MenuItem("teriyaki-chicken", "Teriyaki Chicken", 16000.0, CurrencyKind.Domestic),
                    MenuItem("bulgogi", "Bulgogi", 18000.0, CurrencyKind.Domestic),
                    MenuItem("grilled-salmon", "Grilled Salmon", 20.0),
                    MenuItem("yakitori", "Yakitori", 12000.0, CurrencyKind.Domestic),
                )),
            ),
        ),
        MenuCategory(
            id = "drinks",
            label = "Drinks",
            subCategories = listOf(
                MenuSubCategory("tea", "Tea", listOf(
                    MenuItem("green-tea", "Green Tea", 3000.0, CurrencyKind.Domestic),
                    MenuItem("jasmine-tea", "Jasmine Tea", 3000.0, CurrencyKind.Domestic),
                    MenuItem("bubble-tea", "Bubble Tea", 5000.0, CurrencyKind.Domestic),
                    MenuItem("thai-tea", "Thai Tea", 4.5),
                )),
                MenuSubCategory("beer", "Beer", listOf(
                    MenuItem("asahi", "Asahi", 7.0),
                    MenuItem("sapporo", "Sapporo", 7.0),
                    MenuItem("singha", "Singha", 6.0),
                )),
                MenuSubCategory("cocktails", "Cocktails", listOf(
                    MenuItem("lychee-martini", "Lychee Martini", 12.0),
                    MenuItem("sake-bomb", "Sake Bomb", 10.0),
                    MenuItem("mai-tai", "Mai Tai", 11.0),
                )),
            ),
        ),
    )

    val tables = listOf(
        "T1" to "Table 1", "T2" to "Table 2", "T3" to "Table 3", "T4" to "Table 4", "T5" to "Table 5",
        "T6" to "Table 6", "T7" to "Table 7", "T8" to "Table 8", "T9" to "Table 9", "T10" to "Table 10",
        "T11" to "Table 11", "T12" to "Table 12", "BAR1" to "Bar 1", "BAR2" to "Bar 2", "BAR3" to "Bar 3",
    )

    val initialOrders = mapOf(
        "T12" to listOf(
            OrderItem("lychee-martini", "lychee-martini", "Lychee Martini", 12.0, 2, "COCKTAILS", CurrencyKind.Foreign, ordered = true),
            OrderItem("wings", "wings", "Chicken Wings", 12.0, 1, "APPETIZERS", CurrencyKind.Foreign, ordered = true),
            OrderItem("grilled-salmon", "grilled-salmon", "Grilled Salmon", 20.0, 1, "GRILLED BBQ", CurrencyKind.Foreign, ordered = true),
            OrderItem("bulgogi", "bulgogi", "Bulgogi", 18000.0, 1, "GRILLED BBQ", CurrencyKind.Domestic, ordered = true),
        ),
    )

    private val cafeItems = listOf(
        TableOrderItem("americano", 2, 3500),
        TableOrderItem("cafe_latte", 1, 4000),
        TableOrderItem("flat_white", 1, 4500),
        TableOrderItem("honey_cold_brew", 1, 5500),
        TableOrderItem("tiramisu", 1, 6500),
    )

    private val shortCafeItems = listOf(
        TableOrderItem("espresso_con_panna", 1, 4500),
        TableOrderItem("honey_cold_brew", 2, 5500),
        TableOrderItem("oat_cold_brew", 3, 6500),
        TableOrderItem("tiramisu", 1, 6000),
    )

    val floors = listOf(
        Floor("f1", "Hall", listOf(
            FloorTable("T1", "Table 1", 2, TableShape.Rect, 48, 48, 72, 72, TableStatus.Available),
            FloorTable("T2", "Table 2", 4, TableShape.Circle, 216, 48, 144, 216, TableStatus.Occupied, 156500, 4, "Park K.", orderItems = cafeItems),
            FloorTable("T3", "Table 3", 4, TableShape.Rect, 480, 48, 144, 144, TableStatus.Occupied, 41000, 3, "Lee S.", orderItems = shortCafeItems),
            FloorTable("T4", "Table 4", 2, TableShape.Rect, 504, 216, 72, 72, TableStatus.Available),
            FloorTable("T5", "Table 5", 4, TableShape.Rect, 48, 360, 216, 72, TableStatus.Available),
            FloorTable("T6", "Table 6", 6, TableShape.Rect, 48, 504, 144, 72, TableStatus.Available),
            FloorTable("T7", "Table 7", 2, TableShape.Rect, 264, 504, 144, 72, TableStatus.Available),
            FloorTable("T8", "Table 8", 4, TableShape.Rect, 480, 504, 144, 72, TableStatus.Available),
            FloorTable("T9", "Table 9", 6, TableShape.Circle, 696, 480, 144, 144, TableStatus.Available),
            FloorTable("T10", "Table 10", 6, TableShape.Rect, 552, 336, 216, 144, TableStatus.Occupied, 17500, 5, "Choi M.", orderItems = listOf(TableOrderItem("vanilla_cold_brew", 2, 6500), TableOrderItem("espresso_con_panna", 1, 4500))),
            FloorTable("T11", "Table 11", 4, TableShape.Rect, 48, 648, 144, 72, TableStatus.Available),
        )),
        Floor("f2", "Lounge", listOf(
            FloorTable("T12", "Table 1", 2, TableShape.Rect, 72, 48, 72, 72, TableStatus.Available),
            FloorTable("T13", "Table 2", 4, TableShape.Rect, 264, 48, 144, 72, TableStatus.Reserved, guestName = "Ji N.", reservationTime = "12:38"),
            FloorTable("T14", "Table 3", 4, TableShape.Rect, 480, 48, 144, 72, TableStatus.Available),
            FloorTable("T15", "Table 4", 6, TableShape.Rect, 72, 192, 144, 144, TableStatus.Available),
            FloorTable("T16", "Table 5", 2, TableShape.Rect, 288, 192, 72, 72, TableStatus.Available),
            FloorTable("T17", "Table 6", 4, TableShape.Rect, 456, 192, 144, 72, TableStatus.Available),
            FloorTable("T18", "Table 7", 2, TableShape.Rect, 72, 360, 72, 72, TableStatus.Available),
            FloorTable("T19", "Table 8", 4, TableShape.Rect, 264, 360, 144, 72, TableStatus.Available),
        )),
    )

    val kitchenFloors = listOf(
        KitchenFloor("1F", "1F", listOf("T1", "T2", "T3", "T4", "T5")),
        KitchenFloor("2F", "2F", listOf("T6", "T7", "T8", "T9", "T10", "T11", "T12", "T13", "T14", "T15", "T16", "T17", "T18", "T19", "T20")),
        KitchenFloor("bar", "Bar", listOf("BAR1", "BAR2", "BAR3")),
    )

    val reservations = listOf(
        Reservation("r1", "T2", "Kim M.", 4, "18:00", 2.0, 0, ReservationType.Confirmed),
        Reservation("r2", "T3", "Park S.", 2, "19:00", 1.5, 0, ReservationType.Confirmed),
        Reservation("r3", "T5", "Lee J.", 6, "20:00", 2.0, 0, ReservationType.Confirmed),
        Reservation("r4", "T6", "Choi K.", 4, "18:30", 2.0, 0, ReservationType.Confirmed),
        Reservation("r5", "T8", "Jung H.", 3, "21:00", 1.5, 0, ReservationType.Request),
        Reservation("r6", "T1", "Yoon A.", 2, "19:30", 1.0, 0, ReservationType.Confirmed),
        Reservation("r7", "", "Yoo N.", 4, "17:00", 1.5, 0, ReservationType.Request),
        Reservation("r8", "", "Han D.", 2, "18:30", 1.0, 0, ReservationType.Request),
        Reservation("r9", "", "Kang N.", 4, "17:30", 2.0, 0, ReservationType.Request),
        Reservation("r10", "", "Shin B.", 6, "19:00", 2.0, 0, ReservationType.Request),
        Reservation("r11", "T9", "Song Y.", 4, "19:00", 1.5, 0, ReservationType.Request),
        Reservation("r12", "T10", "Im W.", 6, "17:30", 2.5, 0, ReservationType.Confirmed),
        Reservation("r13", "T2", "Oh S.", 4, "12:00", 2.0, 2, ReservationType.Confirmed),
        Reservation("r14", "", "Baek J.", 8, "18:00", 3.0, 2, ReservationType.Request),
        Reservation("r15", "T5", "Seo H.", 2, "19:30", 1.5, 2, ReservationType.Confirmed),
        Reservation("r16", "", "Hwang D.", 4, "20:00", 2.0, 2, ReservationType.Request),
        Reservation("r17", "T1", "Moon Y.", 2, "11:00", 1.0, 2, ReservationType.Confirmed),
        Reservation("r18", "T3", "Ryu K.", 6, "17:00", 2.5, 3, ReservationType.Confirmed),
        Reservation("r19", "", "Jang M.", 4, "19:00", 2.0, 3, ReservationType.Request),
        Reservation("r20", "", "Kwon T.", 2, "13:00", 1.5, 3, ReservationType.Request),
        Reservation("r21", "T6", "Na E.", 8, "18:30", 2.0, 3, ReservationType.Confirmed),
        Reservation("r22", "", "Ahn S.", 6, "20:30", 2.0, 3, ReservationType.Request),
        Reservation("p1", "T2", "Cho W.", 4, "12:00", 2.0, -1, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p2", "T5", "Bae R.", 2, "13:30", 1.0, -1, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p3", "T6", "Kim E.", 6, "18:00", 2.5, -1, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p4", "T3", "Lim S.", 4, "19:00", 2.0, -1, ReservationType.Confirmed, "NO_SHOW"),
        Reservation("p5", "T8", "Park J.", 3, "20:00", 1.5, -1, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p6", "T1", "Go H.", 2, "21:00", 1.0, -1, ReservationType.Confirmed, "NO_SHOW"),
        Reservation("p4w", "T3", "Walk-in", 3, "19:20", 1.5, -1, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p6w", "T1", "Walk-in", 2, "21:20", 1.0, -1, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p7", "T2", "Seong L.", 4, "11:30", 2.0, -2, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p8", "T9", "Jeon Y.", 6, "13:00", 2.5, -2, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p9", "T5", "Noh K.", 4, "18:30", 2.0, -2, ReservationType.Confirmed, "NO_SHOW"),
        Reservation("p9w", "T5", "Walk-in", 4, "18:50", 1.5, -2, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p10", "T10", "Ha D.", 6, "19:30", 2.5, -2, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p11", "T1", "Min C.", 2, "21:00", 1.0, -2, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p12", "T3", "Koo B.", 4, "12:00", 2.0, -3, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p13", "T6", "Yoo M.", 6, "17:30", 2.5, -3, ReservationType.Confirmed, "NO_SHOW"),
        Reservation("p13w", "T6", "Walk-in", 5, "17:50", 2.0, -3, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p14", "T8", "Sun R.", 3, "19:00", 1.5, -3, ReservationType.Confirmed, "COMPLETED"),
        Reservation("p15", "T2", "Oh D.", 4, "20:00", 2.0, -3, ReservationType.Confirmed, "COMPLETED"),
    )

    val kitchenOrders = listOf(
        KitchenOrder("k1", "T3", KitchenStatus.InProgress, 20, items = listOf(KitchenItem("k1a", "seafood-pasta", 2), KitchenItem("k1b", "white-rice-cake", 1, done = true), KitchenItem("k1c", "mango-juice", 3, done = true))),
        KitchenOrder("k2", "T1", KitchenStatus.InProgress, 10, items = listOf(KitchenItem("k2a", "t-bone-steak", 1, modifier = "Medium rare"), KitchenItem("k2b", "sirloin-steak", 2, previouslyCompleted = true))),
        KitchenOrder("k5", "T7", KitchenStatus.InProgress, 30, items = listOf(KitchenItem("k5a", "pad-thai", 2, done = true), KitchenItem("k5b", "thai-green-curry", 1), KitchenItem("k5c", "tom-yum-soup", 1, done = true))),
        KitchenOrder("k21", "T4", KitchenStatus.Received, 1, items = listOf(KitchenItem("k21a", "bulgogi", 2), KitchenItem("k21b", "steamed-rice", 2))),
        KitchenOrder("k22", "T10", KitchenStatus.Received, 1, items = listOf(KitchenItem("k22a", "udon", 1), KitchenItem("k22b", "tempura", 1))),
        KitchenOrder("k11", "T6", KitchenStatus.Completed, 55, 35, listOf(KitchenItem("k11a", "teriyaki-chicken", 1), KitchenItem("k11b", "udon", 1))),
    )

    val staff = listOf(
        StaffMember(
            id = "2",
            name = "Jamie Chen",
            username = "jamie.chen",
            role = "Chef",
            status = "active",
            joinDate = "Mar 2023",
            permissionCount = 2,
            permissions = mapOf("kitchen" to true, "settings-password" to true),
        ),
        StaffMember(
            id = "3",
            name = "Sam Rivera",
            username = "sam.rivera",
            role = "Waiter",
            status = "active",
            joinDate = "Jun 2023",
            permissionCount = 4,
            permissions = mapOf(
                "orders" to true,
                "kitchen" to true,
                "settings-password" to true,
                "take-orders" to true,
            ),
        ),
        StaffMember(
            id = "4",
            name = "Taylor Kim",
            username = "taylor.kim",
            role = "Cashier",
            status = "active",
            joinDate = "Jan 2024",
            permissionCount = 7,
            permissions = mapOf(
                "floor-plan" to true,
                "reservations" to true,
                "orders" to true,
                "kitchen" to true,
                "settings-password" to true,
                "take-orders" to true,
                "process-payment" to true,
                "menu-management" to true,
            ),
        ),
        StaffMember(
            id = "5",
            name = "Casey Park",
            username = "casey.park",
            role = "Cashier",
            status = "active",
            joinDate = "Aug 2024",
            permissionCount = 6,
            permissions = mapOf(
                "floor-plan" to true,
                "reservations" to true,
                "orders" to true,
                "kitchen" to true,
                "settings-password" to true,
                "take-orders" to true,
                "process-payment" to true,
            ),
        ),
        StaffMember(
            id = "6",
            name = "Riley Thompson",
            username = "riley.t",
            role = "Waiter",
            status = "active",
            joinDate = "Feb 2025",
            permissionCount = 4,
            permissions = mapOf(
                "orders" to true,
                "kitchen" to true,
                "settings-password" to true,
                "take-orders" to true,
            ),
        ),
        StaffMember(
            id = "7",
            name = "Morgan Davis",
            username = "morgan.d",
            role = "Chef",
            status = "active",
            joinDate = "Apr 2024",
            permissionCount = 2,
            permissions = mapOf("kitchen" to true, "settings-password" to true),
        ),
        StaffMember(
            id = "8",
            name = "Jordan Lee",
            username = "jordan.lee",
            role = "Waiter",
            status = "inactive",
            joinDate = "Oct 2024",
            permissionCount = 4,
            permissions = mapOf(
                "orders" to true,
                "kitchen" to true,
                "settings-password" to true,
                "take-orders" to true,
            ),
        ),
        StaffMember(
            id = "9",
            name = "Alex Nguyen",
            username = "alex.nguyen",
            role = "Waiter",
            status = "pending",
            joinDate = "Apr 2026",
            permissionCount = 4,
            permissions = mapOf(
                "orders" to true,
                "kitchen" to true,
                "settings-password" to true,
                "take-orders" to true,
            ),
        ),
    )

    val paymentCards = listOf(
        PaymentCard("credit card", "4242", "12/26", "Alex Morgan", true),
        PaymentCard("credit card", "8888", "03/27", "Glass Onion LLC"),
    )

    val analytics = listOf(
        AnalyticsPoint("8", 40, 680000, 8),
        AnalyticsPoint("9", 120, 1240000, 14),
        AnalyticsPoint("10", 85, 2180000, 28),
        AnalyticsPoint("11", 310, 4620000, 52),
        AnalyticsPoint("12", 620, 5900000, 85),
        AnalyticsPoint("18", 1180, 2940000, 68),
        AnalyticsPoint("19", 1840, 3420000, 92),
        AnalyticsPoint("20", 1520, 4860000, 78),
        AnalyticsPoint("21", 880, 5720000, 45),
    )

    val history = listOf(
        HistoryEvent("#1084", "order", "Park K.", "T2", 156500, "completed", "Dinner order completed"),
        HistoryEvent("#1085", "payment", "Choi M.", "T10", 17500, "paid", "Credit card approved"),
        HistoryEvent("#1086", "reservation", "Kim M.", "T2", 0, "completed", "Confirmed reservation"),
        HistoryEvent("#1087", "no-show", "Lim S.", "T3", 0, "no-show", "Table released after grace period"),
        HistoryEvent("#1088", "walk-in", "Walk-in", "T3", 41000, "completed", "Filled released reservation"),
    )

}

fun formatMoney(value: Double, currency: CurrencyKind): String =
    when (currency) {
        CurrencyKind.Foreign -> "$" + "%,.2f".format(value)
        CurrencyKind.Domestic -> "₩" + "%,.0f".format(value)
    }
