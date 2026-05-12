package com.mh.restaurantchainpos.pos.ui.orders

import com.mh.restaurantchainpos.pos.data.CurrencyKind

internal val OrderFloors = listOf(
    OrderFloor("1F"),
    OrderFloor("2F"),
    OrderFloor("bar"),
)

internal val OrderTables = listOf(
    OrderTable("T1", 2, "1F"),
    OrderTable("T2", 4, "1F"),
    OrderTable("T3", 4, "1F"),
    OrderTable("T4", 6, "1F"),
    OrderTable("T5", 2, "1F"),
    OrderTable("T6", 8, "2F"),
    OrderTable("T7", 4, "2F"),
    OrderTable("T8", 6, "2F"),
    OrderTable("T9", 2, "2F"),
    OrderTable("T10", 4, "2F"),
    OrderTable("T11", 6, "2F"),
    OrderTable("T12", 8, "2F"),
    OrderTable("BAR1", 1, "bar"),
    OrderTable("BAR2", 1, "bar"),
    OrderTable("BAR3", 1, "bar"),
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
        listOf(
            OrderSubCategory(
                "dumplings",
                listOf(
                    OrderMenuItem("pork-dumplings", 8.0),
                    OrderMenuItem("shrimp-dumplings", 9.0),
                    OrderMenuItem("vegetable-dumplings", 7.0),
                    OrderMenuItem("chicken-dumplings", 8.0),
                    OrderMenuItem("soup-dumplings", 10.0),
                ),
            ),
            OrderSubCategory(
                "spring-rolls",
                listOf(
                    OrderMenuItem("veggie-spring-roll", 6.0),
                    OrderMenuItem("pork-spring-roll", 7.0),
                    OrderMenuItem("shrimp-spring-roll", 8.0),
                    OrderMenuItem("crispy-rolls", 7.0),
                ),
            ),
            OrderSubCategory(
                "bao-buns",
                listOf(
                    OrderMenuItem("pork-belly-bao", 9000.0, CurrencyKind.Domestic),
                    OrderMenuItem("chicken-bao", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("veggie-bao", 7000.0, CurrencyKind.Domestic),
                    OrderMenuItem("duck-bao", 10000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "hot-soups",
                listOf(
                    OrderMenuItem("miso-soup", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("hot-sour-soup", 6.0),
                    OrderMenuItem("wonton-soup", 7.0),
                    OrderMenuItem("ramen", 12000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pho", 13.0),
                ),
            ),
            OrderSubCategory(
                "hot-appetizers",
                listOf(
                    OrderMenuItem("wings", 12.0),
                    OrderMenuItem("edamame", 5.0),
                    OrderMenuItem("gyoza", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("takoyaki", 9000.0, CurrencyKind.Domestic),
                    OrderMenuItem("tempura", 10.0),
                ),
            ),
            OrderSubCategory(
                "hot-wok-noodles",
                listOf(
                    OrderMenuItem("ramen", 12000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pho", 13.0),
                    OrderMenuItem("udon", 13000.0, CurrencyKind.Domestic),
                    OrderMenuItem("dan-dan-noodles", 11.0),
                ),
            ),
            OrderSubCategory(
                "hot-comfort-soups",
                listOf(
                    OrderMenuItem("miso-soup", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("wonton-soup", 7.0),
                    OrderMenuItem("hot-sour-soup", 6.0),
                    OrderMenuItem("tom-yum-soup", 12.0),
                ),
            ),
            OrderSubCategory(
                "hot-munchies",
                listOf(
                    OrderMenuItem("soup-dumplings", 10.0),
                    OrderMenuItem("pork-spring-roll", 7.0),
                    OrderMenuItem("shrimp-spring-roll", 8.0),
                    OrderMenuItem("crispy-rolls", 7.0),
                ),
            ),
            OrderSubCategory(
                "hot-rice-bowls",
                listOf(
                    OrderMenuItem("fried-rice", 12.0),
                    OrderMenuItem("bibimbap", 14000.0, CurrencyKind.Domestic),
                    OrderMenuItem("curry-rice", 13.0),
                    OrderMenuItem("donburi", 14000.0, CurrencyKind.Domestic),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "cold-foods",
        listOf(
            OrderSubCategory(
                "sushi-sashimi",
                listOf(
                    OrderMenuItem("salmon-sushi", 8.0),
                    OrderMenuItem("tuna-sushi", 9.0),
                    OrderMenuItem("california-roll", 10.0),
                    OrderMenuItem("spicy-tuna-roll", 11.0),
                    OrderMenuItem("sashimi-platter", 20.0),
                ),
            ),
            OrderSubCategory(
                "cold-appetizers",
                listOf(
                    OrderMenuItem("seaweed-salad", 6.0),
                    OrderMenuItem("kimchi", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pickled-vegetables", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("veggie-spring-roll", 6.0),
                ),
            ),
            OrderSubCategory(
                "salads",
                listOf(
                    OrderMenuItem("asian-chicken-salad", 10.0),
                    OrderMenuItem("cucumber-salad", 6.0),
                    OrderMenuItem("papaya-salad", 8.0),
                ),
            ),
            OrderSubCategory(
                "cold-noodles",
                listOf(
                    OrderMenuItem("soba-noodles", 9.0),
                    OrderMenuItem("sesame-noodles", 8.0),
                    OrderMenuItem("ramen", 12000.0, CurrencyKind.Domestic),
                    OrderMenuItem("udon", 13000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "cold-dimsum",
                listOf(
                    OrderMenuItem("vegetable-dumplings", 7.0),
                    OrderMenuItem("chicken-dumplings", 8.0),
                    OrderMenuItem("soup-dumplings", 10.0),
                    OrderMenuItem("shrimp-dumplings", 9.0),
                ),
            ),
            OrderSubCategory(
                "cold-refreshers",
                listOf(
                    OrderMenuItem("thai-tea", 4.5),
                    OrderMenuItem("bubble-tea", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("coconut-water", 4.5),
                    OrderMenuItem("lychee-juice", 4.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "main-meal",
        listOf(
            OrderSubCategory(
                "rice-dishes",
                listOf(
                    OrderMenuItem("fried-rice", 12.0),
                    OrderMenuItem("bibimbap", 14000.0, CurrencyKind.Domestic),
                    OrderMenuItem("curry-rice", 13.0),
                    OrderMenuItem("donburi", 14000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "noodle-dishes",
                listOf(
                    OrderMenuItem("pad-thai", 13.0),
                    OrderMenuItem("chow-mein", 12.0),
                    OrderMenuItem("lo-mein", 12.0),
                    OrderMenuItem("udon", 13000.0, CurrencyKind.Domestic),
                    OrderMenuItem("dan-dan-noodles", 11.0),
                ),
            ),
            OrderSubCategory(
                "stir-fry",
                listOf(
                    OrderMenuItem("kung-pao-chicken", 15.0),
                    OrderMenuItem("mongolian-beef", 16.0),
                    OrderMenuItem("cashew-chicken", 15.0),
                    OrderMenuItem("mixed-vegetables", 12.0),
                ),
            ),
            OrderSubCategory(
                "curry",
                listOf(
                    OrderMenuItem("thai-green-curry", 14.0),
                    OrderMenuItem("thai-red-curry", 14.0),
                    OrderMenuItem("japanese-curry", 13.0),
                    OrderMenuItem("massaman-curry", 15.0),
                ),
            ),
            OrderSubCategory(
                "grilled-bbq",
                listOf(
                    OrderMenuItem("teriyaki-chicken", 16000.0, CurrencyKind.Domestic),
                    OrderMenuItem("bulgogi", 18000.0, CurrencyKind.Domestic),
                    OrderMenuItem("grilled-salmon", 20.0),
                    OrderMenuItem("yakitori", 12000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "main-soup-line",
                listOf(
                    OrderMenuItem("miso-soup", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("wonton-soup", 7.0),
                    OrderMenuItem("hot-sour-soup", 6.0),
                    OrderMenuItem("tom-yum-soup", 12.0),
                ),
            ),
            OrderSubCategory(
                "main-dim-sum",
                listOf(
                    OrderMenuItem("pork-dumplings", 8.0),
                    OrderMenuItem("vegetable-dumplings", 7.0),
                    OrderMenuItem("gyoza", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("chicken-dumplings", 8.0),
                ),
            ),
            OrderSubCategory(
                "main-quick-bites",
                listOf(
                    OrderMenuItem("cheeseburger", 14.0),
                    OrderMenuItem("french-fries", 6.5),
                    OrderMenuItem("coke", 3.0),
                    OrderMenuItem("sprite", 3.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "drinks",
        listOf(
            OrderSubCategory(
                "tea",
                listOf(
                    OrderMenuItem("green-tea", 3000.0, CurrencyKind.Domestic),
                    OrderMenuItem("jasmine-tea", 3000.0, CurrencyKind.Domestic),
                    OrderMenuItem("oolong-tea", 3500.0, CurrencyKind.Domestic),
                    OrderMenuItem("bubble-tea", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("thai-tea", 4.5),
                ),
            ),
            OrderSubCategory(
                "soft-drinks",
                listOf(
                    OrderMenuItem("coke", 3.0),
                    OrderMenuItem("sprite", 3.0),
                    OrderMenuItem("ginger-ale", 3.0),
                    OrderMenuItem("coconut-water", 4.5),
                ),
            ),
            OrderSubCategory(
                "juice",
                listOf(
                    OrderMenuItem("lychee-juice", 4.0),
                    OrderMenuItem("mango-juice", 4.0),
                    OrderMenuItem("coconut-water", 4.5),
                ),
            ),
            OrderSubCategory(
                "beer",
                listOf(
                    OrderMenuItem("asahi", 7.0),
                    OrderMenuItem("sapporo", 7.0),
                    OrderMenuItem("singha", 6.0),
                    OrderMenuItem("tsingtao", 6.0),
                ),
            ),
            OrderSubCategory(
                "wine",
                listOf(
                    OrderMenuItem("plum-wine", 8.0),
                    OrderMenuItem("red-wine", 9.0),
                    OrderMenuItem("white-wine", 9.0),
                ),
            ),
            OrderSubCategory(
                "sake-soju",
                listOf(
                    OrderMenuItem("sake-hot", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("sake-cold", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("soju", 7000.0, CurrencyKind.Domestic),
                    OrderMenuItem("makgeolli", 9000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "cocktails",
                listOf(
                    OrderMenuItem("lychee-martini", 12.0),
                    OrderMenuItem("sake-bomb", 10.0),
                    OrderMenuItem("mai-tai", 11.0),
                    OrderMenuItem("singapore-sling", 12.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "coffee-bar",
        listOf(
            OrderSubCategory(
                "cb-espresso",
                listOf(
                    OrderMenuItem("americano", 4500.0, CurrencyKind.Domestic),
                    OrderMenuItem("cafe-latte", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("flat-white", 5200.0, CurrencyKind.Domestic),
                    OrderMenuItem("espresso-con-panna", 4800.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "cb-cold-brew",
                listOf(
                    OrderMenuItem("honey-cold-brew", 5500.0, CurrencyKind.Domestic),
                    OrderMenuItem("oat-cold-brew", 5500.0, CurrencyKind.Domestic),
                    OrderMenuItem("vanilla-cold-brew", 5500.0, CurrencyKind.Domestic),
                    OrderMenuItem("bubble-tea", 5000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "cb-milk-tea",
                listOf(
                    OrderMenuItem("thai-tea", 4.5),
                    OrderMenuItem("jasmine-tea", 3000.0, CurrencyKind.Domestic),
                    OrderMenuItem("oolong-tea", 3500.0, CurrencyKind.Domestic),
                    OrderMenuItem("green-tea", 3000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "cb-soda-mixer",
                listOf(
                    OrderMenuItem("coke", 3.0),
                    OrderMenuItem("sprite", 3.0),
                    OrderMenuItem("ginger-ale", 3.0),
                    OrderMenuItem("coconut-water", 4.5),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "desserts-bakery",
        listOf(
            OrderSubCategory(
                "db-cakes",
                listOf(
                    OrderMenuItem("tiramisu", 7.0),
                    OrderMenuItem("white-rice-cake", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("bubble-tea", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("thai-tea", 4.5),
                ),
            ),
            OrderSubCategory(
                "db-frozen",
                listOf(
                    OrderMenuItem("mango-juice", 4.0),
                    OrderMenuItem("lychee-juice", 4.0),
                    OrderMenuItem("coconut-water", 4.5),
                    OrderMenuItem("vanilla-cold-brew", 5500.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "db-bakery",
                listOf(
                    OrderMenuItem("pork-belly-bao", 9000.0, CurrencyKind.Domestic),
                    OrderMenuItem("chicken-bao", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("veggie-bao", 7000.0, CurrencyKind.Domestic),
                    OrderMenuItem("duck-bao", 10000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "db-pairing",
                listOf(
                    OrderMenuItem("plum-wine", 8.0),
                    OrderMenuItem("red-wine", 9.0),
                    OrderMenuItem("white-wine", 9.0),
                    OrderMenuItem("hot-sour-soup", 6.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "breakfast-brunch",
        listOf(
            OrderSubCategory(
                "bf-classics",
                listOf(
                    OrderMenuItem("bibimbap", 14000.0, CurrencyKind.Domestic),
                    OrderMenuItem("donburi", 14000.0, CurrencyKind.Domestic),
                    OrderMenuItem("fried-rice", 12.0),
                    OrderMenuItem("steamed-rice", 2000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "bf-soups",
                listOf(
                    OrderMenuItem("miso-soup", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("wonton-soup", 7.0),
                    OrderMenuItem("pho", 13.0),
                    OrderMenuItem("tom-yum-soup", 12.0),
                ),
            ),
            OrderSubCategory(
                "bf-breads",
                listOf(
                    OrderMenuItem("pork-belly-bao", 9000.0, CurrencyKind.Domestic),
                    OrderMenuItem("chicken-bao", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("veggie-bao", 7000.0, CurrencyKind.Domestic),
                    OrderMenuItem("duck-bao", 10000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "bf-cafe",
                listOf(
                    OrderMenuItem("americano", 4500.0, CurrencyKind.Domestic),
                    OrderMenuItem("cafe-latte", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("jasmine-tea", 3000.0, CurrencyKind.Domestic),
                    OrderMenuItem("green-tea", 3000.0, CurrencyKind.Domestic),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "vegetarian-vegan",
        listOf(
            OrderSubCategory(
                "vg-bowls",
                listOf(
                    OrderMenuItem("vegetable-dumplings", 7.0),
                    OrderMenuItem("mixed-vegetables", 12.0),
                    OrderMenuItem("seaweed-salad", 6.0),
                    OrderMenuItem("cucumber-salad", 6.0),
                ),
            ),
            OrderSubCategory(
                "vg-plates",
                listOf(
                    OrderMenuItem("miso-soup", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("kimchi", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pickled-vegetables", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("papaya-salad", 8.0),
                ),
            ),
            OrderSubCategory(
                "vg-noodles",
                listOf(
                    OrderMenuItem("sesame-noodles", 8.0),
                    OrderMenuItem("soba-noodles", 9.0),
                    OrderMenuItem("ramen", 12000.0, CurrencyKind.Domestic),
                    OrderMenuItem("udon", 13000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "vg-drinks",
                listOf(
                    OrderMenuItem("green-tea", 3000.0, CurrencyKind.Domestic),
                    OrderMenuItem("coconut-water", 4.5),
                    OrderMenuItem("mango-juice", 4.0),
                    OrderMenuItem("lychee-juice", 4.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "seafood-grill",
        listOf(
            OrderSubCategory(
                "sf-ocean",
                listOf(
                    OrderMenuItem("grilled-salmon", 20.0),
                    OrderMenuItem("salmon-sushi", 8.0),
                    OrderMenuItem("tuna-sushi", 9.0),
                    OrderMenuItem("sashimi-platter", 20.0),
                ),
            ),
            OrderSubCategory(
                "sf-shell",
                listOf(
                    OrderMenuItem("shrimp-dumplings", 9.0),
                    OrderMenuItem("shrimp-spring-roll", 8.0),
                    OrderMenuItem("seafood-pasta", 16.0),
                    OrderMenuItem("pho", 13.0),
                ),
            ),
            OrderSubCategory(
                "sf-grill",
                listOf(
                    OrderMenuItem("teriyaki-chicken", 16000.0, CurrencyKind.Domestic),
                    OrderMenuItem("yakitori", 12000.0, CurrencyKind.Domestic),
                    OrderMenuItem("bulgogi", 18000.0, CurrencyKind.Domestic),
                    OrderMenuItem("tempura", 10.0),
                ),
            ),
            OrderSubCategory(
                "sf-sides",
                listOf(
                    OrderMenuItem("seaweed-salad", 6.0),
                    OrderMenuItem("miso-soup", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("edamame", 5.0),
                    OrderMenuItem("cucumber-salad", 6.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "western-specials",
        listOf(
            OrderSubCategory(
                "ws-steaks",
                listOf(
                    OrderMenuItem("t-bone-steak", 42.0),
                    OrderMenuItem("sirloin-steak", 36.0),
                    OrderMenuItem("mongolian-beef", 16.0),
                    OrderMenuItem("kung-pao-chicken", 15.0),
                ),
            ),
            OrderSubCategory(
                "ws-pasta",
                listOf(
                    OrderMenuItem("seafood-pasta", 16.0),
                    OrderMenuItem("tom-yum-soup", 12.0),
                    OrderMenuItem("japanese-curry", 13.0),
                    OrderMenuItem("pad-thai", 13.0),
                ),
            ),
            OrderSubCategory(
                "ws-casual",
                listOf(
                    OrderMenuItem("cheeseburger", 14.0),
                    OrderMenuItem("french-fries", 6.5),
                    OrderMenuItem("coke", 3.0),
                    OrderMenuItem("sprite", 3.0),
                ),
            ),
            OrderSubCategory(
                "ws-sides",
                listOf(
                    OrderMenuItem("steamed-rice", 2000.0, CurrencyKind.Domestic),
                    OrderMenuItem("kimchi", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pickled-vegetables", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("cucumber-salad", 6.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "kids-menu",
        listOf(
            OrderSubCategory(
                "kd-mains",
                listOf(
                    OrderMenuItem("chicken-dumplings", 8.0),
                    OrderMenuItem("chicken-bao", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("fried-rice", 12.0),
                    OrderMenuItem("teriyaki-chicken", 16000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "kd-sides",
                listOf(
                    OrderMenuItem("edamame", 5.0),
                    OrderMenuItem("veggie-spring-roll", 6.0),
                    OrderMenuItem("french-fries", 6.5),
                    OrderMenuItem("steamed-rice", 2000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "kd-drinks",
                listOf(
                    OrderMenuItem("coke", 3.0),
                    OrderMenuItem("sprite", 3.0),
                    OrderMenuItem("mango-juice", 4.0),
                    OrderMenuItem("lychee-juice", 4.0),
                ),
            ),
            OrderSubCategory(
                "kd-treats",
                listOf(
                    OrderMenuItem("bubble-tea", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("tiramisu", 7.0),
                    OrderMenuItem("california-roll", 10.0),
                    OrderMenuItem("gyoza", 8000.0, CurrencyKind.Domestic),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "sides-extras",
        listOf(
            OrderSubCategory(
                "sd-grains",
                listOf(
                    OrderMenuItem("steamed-rice", 2000.0, CurrencyKind.Domestic),
                    OrderMenuItem("fried-rice", 12.0),
                    OrderMenuItem("lo-mein", 12.0),
                    OrderMenuItem("chow-mein", 12.0),
                ),
            ),
            OrderSubCategory(
                "sd-pickles",
                listOf(
                    OrderMenuItem("kimchi", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pickled-vegetables", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("seaweed-salad", 6.0),
                    OrderMenuItem("papaya-salad", 8.0),
                ),
            ),
            OrderSubCategory(
                "sd-dimsum",
                listOf(
                    OrderMenuItem("pork-dumplings", 8.0),
                    OrderMenuItem("gyoza", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("veggie-spring-roll", 6.0),
                    OrderMenuItem("crispy-rolls", 7.0),
                ),
            ),
            OrderSubCategory(
                "sd-quench",
                listOf(
                    OrderMenuItem("oolong-tea", 3500.0, CurrencyKind.Domestic),
                    OrderMenuItem("jasmine-tea", 3000.0, CurrencyKind.Domestic),
                    OrderMenuItem("green-tea", 3000.0, CurrencyKind.Domestic),
                    OrderMenuItem("thai-tea", 4.5),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "late-night",
        listOf(
            OrderSubCategory(
                "ln-crispy",
                listOf(
                    OrderMenuItem("tempura", 10.0),
                    OrderMenuItem("takoyaki", 9000.0, CurrencyKind.Domestic),
                    OrderMenuItem("gyoza", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("chicken-dumplings", 8.0),
                ),
            ),
            OrderSubCategory(
                "ln-noodles",
                listOf(
                    OrderMenuItem("ramen", 12000.0, CurrencyKind.Domestic),
                    OrderMenuItem("udon", 13000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pho", 13.0),
                    OrderMenuItem("dan-dan-noodles", 11.0),
                ),
            ),
            OrderSubCategory(
                "ln-rice",
                listOf(
                    OrderMenuItem("bibimbap", 14000.0, CurrencyKind.Domestic),
                    OrderMenuItem("curry-rice", 13.0),
                    OrderMenuItem("fried-rice", 12.0),
                    OrderMenuItem("donburi", 14000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "ln-bar",
                listOf(
                    OrderMenuItem("asahi", 7.0),
                    OrderMenuItem("soju", 7000.0, CurrencyKind.Domestic),
                    OrderMenuItem("mai-tai", 11.0),
                    OrderMenuItem("lychee-martini", 12.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "lunch-combos",
        listOf(
            OrderSubCategory(
                "lc-hot",
                listOf(
                    OrderMenuItem("pad-thai", 13.0),
                    OrderMenuItem("kung-pao-chicken", 15.0),
                    OrderMenuItem("pork-spring-roll", 7.0),
                    OrderMenuItem("green-tea", 3000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "lc-grill",
                listOf(
                    OrderMenuItem("bulgogi", 18000.0, CurrencyKind.Domestic),
                    OrderMenuItem("teriyaki-chicken", 16000.0, CurrencyKind.Domestic),
                    OrderMenuItem("grilled-salmon", 20.0),
                    OrderMenuItem("jasmine-tea", 3000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "lc-curry",
                listOf(
                    OrderMenuItem("thai-green-curry", 14.0),
                    OrderMenuItem("massaman-curry", 15.0),
                    OrderMenuItem("japanese-curry", 13.0),
                    OrderMenuItem("steamed-rice", 2000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "lc-light",
                listOf(
                    OrderMenuItem("sashimi-platter", 20.0),
                    OrderMenuItem("california-roll", 10.0),
                    OrderMenuItem("miso-soup", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("cucumber-salad", 6.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "happy-hour",
        listOf(
            OrderSubCategory(
                "hh-beer",
                listOf(
                    OrderMenuItem("asahi", 7.0),
                    OrderMenuItem("sapporo", 7.0),
                    OrderMenuItem("singha", 6.0),
                    OrderMenuItem("tsingtao", 6.0),
                ),
            ),
            OrderSubCategory(
                "hh-shaken",
                listOf(
                    OrderMenuItem("lychee-martini", 12.0),
                    OrderMenuItem("sake-bomb", 10.0),
                    OrderMenuItem("mai-tai", 11.0),
                    OrderMenuItem("singapore-sling", 12.0),
                ),
            ),
            OrderSubCategory(
                "hh-wine",
                listOf(
                    OrderMenuItem("plum-wine", 8.0),
                    OrderMenuItem("red-wine", 9.0),
                    OrderMenuItem("white-wine", 9.0),
                    OrderMenuItem("sake-hot", 8000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "hh-bites",
                listOf(
                    OrderMenuItem("edamame", 5.0),
                    OrderMenuItem("gyoza", 8000.0, CurrencyKind.Domestic),
                    OrderMenuItem("wings", 12.0),
                    OrderMenuItem("tempura", 10.0),
                ),
            ),
        ),
    ),
    OrderMenuCategory(
        "retail-pantry",
        listOf(
            OrderSubCategory(
                "rt-noodles",
                listOf(
                    OrderMenuItem("ramen", 12000.0, CurrencyKind.Domestic),
                    OrderMenuItem("udon", 13000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pho", 13.0),
                    OrderMenuItem("soba-noodles", 9.0),
                ),
            ),
            OrderSubCategory(
                "rt-grains",
                listOf(
                    OrderMenuItem("steamed-rice", 2000.0, CurrencyKind.Domestic),
                    OrderMenuItem("white-rice-cake", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("kimchi", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("pickled-vegetables", 5000.0, CurrencyKind.Domestic),
                ),
            ),
            OrderSubCategory(
                "rt-bev",
                listOf(
                    OrderMenuItem("coke", 3.0),
                    OrderMenuItem("sprite", 3.0),
                    OrderMenuItem("green-tea", 3000.0, CurrencyKind.Domestic),
                    OrderMenuItem("coconut-water", 4.5),
                ),
            ),
            OrderSubCategory(
                "rt-sweets",
                listOf(
                    OrderMenuItem("tiramisu", 7.0),
                    OrderMenuItem("bubble-tea", 5000.0, CurrencyKind.Domestic),
                    OrderMenuItem("flat-white", 5200.0, CurrencyKind.Domestic),
                    OrderMenuItem("americano", 4500.0, CurrencyKind.Domestic),
                ),
            ),
        ),
    ),
)

internal fun initialOrderLines(): Map<String, List<OrderLine>> = mapOf(
    "T12" to listOf(
        OrderLine("lychee-martini", "lychee-martini", 12.0, 2, "cocktails", CurrencyKind.Foreign, ordered = true),
        OrderLine("wings", "wings", 12.0, 1, "hot-appetizers", CurrencyKind.Foreign, ordered = true),
        OrderLine(
            id = "grilled-salmon",
            baseId = "grilled-salmon",
            price = 20.0,
            qty = 1,
            categoryId = "grilled-bbq",
            currency = CurrencyKind.Foreign,
            modifiers = listOf("NO Garlic", "Side Asparagus"),
            ordered = true,
        ),
        OrderLine(
            id = "bulgogi",
            baseId = "bulgogi",
            price = 18000.0,
            qty = 1,
            categoryId = "grilled-bbq",
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
        methodKey = "cash",
        lines = listOf(
            HistoryLineItem("kimchi-fried-rice", 1, 12000.0, 12000.0, CurrencyKind.Domestic),
            HistoryLineItem("soju", 2, 7000.0, 14000.0, CurrencyKind.Domestic),
            HistoryLineItem("green-tea", 4, 3000.0, 12000.0, CurrencyKind.Domestic),
        ),
    ),
    HistoryBill(
        id = "B-1043",
        tableId = "T3",
        time = "09:15",
        krw = 0.0,
        usd = 42.5,
        methodKey = "credit",
        lines = listOf(
            HistoryLineItem("cheeseburger", 2, 14.0, 28.0, CurrencyKind.Foreign),
            HistoryLineItem("french-fries", 1, 6.5, 6.5, CurrencyKind.Foreign),
            HistoryLineItem("coke", 2, 4.0, 8.0, CurrencyKind.Foreign),
        ),
    ),
    HistoryBill(
        id = "B-1044",
        tableId = "T6",
        time = "10:03",
        krw = 124000.0,
        usd = 18.0,
        methodKey = "mix",
        lines = listOf(
            HistoryLineItem("bibimbap", 2, 14000.0, 28000.0, CurrencyKind.Domestic),
            HistoryLineItem("pad-thai", 2, 13.0, 26.0, CurrencyKind.Foreign),
            HistoryLineItem("soju", 4, 7000.0, 28000.0, CurrencyKind.Domestic),
            HistoryLineItem("asahi", 2, 7.0, 14.0, CurrencyKind.Foreign),
        ),
    ),
    HistoryBill(
        id = "B-1045",
        tableId = "BAR1",
        time = "10:51",
        krw = 22000.0,
        usd = 0.0,
        methodKey = "cash",
        lines = listOf(
            HistoryLineItem("sake-hot", 2, 8000.0, 16000.0, CurrencyKind.Domestic),
            HistoryLineItem("jasmine-tea", 2, 3000.0, 6000.0, CurrencyKind.Domestic),
        ),
    ),
    HistoryBill(
        id = "B-1046",
        tableId = "T1",
        time = "11:28",
        krw = 0.0,
        usd = 96.75,
        methodKey = "credit",
        lines = listOf(
            HistoryLineItem("sashimi-platter", 2, 20.0, 40.0, CurrencyKind.Foreign),
            HistoryLineItem("thai-green-curry", 2, 14.0, 28.0, CurrencyKind.Foreign),
            HistoryLineItem("red-wine", 3, 9.0, 27.0, CurrencyKind.Foreign),
        ),
    ),
    HistoryBill(
        id = "B-1047",
        tableId = "T11",
        time = "12:09",
        krw = 64500.0,
        usd = 0.0,
        methodKey = "cash",
        lines = listOf(
            HistoryLineItem("bulgogi", 2, 18000.0, 36000.0, CurrencyKind.Domestic),
            HistoryLineItem("jasmine-tea", 3, 3000.0, 9000.0, CurrencyKind.Domestic),
            HistoryLineItem("gyoza", 2, 8000.0, 16000.0, CurrencyKind.Domestic),
        ),
    ),
    HistoryBill(
        id = "B-1048",
        tableId = "T4",
        time = "12:47",
        krw = 88000.0,
        usd = 24.0,
        methodKey = "mix",
        lines = listOf(
            HistoryLineItem("teriyaki-chicken", 2, 16000.0, 32000.0, CurrencyKind.Domestic),
            HistoryLineItem("grilled-salmon", 1, 20.0, 20.0, CurrencyKind.Foreign),
            HistoryLineItem("bibimbap", 2, 14000.0, 28000.0, CurrencyKind.Domestic),
            HistoryLineItem("tsingtao", 1, 6.0, 6.0, CurrencyKind.Foreign),
        ),
    ),
)
