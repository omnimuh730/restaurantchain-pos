package com.mh.restaurantchainpos.pos.ui.auth

internal data class SignUpRestaurant(val id: String, val name: String, val approved: Boolean)

internal val Restaurants = listOf(
    SignUpRestaurant("r1", "Glass Onion", true),
    SignUpRestaurant("r2", "The Blue Lotus", true),
    SignUpRestaurant("r3", "Sakura Garden", true),
    SignUpRestaurant("r4", "Dragon Pearl", false),
    SignUpRestaurant("r5", "Bamboo House", true),
)

internal enum class SignUpMode { Select, Restaurant, Staff }
internal enum class StaffStep { SelectRestaurant, WaitingRestaurantApproval, Details, WaitingStaffApproval }
internal enum class RestaurantStep { Form, WaitingApproval }
