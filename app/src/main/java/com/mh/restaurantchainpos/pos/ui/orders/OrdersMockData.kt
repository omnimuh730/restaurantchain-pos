package com.mh.restaurantchainpos.pos.ui.orders

import com.mh.restaurantchainpos.pos.data.CurrencyKind

internal val OrderFloors = listOf(
    OrderFloor("1F", "1st Floor"),
    OrderFloor("2F", "2nd Floor"),
    OrderFloor("bar", "Bar"),
)

internal val OrderTables = listOf(
    OrderTable("T1", "Table 1", 2, "1F"),
    OrderTable("T2", "Table 2", 4, "1F"),
    OrderTable("T3", "Table 3", 4, "1F"),
    OrderTable("T4", "Table 4", 6, "1F"),
    OrderTable("T5", "Table 5", 2, "1F"),
    OrderTable("T6", "Table 6", 8, "2F"),
    OrderTable("T7", "Table 7", 4, "2F"),
    OrderTable("T8", "Table 8", 6, "2F"),
    OrderTable("T9", "Table 9", 2, "2F"),
    OrderTable("T10", "Table 10", 4, "2F"),
    OrderTable("T11", "Table 11", 6, "2F"),
    OrderTable("T12", "Table 12", 8, "2F"),
    OrderTable("BAR1", "Bar 1", 1, "bar"),
    OrderTable("BAR2", "Bar 2", 1, "bar"),
    OrderTable("BAR3", "Bar 3", 1, "bar"),
)

internal val CheckNumbers = mapOf(
    "T1" to "Ch. #71",
    "T2" to "Ch. #72",
    "T3" to "Ch. #73",
    "T4" to "Ch. #74",
    "T5" to "Ch. #75",
    "T6" to "Ch. #76",
    "T7" to "Ch. #77",
    "T8" to "Ch. #78",
    "T9" to "Ch. #79",
    "T10" to "Ch. #80",
    "T11" to "Ch. #81",
    "T12" to "Ch. #85",
    "BAR1" to "Ch. #90",
    "BAR2" to "Ch. #91",
    "BAR3" to "Ch. #92",
)

internal val OrderMenuCategories = listOf(
    OrderMenuCategory(
        "hot-foods",
        "Hot Foods",
        listOf(
            OrderSubCategory("dumplings", "Dumplings", listOf(
                OrderMenuItem("pork-dumplings", "Pork Dumplings", 8.0),
                OrderMenuItem("shrimp-dumplings", "Shrimp Dumplings", 9.0),
                OrderMenuItem("vegetable-dumplings", "Vegetable Dumplings", 7.0),
                OrderMenuItem("chicken-dumplings", "Chicken Dumplings", 8.0),
                OrderMenuItem("soup-dumplings", "Soup Dumplings", 10.0),
            )),
            OrderSubCategory("spring-rolls", "Spring Rolls", listOf(
                OrderMenuItem("veggie-spring-roll", "Vegetable Spring Rolls", 6.0),
                OrderMenuItem("pork-spring-roll", "Pork Spring Rolls", 7.0),
                OrderMenuItem("shrimp-spring-roll", "Shrimp Spring Rolls", 8.0),
                OrderMenuItem("crispy-rolls", "Crispy Egg Rolls", 7.0),
            )),
            OrderSubCategory("bao-buns", "Bao Buns", listOf(
                OrderMenuItem("pork-belly-bao", "Pork Belly Bao", 9000.0, CurrencyKind.Domestic),
                OrderMenuItem("chicken-bao", "Chicken Bao", 8000.0, CurrencyKind.Domestic),
                OrderMenuItem("veggie-bao", "Vegetable Bao", 7000.0, CurrencyKind.Domestic),
                OrderMenuItem("duck-bao", "Duck Bao", 10000.0, CurrencyKind.Domestic),
            )),
            OrderSubCategory("hot-soups", "Hot Soups", listOf(
                OrderMenuItem("miso-soup", "Miso Soup", 5000.0, CurrencyKind.Domestic),
                OrderMenuItem("hot-sour-soup", "Hot & Sour Soup", 6.0),
                OrderMenuItem("wonton-soup", "Wonton Soup", 7.0),
                OrderMenuItem("ramen", "Ramen", 12000.0, CurrencyKind.Domestic),
                OrderMenuItem("pho", "Pho", 13.0),
            )),
            OrderSubCategory("hot-appetizers", "Hot Appetizers", listOf(
                OrderMenuItem("edamame", "Edamame", 5.0),
                OrderMenuItem("gyoza", "Gyoza", 8000.0, CurrencyKind.Domestic),
                OrderMenuItem("takoyaki", "Takoyaki", 9000.0, CurrencyKind.Domestic),
                OrderMenuItem("tempura", "Tempura", 10.0),
            )),
        ),
    ),
    OrderMenuCategory(
        "cold-foods",
        "Cold Foods",
        listOf(
            OrderSubCategory("sushi-sashimi", "Sushi & Sashimi", listOf(
                OrderMenuItem("salmon-sushi", "Salmon Sushi", 8.0),
                OrderMenuItem("tuna-sushi", "Tuna Sushi", 9.0),
                OrderMenuItem("california-roll", "California Roll", 10.0),
                OrderMenuItem("spicy-tuna-roll", "Spicy Tuna Roll", 11.0),
                OrderMenuItem("sashimi-platter", "Sashimi Platter", 20.0),
            )),
            OrderSubCategory("cold-appetizers", "Cold Appetizers", listOf(
                OrderMenuItem("seaweed-salad", "Seaweed Salad", 6.0),
                OrderMenuItem("kimchi", "Kimchi", 5000.0, CurrencyKind.Domestic),
                OrderMenuItem("pickled-vegetables", "Pickled Vegetables", 5000.0, CurrencyKind.Domestic),
            )),
            OrderSubCategory("salads", "Salads", listOf(
                OrderMenuItem("asian-chicken-salad", "Asian Chicken Salad", 10.0),
                OrderMenuItem("cucumber-salad", "Cucumber Salad", 6.0),
                OrderMenuItem("papaya-salad", "Papaya Salad", 8.0),
            )),
            OrderSubCategory("cold-noodles", "Cold Noodles", listOf(
                OrderMenuItem("soba-noodles", "Cold Soba Noodles", 9.0),
                OrderMenuItem("sesame-noodles", "Sesame Noodles", 8.0),
            )),
        ),
    ),
    OrderMenuCategory(
        "main-meal",
        "Main Meal",
        listOf(
            OrderSubCategory("rice-dishes", "Rice Dishes", listOf(
                OrderMenuItem("fried-rice", "Fried Rice", 12.0),
                OrderMenuItem("bibimbap", "Bibimbap", 14000.0, CurrencyKind.Domestic),
                OrderMenuItem("curry-rice", "Curry Rice", 13.0),
                OrderMenuItem("donburi", "Chicken Donburi", 14000.0, CurrencyKind.Domestic),
            )),
            OrderSubCategory("noodle-dishes", "Noodle Dishes", listOf(
                OrderMenuItem("pad-thai", "Pad Thai", 13.0),
                OrderMenuItem("chow-mein", "Chow Mein", 12.0),
                OrderMenuItem("lo-mein", "Lo Mein", 12.0),
                OrderMenuItem("udon", "Udon Noodles", 13000.0, CurrencyKind.Domestic),
                OrderMenuItem("dan-dan-noodles", "Dan Dan Noodles", 11.0),
            )),
            OrderSubCategory("stir-fry", "Stir Fry", listOf(
                OrderMenuItem("kung-pao-chicken", "Kung Pao Chicken", 15.0),
                OrderMenuItem("mongolian-beef", "Mongolian Beef", 16.0),
                OrderMenuItem("cashew-chicken", "Cashew Chicken", 15.0),
                OrderMenuItem("mixed-vegetables", "Mixed Vegetables", 12.0),
            )),
            OrderSubCategory("curry", "Curry", listOf(
                OrderMenuItem("thai-green-curry", "Thai Green Curry", 14.0),
                OrderMenuItem("thai-red-curry", "Thai Red Curry", 14.0),
                OrderMenuItem("japanese-curry", "Japanese Curry", 13.0),
                OrderMenuItem("massaman-curry", "Massaman Curry", 15.0),
            )),
            OrderSubCategory("grilled-bbq", "Grilled & BBQ", listOf(
                OrderMenuItem("teriyaki-chicken", "Teriyaki Chicken", 16000.0, CurrencyKind.Domestic),
                OrderMenuItem("bulgogi", "Bulgogi", 18000.0, CurrencyKind.Domestic),
                OrderMenuItem("grilled-salmon", "Grilled Salmon", 20.0),
                OrderMenuItem("yakitori", "Yakitori", 12000.0, CurrencyKind.Domestic),
            )),
        ),
    ),
    OrderMenuCategory(
        "drinks",
        "Drinks",
        listOf(
            OrderSubCategory("tea", "Tea", listOf(
                OrderMenuItem("green-tea", "Green Tea", 3000.0, CurrencyKind.Domestic),
                OrderMenuItem("jasmine-tea", "Jasmine Tea", 3000.0, CurrencyKind.Domestic),
                OrderMenuItem("oolong-tea", "Oolong Tea", 3500.0, CurrencyKind.Domestic),
                OrderMenuItem("bubble-tea", "Bubble Tea", 5000.0, CurrencyKind.Domestic),
                OrderMenuItem("thai-tea", "Thai Tea", 4.5),
            )),
            OrderSubCategory("soft-drinks", "Soft Drinks", listOf(
                OrderMenuItem("coke", "Coca-Cola", 3.0),
                OrderMenuItem("sprite", "Sprite", 3.0),
                OrderMenuItem("ginger-ale", "Ginger Ale", 3.0),
            )),
            OrderSubCategory("juice", "Juice", listOf(
                OrderMenuItem("lychee-juice", "Lychee Juice", 4.0),
                OrderMenuItem("mango-juice", "Mango Juice", 4.0),
                OrderMenuItem("coconut-water", "Coconut Water", 4.5),
            )),
            OrderSubCategory("beer", "Beer", listOf(
                OrderMenuItem("asahi", "Asahi", 7.0),
                OrderMenuItem("sapporo", "Sapporo", 7.0),
                OrderMenuItem("singha", "Singha", 6.0),
                OrderMenuItem("tsingtao", "Tsingtao", 6.0),
            )),
            OrderSubCategory("wine", "Wine", listOf(
                OrderMenuItem("plum-wine", "Plum Wine", 8.0),
                OrderMenuItem("red-wine", "Red Wine", 9.0),
                OrderMenuItem("white-wine", "White Wine", 9.0),
            )),
            OrderSubCategory("sake-soju", "Sake & Soju", listOf(
                OrderMenuItem("sake-hot", "Hot Sake", 8000.0, CurrencyKind.Domestic),
                OrderMenuItem("sake-cold", "Cold Sake", 8000.0, CurrencyKind.Domestic),
                OrderMenuItem("soju", "Soju", 7000.0, CurrencyKind.Domestic),
                OrderMenuItem("makgeolli", "Makgeolli", 9000.0, CurrencyKind.Domestic),
            )),
            OrderSubCategory("cocktails", "Cocktails", listOf(
                OrderMenuItem("lychee-martini", "Lychee Martini", 12.0),
                OrderMenuItem("sake-bomb", "Sake Bomb", 10.0),
                OrderMenuItem("mai-tai", "Mai Tai", 11.0),
                OrderMenuItem("singapore-sling", "Singapore Sling", 12.0),
            )),
        ),
    ),
)

internal fun initialOrderLines(): Map<String, List<OrderLine>> = mapOf(
    "T12" to listOf(
        OrderLine("lychee-martini", "lychee-martini", "Lychee Martini", 12.0, 2, "Cocktails", CurrencyKind.Foreign, ordered = true),
        OrderLine("wings", "wings", "Chicken Wings", 12.0, 1, "Appetizers", CurrencyKind.Foreign, ordered = true),
        OrderLine(
            id = "grilled-salmon",
            baseId = "grilled-salmon",
            name = "Grilled Salmon",
            price = 20.0,
            qty = 1,
            category = "Grilled BBQ",
            currency = CurrencyKind.Foreign,
            modifiers = listOf("NO Garlic", "Side Asparagus"),
            ordered = true,
        ),
        OrderLine(
            id = "bulgogi",
            baseId = "bulgogi",
            name = "Bulgogi",
            price = 18000.0,
            qty = 1,
            category = "Grilled BBQ",
            currency = CurrencyKind.Domestic,
            modifiers = listOf("Medium Rare"),
            ordered = true,
        ),
    ),
)

internal val TodayBills = listOf(
    HistoryBill(
        id = "B-1042",
        tableId = "T8",
        time = "08:42",
        krw = 38000.0,
        usd = 0.0,
        method = "Cash",
        lines = listOf(
            HistoryLineItem("Kimchi Fried Rice", 1, 12000.0, 12000.0, CurrencyKind.Domestic),
            HistoryLineItem("Soju", 2, 7000.0, 14000.0, CurrencyKind.Domestic),
            HistoryLineItem("Green Tea", 4, 3000.0, 12000.0, CurrencyKind.Domestic),
        ),
    ),
    HistoryBill(
        id = "B-1043",
        tableId = "T3",
        time = "09:15",
        krw = 0.0,
        usd = 42.5,
        method = "Credit",
        lines = listOf(
            HistoryLineItem("Cheeseburger", 2, 14.0, 28.0, CurrencyKind.Foreign),
            HistoryLineItem("French Fries", 1, 6.5, 6.5, CurrencyKind.Foreign),
            HistoryLineItem("Cola", 2, 4.0, 8.0, CurrencyKind.Foreign),
        ),
    ),
    HistoryBill(
        id = "B-1044",
        tableId = "T6",
        time = "10:03",
        krw = 124000.0,
        usd = 18.0,
        method = "Mix",
        lines = listOf(
            HistoryLineItem("Bibimbap", 2, 14000.0, 28000.0, CurrencyKind.Domestic),
            HistoryLineItem("Pad Thai", 2, 13.0, 26.0, CurrencyKind.Foreign),
            HistoryLineItem("Soju", 4, 7000.0, 28000.0, CurrencyKind.Domestic),
            HistoryLineItem("Asahi", 2, 7.0, 14.0, CurrencyKind.Foreign),
        ),
    ),
    HistoryBill(
        id = "B-1045",
        tableId = "BAR1",
        time = "10:51",
        krw = 22000.0,
        usd = 0.0,
        method = "Cash",
        lines = listOf(
            HistoryLineItem("Hot Sake", 2, 8000.0, 16000.0, CurrencyKind.Domestic),
            HistoryLineItem("Jasmine Tea", 2, 3000.0, 6000.0, CurrencyKind.Domestic),
        ),
    ),
    HistoryBill(
        id = "B-1046",
        tableId = "T1",
        time = "11:28",
        krw = 0.0,
        usd = 96.75,
        method = "Credit",
        lines = listOf(
            HistoryLineItem("Sashimi Platter", 2, 20.0, 40.0, CurrencyKind.Foreign),
            HistoryLineItem("Thai Green Curry", 2, 14.0, 28.0, CurrencyKind.Foreign),
            HistoryLineItem("Red Wine", 3, 9.0, 27.0, CurrencyKind.Foreign),
        ),
    ),
    HistoryBill(
        id = "B-1047",
        tableId = "T11",
        time = "12:09",
        krw = 64500.0,
        usd = 0.0,
        method = "Cash",
        lines = listOf(
            HistoryLineItem("Bulgogi", 2, 18000.0, 36000.0, CurrencyKind.Domestic),
            HistoryLineItem("Jasmine Tea", 3, 3000.0, 9000.0, CurrencyKind.Domestic),
            HistoryLineItem("Gyoza", 2, 8000.0, 16000.0, CurrencyKind.Domestic),
        ),
    ),
    HistoryBill(
        id = "B-1048",
        tableId = "T4",
        time = "12:47",
        krw = 88000.0,
        usd = 24.0,
        method = "Mix",
        lines = listOf(
            HistoryLineItem("Teriyaki Chicken", 2, 16000.0, 32000.0, CurrencyKind.Domestic),
            HistoryLineItem("Grilled Salmon", 1, 20.0, 20.0, CurrencyKind.Foreign),
            HistoryLineItem("Bibimbap", 2, 14000.0, 28000.0, CurrencyKind.Domestic),
            HistoryLineItem("Tsingtao", 1, 6.0, 6.0, CurrencyKind.Foreign),
        ),
    ),
)
